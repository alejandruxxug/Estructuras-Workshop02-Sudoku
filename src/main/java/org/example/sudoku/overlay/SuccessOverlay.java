package org.example.sudoku.overlay;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.example.sudoku.audio.AudioCue;
import org.example.sudoku.fx.ConfettiCanvas;

/**
 * Overlay de éxito: fondo semitransparente, etiqueta "¡RESUELTO!" enorme,
 * dos chorros de confeti desde las esquinas inferiores apuntando al centro
 * superior, y audio de éxito.
 */
public class SuccessOverlay extends StackPane {

    public SuccessOverlay(double width, double height) {
        setPrefSize(width, height);

        Rectangle dim = new Rectangle(width, height, Color.rgb(0, 0, 0, 0.35));

        Label title = new Label("¡RESUELTO!");
        title.getStyleClass().add("success-title");
        title.setOpacity(0);

        double targetX = width / 2.0;
        double targetY = height * 0.20;

        ConfettiCanvas left = new ConfettiCanvas(width, height,
                width * 0.05, height * 0.95, targetX, targetY);
        ConfettiCanvas right = new ConfettiCanvas(width, height,
                width * 0.95, height * 0.95, targetX, targetY);

        getChildren().addAll(dim, left, right, title);
        StackPane.setAlignment(title, Pos.CENTER);

        // El audio comienza 1 s ANTES de la animación (confeti + título).
        AudioCue.play("/org/example/sudoku/assets/audioCues/success.mp3");

        PauseTransition leadIn = new PauseTransition(Duration.millis(1000));
        leadIn.setOnFinished(e -> {
            left.burst(2.5);
            right.burst(2.5);
            FadeTransition titleFade = new FadeTransition(Duration.millis(250), title);
            titleFade.setFromValue(0);
            titleFade.setToValue(1);
            titleFade.play();
        });
        leadIn.play();

        setOnMouseClicked(e -> OverlayManager.get().dismiss());

        // Cierre automático tras unos segundos
        FadeTransition fade = new FadeTransition(Duration.millis(800), this);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        PauseTransition hold = new PauseTransition(Duration.seconds(5));
        SequentialTransition seq = new SequentialTransition(hold, fade);
        seq.setOnFinished(e -> OverlayManager.get().dismiss());
        seq.play();
    }
}
