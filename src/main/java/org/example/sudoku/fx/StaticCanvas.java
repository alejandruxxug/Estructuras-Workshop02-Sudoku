package org.example.sudoku.fx;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Canvas que dibuja estática de TV (ruido en escala de grises) bloque por
 * bloque a alta velocidad. Optimizado dibujando bloques de 6x6 px en vez
 * de pixel por pixel.
 */
public class StaticCanvas extends Canvas {

    private static final int BLOCK = 6;
    private final Random rng = new Random();
    private AnimationTimer timer;

    public StaticCanvas(double width, double height) {
        super(width, height);
        start();
    }

    public final void start() {
        if (timer != null) return;
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                drawFrame();
            }
        };
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    private void drawFrame() {
        GraphicsContext g = getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();
        for (int y = 0; y < h; y += BLOCK) {
            for (int x = 0; x < w; x += BLOCK) {
                double v = rng.nextDouble();
                g.setFill(Color.gray(v));
                g.fillRect(x, y, BLOCK, BLOCK);
            }
        }
    }
}
