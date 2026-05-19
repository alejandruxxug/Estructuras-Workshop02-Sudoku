package org.example.sudoku.overlay;

import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.example.sudoku.audio.AudioCue;
import org.example.sudoku.fx.AnimatedWebpPlayer;
import org.example.sudoku.fx.StaticCanvas;

import java.util.Random;

/**
 * Overlay de fallo en cuatro etapas:
 *   1. Corte directo a negro con el jumpscare ya encima + audio sincronizado
 *      (sin retardo previo).
 *   2. Estática a pantalla completa.
 *   3. Pantalla roja con estática atenuada y botón de reintento.
 */
public class FailureOverlay extends StackPane {

    private final double width;
    private final double height;
    private final Random rng = new Random();

    private Timeline jumpscareTimeline;
    private MediaPlayer jumpscareAudio;
    private StaticCanvas staticCanvas;

    public FailureOverlay(double width, double height) {
        this.width = width;
        this.height = height;
        setPrefSize(width, height);
        setAlignment(Pos.CENTER);
        startJumpscare();
    }

    private void startJumpscare() {
        Rectangle black = new Rectangle(width, height, Color.BLACK);

        int pick = 1 + rng.nextInt(5);
        AnimatedWebpPlayer player = AnimatedWebpPlayer.load(
                "/org/example/sudoku/assets/jumpscares/" + pick + ".webp");

        ImageView img = new ImageView(player.firstFrame());
        img.setPreserveRatio(true);
        img.setFitWidth(width);
        img.setFitHeight(height);
        img.setVisible(false);

        jumpscareTimeline = player.buildTimeline(img);
        jumpscareTimeline.setCycleCount(1);
        jumpscareTimeline.setOnFinished(e -> goToStatic());

        getChildren().setAll(black, img);

        // El audio arranca 50 ms ANTES de que el gif comience a animarse.
        jumpscareAudio = AudioCue.play("/org/example/sudoku/assets/audioCues/jumpscare.mp3");

        PauseTransition leadIn = new PauseTransition(Duration.millis(80));
        leadIn.setOnFinished(e -> {
            img.setVisible(true);
            jumpscareTimeline.play();

            // Fallback por si el timeline está vacío (clip de un solo frame).
            if (player.frameCount() <= 1) {
                PauseTransition fb = new PauseTransition(Duration.millis(900));
                fb.setOnFinished(ev -> goToStatic());
                fb.play();
            }
        });
        leadIn.play();
    }

    private void goToStatic() {
        if (jumpscareTimeline != null) jumpscareTimeline.stop();
        // El audio NO se detiene: sigue sonando por debajo de la estática
        // hasta que termine naturalmente o se cierre el overlay.

        staticCanvas = new StaticCanvas(width, height);
        getChildren().setAll(staticCanvas);

        PauseTransition hold = new PauseTransition(Duration.millis(1500));
        hold.setOnFinished(e -> goToDeathScreen());
        hold.play();
    }

    private void goToDeathScreen() {
        // Mantener la estática atenuada y superponer el rojo + botón
        if (staticCanvas != null) staticCanvas.setOpacity(0.35);

        Rectangle red = new Rectangle(width, height, Color.web("#CC0000", 0.80));

        Button retry = new Button("REINTENTAR");
        retry.getStyleClass().add("retry-button");
        retry.setOnAction(e -> {
            AudioCue.stopAll();
            if (staticCanvas != null) staticCanvas.stop();
            OverlayManager.get().dismiss();
        });

        StackPane retryWrap = new StackPane(retry);
        retryWrap.setAlignment(Pos.CENTER);

        getChildren().setAll(staticCanvas, red, retryWrap);
    }
}
