package org.example.sudoku.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * Controlador del feed principal. Inserta el post interactivo de Sudoku
 * arriba y posts de animatronics de FNAF Sister Location debajo.
 */
public class AppController {

    @FXML private BorderPane rootPane;
    @FXML private VBox feedContainer;

    private SudokuPostController sudokuPostController;

    @FXML
    public void initialize() {
        feedContainer.getChildren().add(loadSudokuPost());

        feedContainer.getChildren().add(AnimatronicPost.build(
                "Circus Baby", "@circus_baby",
                "Si crees que un sudoku es difícil, intenta sostener una cucharita.",
                Color.web("#E0506E")));

        feedContainer.getChildren().add(AnimatronicPost.build(
                "Ballora", "@ballora_ballet",
                "Cada celda vacía es un compás. El backtracking baila hacia atrás cuando se equivoca de paso.",
                Color.web("#7E57C2")));

        feedContainer.getChildren().add(AnimatronicPost.build(
                "Funtime Foxy", "@funtime_foxy",
                "isValidPlacement es solo un eufemismo para 'no te metas en mi función pirata'.",
                Color.web("#EC6FA0")));

        feedContainer.getChildren().add(AnimatronicPost.build(
                "Funtime Freddy", "@bonbon_freddy",
                "Bon-Bon dice que el peor caso es 9^m. Yo digo que el peor caso es estar guardado en una caja.",
                Color.web("#5C8DCB")));

        feedContainer.getChildren().add(AnimatronicPost.build(
                "Ennard", "@ennard_ensamble",
                "Soy varios animatronics resolviendo el mismo sudoku al mismo tiempo. Es como hilos sin sincronización.",
                Color.web("#9E9E9E")));

        feedContainer.getChildren().add(AnimatronicPost.build(
                "Funtime Bonnie", "@bonnie_funtime",
                "Recursión, eso es todo. Llamas, fallas, deshaces, vuelves a llamar. Como los turnos en Sister Location.",
                Color.web("#42A5F5")));
    }

    private Node loadSudokuPost() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/sudoku/sudoku-post.fxml"));
            Node node = loader.load();
            sudokuPostController = loader.getController();
            return node;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar sudoku-post.fxml", e);
        }
    }

    public VBox getFeedContainer() {
        return feedContainer;
    }

    public SudokuPostController getSudokuPostController() {
        return sudokuPostController;
    }
}
