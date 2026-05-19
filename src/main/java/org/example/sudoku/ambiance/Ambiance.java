package org.example.sudoku.ambiance;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.example.sudoku.controllers.AnimatronicPost;
import org.example.sudoku.fx.CursorGhostLayer;
import org.example.sudoku.fx.GlitchEffect;
import org.example.sudoku.fx.VignetteOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Capa "ambiente" persistente: viñeta, fantasmas del cursor, parpadeos
 * de celda, glitches breves y posts fantasma. Cada tick se reprograma
 * a un intervalo aleatorio para que el usuario no pueda anticipar nada.
 */
public final class Ambiance {

    private static final Random RNG = new Random();

    private static StackPane sceneRoot;
    private static VignetteOverlay vignette;
    private static CursorGhostLayer cursorLayer;
    private static VBox feed;
    private static TextField[][] cells;

    private static final List<Timeline> tickers = new ArrayList<>();
    private static boolean started;

    private static final String[] PHANTOM_BODIES = {
            "te estoy viendo desplazar.",
            "5, 3, 0, 0, 7, 0, 0, 0, 0",
            "no me dejaste cuando me apagaste.",
            "la celda (3, 7) es mía.",
            "¿quién está moviendo el cursor?",
            "el solver no es el único que recorre el tablero.",
            "behind you. just kidding. or am I.",
            "este post no estaba aquí hace un minuto."
    };

    private static final String[][] PHANTOM_IDENTITIES = {
            {"Circus Baby",    "@circus_baby",    "#E0506E"},
            {"Ballora",        "@ballora_ballet", "#7E57C2"},
            {"Funtime Foxy",   "@funtime_foxy",   "#EC6FA0"},
            {"Funtime Freddy", "@bonbon_freddy",  "#5C8DCB"},
            {"Ennard",         "@ennard_ensamble","#9E9E9E"},
            {"Funtime Bonnie", "@bonnie_funtime", "#42A5F5"}
    };

    private Ambiance() {}

    public static void start(StackPane root, Scene scene) {
        if (started) return;
        started = true;
        sceneRoot = root;

        vignette = new VignetteOverlay();
        cursorLayer = new CursorGhostLayer();
        cursorLayer.install(scene);

        sceneRoot.getChildren().addAll(vignette, cursorLayer);

        scheduleGlitchTicker();
        scheduleCursorGhostTicker();
        scheduleGhostCellTicker();
        schedulePhantomPostTicker();
    }

    public static void registerFeed(VBox feedVBox) {
        feed = feedVBox;
    }

    public static void registerCells(TextField[][] sudokuCells) {
        cells = sudokuCells;
    }

    public static void stop() {
        for (Timeline t : tickers) t.stop();
        tickers.clear();
        if (sceneRoot != null) {
            if (vignette != null) sceneRoot.getChildren().remove(vignette);
            if (cursorLayer != null) sceneRoot.getChildren().remove(cursorLayer);
        }
        vignette = null;
        cursorLayer = null;
        feed = null;
        cells = null;
        started = false;
    }

    // ---------- Tickers ----------

    private static void scheduleGlitchTicker() {
        scheduleNext(30_000, 90_000, () -> GlitchEffect.brief(sceneRoot), Ambiance::scheduleGlitchTicker);
    }

    private static void scheduleCursorGhostTicker() {
        scheduleNext(5_000, 15_000, () -> {
            if (cursorLayer != null) cursorLayer.spawnGhost();
        }, Ambiance::scheduleCursorGhostTicker);
    }

    private static void scheduleGhostCellTicker() {
        scheduleNext(8_000, 25_000, Ambiance::flashRandomCell, Ambiance::scheduleGhostCellTicker);
    }

    private static void schedulePhantomPostTicker() {
        // Primera aparición tarda más que las siguientes.
        scheduleNext(60_000, 180_000, Ambiance::insertPhantomPost,
                () -> scheduleNext(90_000, 300_000, Ambiance::insertPhantomPost,
                        Ambiance::schedulePhantomPostTicker));
    }

    private static void scheduleNext(long minMs, long maxMs,
                                     Runnable action, Runnable reschedule) {
        long delay = minMs + (long) (RNG.nextDouble() * (maxMs - minMs));
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(delay), e -> {
            try { action.run(); } catch (Exception ex) { ex.printStackTrace(); }
            reschedule.run();
        }));
        tickers.add(tl);
        tl.play();
    }

    private static void flashRandomCell() {
        if (cells == null) return;
        int r = RNG.nextInt(cells.length);
        int c = RNG.nextInt(cells[0].length);
        TextField tf = cells[r][c];
        if (tf == null) return;
        if (!tf.getStyleClass().contains("ghost-flash")) {
            tf.getStyleClass().add("ghost-flash");
        }
        PauseTransition off = new PauseTransition(Duration.millis(180));
        off.setOnFinished(e -> tf.getStyleClass().remove("ghost-flash"));
        off.play();
    }

    private static void insertPhantomPost() {
        if (feed == null) return;
        String body = PHANTOM_BODIES[RNG.nextInt(PHANTOM_BODIES.length)];
        String[] who = PHANTOM_IDENTITIES[RNG.nextInt(PHANTOM_IDENTITIES.length)];
        var node = AnimatronicPost.build(who[0], who[1], body, Color.web(who[2]));
        // Insertarlo justo debajo del post de sudoku (índice 1).
        int idx = Math.min(1, feed.getChildren().size());
        feed.getChildren().add(idx, node);
    }
}
