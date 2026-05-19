package org.example.sudoku.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/**
 * Reproduce los loops ambientales constantes de la app:
 *   - room-tone (zumbido grave, tipo grabación de cámara).
 *   - heartbeat (~60 bpm, subliminal).
 *
 * Si alguno de los archivos no existe se ignora silenciosamente para
 * que la app siga funcionando.
 */
public final class AmbiancePlayer {

    private static MediaPlayer roomTone;
    private static MediaPlayer heartbeat;

    private AmbiancePlayer() {}

    public static void start() {
        if (roomTone == null) {
            roomTone = loop("/org/example/sudoku/assets/audioCues/ambient_roomtone.mp3", 0.18);
        }
        if (heartbeat == null) {
            heartbeat = loop("/org/example/sudoku/assets/audioCues/heartbeat.mp3", 0.10);
        }
    }

    public static void stop() {
        roomTone = dispose(roomTone);
        heartbeat = dispose(heartbeat);
    }

    private static MediaPlayer dispose(MediaPlayer p) {
        if (p == null) return null;
        try { p.stop(); p.dispose(); } catch (Exception ignored) {}
        return null;
    }

    private static MediaPlayer loop(String resourcePath, double volume) {
        URL url = AmbiancePlayer.class.getResource(resourcePath);
        if (url == null) {
            System.out.println("[Ambiance] recurso opcional ausente: " + resourcePath);
            return null;
        }
        try {
            Media media = new Media(url.toExternalForm());
            MediaPlayer p = new MediaPlayer(media);
            p.setCycleCount(MediaPlayer.INDEFINITE);
            p.setVolume(volume);
            p.play();
            return p;
        } catch (Exception e) {
            System.err.println("[Ambiance] no se pudo iniciar " + resourcePath + ": " + e);
            return null;
        }
    }
}
