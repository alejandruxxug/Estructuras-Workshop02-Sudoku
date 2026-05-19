package org.example.sudoku.fx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

/**
 * Efectos de glitch visual: un flicker corto para "tics" ambientales
 * (poco invasivo) y uno fuerte para la transición de la pantalla de
 * carga al feed.
 *
 * Todos los nodos se montan sobre un {@link StackPane} anfitrión y se
 * retiran al finalizar para no dejar basura en la escena.
 */
public final class GlitchEffect {

    private static final Random RNG = new Random();

    private GlitchEffect() {}

    /** Glitch ambiente: una banda horizontal que cruza la pantalla. */
    public static void brief(StackPane host) {
        if (host == null) return;
        double w = host.getWidth();
        double h = host.getHeight();
        if (w <= 0 || h <= 0) return;

        Pane layer = new Pane();
        layer.setMouseTransparent(true);
        layer.setPickOnBounds(false);
        layer.setPrefSize(w, h);

        Rectangle scan = new Rectangle(w, 6 + RNG.nextInt(10),
                Color.color(1, 1, 1, 0.55));
        scan.setMouseTransparent(true);
        layer.getChildren().add(scan);

        Rectangle flash = new Rectangle(w, h, Color.color(0, 0.69, 0.94, 0));
        flash.setMouseTransparent(true);
        layer.getChildren().add(flash);

        host.getChildren().add(layer);

        double startY = -scan.getHeight();
        double endY = h + scan.getHeight();
        scan.setLayoutY(startY);

        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,    e -> { scan.setLayoutY(startY); flash.setOpacity(0); }),
                new KeyFrame(Duration.millis(35),  e -> flash.setOpacity(0.18)),
                new KeyFrame(Duration.millis(70),  e -> flash.setOpacity(0.0)),
                new KeyFrame(Duration.millis(110), e -> scan.setLayoutY(endY))
        );
        tl.setOnFinished(e -> host.getChildren().remove(layer));
        tl.play();
    }

    /**
     * Glitch fuerte para la transición de carga. A los {@code midDelayMs}
     * milisegundos invoca {@code mid} (típicamente el cambio de escena);
     * el glitch continúa unos 300 ms más sobre la nueva escena y luego
     * se retira.
     */
    public static void transition(StackPane host, Runnable mid) {
        if (host == null) {
            if (mid != null) mid.run();
            return;
        }
        double w = host.getWidth();
        double h = host.getHeight();
        if (w <= 0 || h <= 0) {
            if (mid != null) mid.run();
            return;
        }

        Pane layer = new Pane();
        layer.setMouseTransparent(true);
        layer.setPickOnBounds(false);
        layer.setPrefSize(w, h);

        Rectangle flashWhite = new Rectangle(w, h, Color.color(1, 1, 1, 0));
        Rectangle flashCyan  = new Rectangle(w, h, Color.color(0, 0.69, 0.94, 0));
        Rectangle flashBlack = new Rectangle(w, h, Color.color(0, 0, 0, 0));
        Rectangle scanA = new Rectangle(w, 4, Color.color(1, 1, 1, 0.8));
        Rectangle scanB = new Rectangle(w, 3, Color.color(0, 0.69, 0.94, 0.85));
        Rectangle scanC = new Rectangle(w, 8, Color.color(1, 1, 1, 0.4));
        scanA.setLayoutY(-10);
        scanB.setLayoutY(-10);
        scanC.setLayoutY(-10);

        layer.getChildren().addAll(flashWhite, flashCyan, flashBlack, scanA, scanB, scanC);
        host.getChildren().add(layer);

        // Secuencia de keyframes: parpadeos rápidos y barras cruzadas.
        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,        e -> flashWhite.setOpacity(0.85)),
                new KeyFrame(Duration.millis(40),  e -> flashWhite.setOpacity(0)),
                new KeyFrame(Duration.millis(55),  e -> flashCyan.setOpacity(0.55)),
                new KeyFrame(Duration.millis(95),  e -> flashCyan.setOpacity(0)),
                new KeyFrame(Duration.millis(110), e -> flashBlack.setOpacity(1)),
                new KeyFrame(Duration.millis(150), e -> {
                    if (mid != null) {
                        try { mid.run(); } catch (Exception ex) { ex.printStackTrace(); }
                    }
                    // Tras el swap el layer ya no está en host -> reañadir encima
                    if (!host.getChildren().contains(layer)) {
                        host.getChildren().add(layer);
                    }
                }),
                new KeyFrame(Duration.millis(180), e -> flashBlack.setOpacity(0)),
                new KeyFrame(Duration.millis(190), e -> flashWhite.setOpacity(0.6)),
                new KeyFrame(Duration.millis(230), e -> flashWhite.setOpacity(0)),
                new KeyFrame(Duration.millis(80),  e -> scanA.setLayoutY(-10)),
                new KeyFrame(Duration.millis(250), e -> scanA.setLayoutY(h + 20)),
                new KeyFrame(Duration.millis(140), e -> scanB.setLayoutY(h + 20)),
                new KeyFrame(Duration.millis(320), e -> scanB.setLayoutY(-10)),
                new KeyFrame(Duration.millis(200), e -> scanC.setLayoutY(-10)),
                new KeyFrame(Duration.millis(420), e -> scanC.setLayoutY(h + 20)),
                new KeyFrame(Duration.millis(340), e -> flashCyan.setOpacity(0.35)),
                new KeyFrame(Duration.millis(420), e -> flashCyan.setOpacity(0))
        );
        tl.setOnFinished(e -> host.getChildren().remove(layer));
        tl.play();
    }
}
