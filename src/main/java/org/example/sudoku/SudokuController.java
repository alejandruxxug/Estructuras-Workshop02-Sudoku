package org.example.sudoku;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SudokuController {
    @FXML
    private Label statusLabel;

    @FXML
    protected void onSolveClick() {
        statusLabel.setText("TODO: ejecutar backtracking");
    }

    @FXML
    protected void onClearClick() {
        statusLabel.setText("TODO: limpiar tablero");
    }
}
