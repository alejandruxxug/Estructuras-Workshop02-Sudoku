package org.example.sudoku;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("sudoku-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 540, 640);
        stage.setTitle("Sudoku 9x9 - Backtracking");
        stage.setScene(scene);
        stage.show();
    }
}
