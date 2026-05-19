package org.example.sudoku;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.sudoku.ambiance.Ambiance;
import org.example.sudoku.audio.AmbiancePlayer;
import org.example.sudoku.controllers.AppController;
import org.example.sudoku.controllers.LoadingController;
import org.example.sudoku.fx.GlitchEffect;
import org.example.sudoku.overlay.OverlayManager;

import java.io.IOException;

public class Main extends Application {

    /** Tiempo mínimo que la pantalla de carga permanece visible (ms). */
    private static final long MIN_SPLASH_MS = 1500;

    private StackPane sceneRoot;
    private Scene scene;
    private LoadingController loadingController;
    private FXMLLoader appLoader;

    @Override
    public void start(Stage stage) throws IOException {
        var bounds = Screen.getPrimary().getVisualBounds();

        FXMLLoader loadingLoader = new FXMLLoader(
                Main.class.getResource("loading-view.fxml"));
        Parent loadingRoot = loadingLoader.load();

        sceneRoot = new StackPane(loadingRoot);
        sceneRoot.setStyle("-fx-background-color: #0B0F14;");
        scene = new Scene(sceneRoot, bounds.getWidth(), bounds.getHeight());
        scene.setFill(Color.web("#0B0F14"));
        scene.getStylesheets().add(
                Main.class.getResource("app.css").toExternalForm());

        OverlayManager.init(sceneRoot);

        stage.setTitle("OnlySudoku");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
        stage.show();

        loadingController = loadingLoader.getController();

        long splashStart = System.currentTimeMillis();
        preloadApp(splashStart);
    }

    private void preloadApp(long splashStart) {
        Task<Parent> task = new Task<>() {
            @Override protected Parent call() throws IOException {
                appLoader = new FXMLLoader(Main.class.getResource("app-view.fxml"));
                return appLoader.load();
            }
        };
        task.setOnSucceeded(e -> {
            Parent appRoot = task.getValue();
            long elapsed = System.currentTimeMillis() - splashStart;
            long wait = Math.max(0, MIN_SPLASH_MS - elapsed);
            if (wait == 0) {
                finishSplash(appRoot);
            } else {
                new Thread(() -> {
                    try { Thread.sleep(wait); } catch (InterruptedException ignored) {}
                    Platform.runLater(() -> finishSplash(appRoot));
                }, "splash-min-wait").start();
            }
        });
        task.setOnFailed(e -> task.getException().printStackTrace());
        Thread t = new Thread(task, "app-preload");
        t.setDaemon(true);
        t.start();
    }

    private void finishSplash(Parent appRoot) {
        // El controller solo detiene su pulso y nos avisa; aquí
        // disparamos la transición de glitch que cambia la escena.
        loadingController.setOnFinished(v -> runGlitchTransition(appRoot));
        loadingController.requestFinish();
    }

    private void runGlitchTransition(Parent appRoot) {
        GlitchEffect.transition(sceneRoot, () -> {
            sceneRoot.getChildren().setAll(appRoot);
            startAmbiance();
        });
    }

    private void startAmbiance() {
        AmbiancePlayer.start();
        Ambiance.start(sceneRoot, scene);

        if (appLoader != null) {
            AppController appCtrl = appLoader.getController();
            if (appCtrl != null) {
                Ambiance.registerFeed(appCtrl.getFeedContainer());
                if (appCtrl.getSudokuPostController() != null) {
                    Ambiance.registerCells(appCtrl.getSudokuPostController().getCells());
                }
            }
        }
    }
}
