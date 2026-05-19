package org.example.sudoku.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import org.example.sudoku.overlay.FailureOverlay;
import org.example.sudoku.overlay.OverlayManager;
import org.example.sudoku.overlay.SuccessOverlay;
import org.example.sudoku.sudoku.BacktrackingSolver;
import org.example.sudoku.sudoku.SolveListener;
import org.example.sudoku.sudoku.SudokuBoard;
import org.example.sudoku.sudoku.SudokuGenerator;

import java.util.function.UnaryOperator;

/**
 * Controla el post del Sudoku: cuadrícula 9x9 editable, slider de
 * velocidad y botones Resolver / Limpiar. Lanza el solver en un hilo
 * de fondo y refleja cada paso en la UI mediante {@link SolveListener}.
 */
public class SudokuPostController {

    private static final int SIZE = SudokuBoard.SIZE;

    @FXML private GridPane gridPane;
    @FXML private Slider speedSlider;
    @FXML private Label speedValueLabel;

    private final TextField[][] cells = new TextField[SIZE][SIZE];
    private final boolean[][] userEntered = new boolean[SIZE][SIZE];
    private Task<Boolean> currentTask;
    // Cuando el solver escribe en los TextField su propio listener se dispara.
    // Esta bandera lo silencia para que no marque esas celdas como del usuario
    // (lo que bloquearía las pisadas/desapilados subsecuentes).
    private boolean updatingProgrammatically;

    @FXML
    public void initialize() {
        buildGrid();
        speedSlider.valueProperty().addListener((obs, o, n) -> updateSpeedLabel(n.doubleValue()));
        updateSpeedLabel(speedSlider.getValue());
    }

    public TextField[][] getCells() {
        return cells;
    }

    private void updateSpeedLabel(double v) {
        int ms = (int) v;
        speedValueLabel.setText(ms == 0 ? "tiempo real" : ms + " ms");
    }

    private void buildGrid() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String t = change.getControlNewText();
            if (t.isEmpty()) return change;
            return t.length() == 1 && "123456789".contains(t) ? change : null;
        };

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                TextField tf = new TextField();
                tf.getStyleClass().add("sudoku-cell");
                if ((c + 1) % 3 == 0 && c != SIZE - 1) tf.getStyleClass().add("thick-right");
                if ((r + 1) % 3 == 0 && r != SIZE - 1) tf.getStyleClass().add("thick-bottom");
                tf.setTextFormatter(new TextFormatter<>(filter));
                final int row = r, col = c;
                tf.textProperty().addListener((obs, oldV, newV) -> {
                    if (updatingProgrammatically) return;
                    userEntered[row][col] = newV != null && !newV.isEmpty();
                    if (userEntered[row][col]) {
                        markUserStyle(tf);
                    } else {
                        clearStyle(tf);
                    }
                });
                cells[r][c] = tf;
                gridPane.add(tf, c, r);
            }
        }
    }

    @FXML private void onGenerateEasy()   { generate(48); }
    @FXML private void onGenerateMedium() { generate(34); }
    @FXML private void onGenerateHard()   { generate(24); }

    private void generate(int clues) {
        cancelTask();
        int[][] puzzle = new SudokuGenerator().generate(clues);
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                TextField tf = cells[r][c];
                int v = puzzle[r][c];
                updatingProgrammatically = true;
                try {
                    tf.setText(v == 0 ? "" : Integer.toString(v));
                } finally {
                    updatingProgrammatically = false;
                }
                if (v == 0) {
                    userEntered[r][c] = false;
                    clearStyle(tf);
                } else {
                    userEntered[r][c] = true;
                    markUserStyle(tf);
                }
                tf.setDisable(false);
            }
        }
    }

    @FXML
    private void onClear() {
        cancelTask();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c].setText("");
                userEntered[r][c] = false;
                clearStyle(cells[r][c]);
                cells[r][c].setDisable(false);
            }
        }
    }

    @FXML
    private void onSolve() {
        cancelTask();
        SudokuBoard board = readBoard();
        if (board == null) return;

        // Camino 1: tablero completo -> validar instantáneamente
        if (board.isComplete()) {
            if (board.isInitialStateValid()) {
                showSuccess();
            } else {
                showFailure();
            }
            return;
        }

        // Camino 2: tablero parcial/vacío -> ejecutar solver con visualización
        setEditable(false);
        SolveListener listener = new SolveListener() {
            @Override public void onPlace(int row, int col, int value) {
                Platform.runLater(() -> setSolverCell(row, col, value));
                sleep();
            }
            @Override public void onUnplace(int row, int col) {
                Platform.runLater(() -> setSolverCell(row, col, 0));
                sleep();
            }
        };

        currentTask = new Task<>() {
            @Override protected Boolean call() {
                return new BacktrackingSolver(listener).solve(board);
            }
        };
        currentTask.setOnSucceeded(e -> {
            setEditable(true);
            if (Boolean.TRUE.equals(currentTask.getValue())) {
                showSuccess();
            } else {
                showFailure();
            }
        });
        currentTask.setOnFailed(e -> {
            setEditable(true);
            currentTask.getException().printStackTrace();
        });
        Thread t = new Thread(currentTask, "sudoku-solver");
        t.setDaemon(true);
        t.start();
    }

    private void cancelTask() {
        if (currentTask != null && currentTask.isRunning()) {
            currentTask.cancel(true);
        }
    }

    private SudokuBoard readBoard() {
        int[][] values = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                String txt = cells[r][c].getText();
                values[r][c] = txt == null || txt.isEmpty() ? 0 : Integer.parseInt(txt);
            }
        }
        return new SudokuBoard(values);
    }

    private void setSolverCell(int row, int col, int value) {
        TextField tf = cells[row][col];
        if (userEntered[row][col]) return; // no pisar valores del usuario
        updatingProgrammatically = true;
        try {
            tf.setText(value == 0 ? "" : Integer.toString(value));
        } finally {
            updatingProgrammatically = false;
        }
        if (value == 0) {
            clearStyle(tf);
        } else {
            markSolverStyle(tf);
        }
    }

    private void setEditable(boolean editable) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c].setEditable(editable);
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep((long) speedSlider.getValue());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    private void markUserStyle(TextField tf) {
        if (!tf.getStyleClass().contains("user")) tf.getStyleClass().add("user");
        tf.getStyleClass().remove("solver");
    }
    private void markSolverStyle(TextField tf) {
        if (!tf.getStyleClass().contains("solver")) tf.getStyleClass().add("solver");
        tf.getStyleClass().remove("user");
    }
    private void clearStyle(TextField tf) {
        tf.getStyleClass().remove("user");
        tf.getStyleClass().remove("solver");
    }

    private void showSuccess() {
        var stack = OverlayManager.get().root();
        OverlayManager.get().show(new SuccessOverlay(stack.getWidth(), stack.getHeight()));
    }
    private void showFailure() {
        var stack = OverlayManager.get().root();
        OverlayManager.get().show(new FailureOverlay(stack.getWidth(), stack.getHeight()));
    }
}
