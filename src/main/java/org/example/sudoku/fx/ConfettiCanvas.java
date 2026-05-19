package org.example.sudoku.fx;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Canvas para confeti estilo "FNAF 6 AM". Lanza partículas desde una
 * esquina inferior hacia el centro superior con un arco parabólico.
 * El llamador instancia dos: una en la esquina inferior izquierda y
 * otra en la derecha.
 */
public class ConfettiCanvas extends Canvas {

    private static final Color[] PALETTE = {
            Color.web("#00AFF0"),
            Color.web("#FFD400"),
            Color.web("#FF4F8B"),
            Color.web("#7CFFB2"),
            Color.web("#FFFFFF"),
            Color.web("#FF8A00")
    };

    private final List<Particle> particles = new ArrayList<>();
    private final Random rng = new Random();
    private final double originX;
    private final double originY;
    private final double targetX;
    private final double targetY;
    private final AnimationTimer timer;
    private long lastSpawnNanos;
    private long endTimeNanos;

    public ConfettiCanvas(double width, double height,
                          double originX, double originY,
                          double targetX, double targetY) {
        super(width, height);
        this.originX = originX;
        this.originY = originY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.timer = new AnimationTimer() {
            private long last;
            @Override public void handle(long now) {
                if (last == 0) last = now;
                double dt = (now - last) / 1_000_000_000.0;
                last = now;
                tick(now, dt);
            }
        };
    }

    public void burst(double durationSeconds) {
        endTimeNanos = System.nanoTime() + (long) (durationSeconds * 1_000_000_000L);
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    private void tick(long now, double dt) {
        if (now < endTimeNanos && now - lastSpawnNanos > 15_000_000L) {
            spawnBatch();
            lastSpawnNanos = now;
        }

        GraphicsContext g = getGraphicsContext2D();
        g.clearRect(0, 0, getWidth(), getHeight());

        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.life += dt;
            p.vy += 380 * dt; // gravedad
            p.x += p.vx * dt;
            p.y += p.vy * dt;
            p.rot += p.spin * dt;
            if (p.life > p.maxLife || p.y > getHeight() + 40) {
                particles.remove(i);
                continue;
            }
            g.save();
            g.translate(p.x, p.y);
            g.rotate(Math.toDegrees(p.rot));
            g.setFill(p.color);
            g.fillRect(-p.size / 2.0, -p.size / 2.0, p.size, p.size * 0.5);
            g.restore();
        }

        if (now >= endTimeNanos && particles.isEmpty()) {
            timer.stop();
        }
    }

    private void spawnBatch() {
        for (int i = 0; i < 6; i++) {
            particles.add(create());
        }
    }

    private Particle create() {
        // Velocidad hacia el objetivo + ruido
        double dx = targetX - originX;
        double dy = targetY - originY;
        double dist = Math.hypot(dx, dy);
        double nx = dx / dist;
        double ny = dy / dist;
        double speed = 700 + rng.nextDouble() * 350;
        // Inyectar varianza angular para abanico
        double angle = Math.atan2(ny, nx) + (rng.nextDouble() - 0.5) * 0.8;
        Particle p = new Particle();
        p.x = originX + (rng.nextDouble() - 0.5) * 20;
        p.y = originY + (rng.nextDouble() - 0.5) * 20;
        p.vx = Math.cos(angle) * speed;
        p.vy = Math.sin(angle) * speed;
        p.size = 8 + rng.nextDouble() * 8;
        p.color = PALETTE[rng.nextInt(PALETTE.length)];
        p.rot = rng.nextDouble() * Math.PI * 2;
        p.spin = (rng.nextDouble() - 0.5) * 12;
        p.maxLife = 2.5 + rng.nextDouble();
        return p;
    }

    private static class Particle {
        double x, y, vx, vy, rot, spin, size, life, maxLife;
        Color color;
    }
}
