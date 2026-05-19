package org.example.sudoku.controllers;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * Pantalla de carga. Hace fade-in del logo y luego pulsa indefinidamente
 * (1.0 → 1.25 → 1.0) hasta que {@link #requestFinish()} sea invocado.
 * Una vez recibido el aviso, termina el pulso en curso, ejecuta el
 * fade-out final y dispara {@link #onFinished}.
 *
 * Esto permite que {@link org.example.sudoku.Main} pre-cargue la vista
 * principal en segundo plano sin que el usuario vea un congelamiento.
 */
public class LoadingController {

    @FXML private StackPane root;
    @FXML private ImageView logo;
    @FXML private ProgressBar progressBar;
    @FXML private Label loadingLabel;

    private Consumer<Void> onFinished;
    private SequentialTransition pulse;
    private Timeline progressCreep;
    private boolean finishRequested;

    public void setOnFinished(Consumer<Void> onFinished) {
        this.onFinished = onFinished;
    }

    @FXML
    public void initialize() {
        logo.setOpacity(0);
        logo.setScaleX(1.0);
        logo.setScaleY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), logo);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        ScaleTransition grow = new ScaleTransition(Duration.millis(700), logo);
        grow.setFromX(1.0); grow.setFromY(1.0);
        grow.setToX(1.25); grow.setToY(1.25);

        ScaleTransition shrink = new ScaleTransition(Duration.millis(700), logo);
        shrink.setFromX(1.25); shrink.setFromY(1.25);
        shrink.setToX(1.0); shrink.setToY(1.0);

        pulse = new SequentialTransition(grow, shrink);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        // La barra "se arrastra" hasta 90% durante el periodo esperado de
        // arranque; cuando la app esté lista saltamos a 100%.
        progressBar.setProgress(0);
        progressCreep = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(progressBar.progressProperty(), 0.0, Interpolator.LINEAR)),
                new KeyFrame(Duration.millis(2500),
                        new KeyValue(progressBar.progressProperty(), 0.90, Interpolator.EASE_OUT))
        );
        progressCreep.play();
    }

    /**
     * Avisa que la app está lista. El método devuelve de inmediato;
     * el callback configurado en {@link #setOnFinished} se invoca
     * cuando termina el fade-out final.
     */
    public void requestFinish() {
        if (finishRequested) return;
        finishRequested = true;

        // Saltar la barra a 100% rápidamente y luego encadenar el fade-out.
        if (progressCreep != null) progressCreep.stop();
        Timeline snap = new Timeline(
                new KeyFrame(Duration.millis(220),
                        new KeyValue(progressBar.progressProperty(), 1.0, Interpolator.EASE_OUT))
        );
        snap.setOnFinished(e -> playFadeOut());
        snap.play();
    }

    private void playFadeOut() {
        // El fade tradicional fue reemplazado por una transición de
        // glitch que orquesta {@link org.example.sudoku.Main}. Aquí
        // simplemente detenemos las animaciones del splash y avisamos.
        if (loadingLabel != null) loadingLabel.setText("Listo");
        pulse.stop();
        if (onFinished != null) onFinished.accept(null);
    }
}
