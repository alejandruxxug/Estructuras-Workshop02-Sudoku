package org.example.sudoku.fx;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Capa transparente al ratón que registra la última posición del cursor
 * y permite "soltar" copias fantasma del mismo en momentos aleatorios.
 *
 * El cursor real no se modifica. Cada fantasma es un círculo blanco
 * tenue que aparece y se desvanece en ~700 ms.
 */
public class CursorGhostLayer extends Pane {

    private double lastX, lastY;
    private boolean hasPosition;

    public CursorGhostLayer() {
        setMouseTransparent(true);
        setPickOnBounds(false);
    }

    public void install(Scene scene) {
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            lastX = e.getSceneX();
            lastY = e.getSceneY();
            hasPosition = true;
        });
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            lastX = e.getSceneX();
            lastY = e.getSceneY();
            hasPosition = true;
        });
    }

    public void spawnGhost() {
        if (!hasPosition) return;
        Circle dot = new Circle(lastX, lastY, 7, Color.color(1, 1, 1, 0.45));
        dot.setMouseTransparent(true);
        getChildren().add(dot);

        FadeTransition fade = new FadeTransition(Duration.millis(700), dot);
        fade.setFromValue(0.55);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> getChildren().remove(dot));
        fade.play();
    }
}
