package org.example.sudoku.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Reproductor sencillo de pistas cortas. Cada llamada a {@link #play(String)}
 * crea un {@link MediaPlayer} nuevo para evitar problemas de re-uso. Los
 * reproductores activos se registran para poder detenerlos al cerrar overlays.
 */
public final class AudioCue {

    private static final List<MediaPlayer> ACTIVE = new ArrayList<>();

    private AudioCue() {}

    public static MediaPlayer play(String resourcePath) {
        URL url = AudioCue.class.getResource(resourcePath);
        if (url == null) {
            System.err.println("Recurso de audio no encontrado: " + resourcePath);
            return null;
        }
        Media media = new Media(url.toExternalForm());
        MediaPlayer player = new MediaPlayer(media);
        player.setVolume(1.0);
        player.setOnEndOfMedia(() -> {
            player.dispose();
            ACTIVE.remove(player);
        });
        ACTIVE.add(player);
        player.play();
        return player;
    }

    public static void stopAll() {
        for (MediaPlayer p : new ArrayList<>(ACTIVE)) {
            try {
                p.stop();
                p.dispose();
            } catch (Exception ignored) {
            }
        }
        ACTIVE.clear();
    }
}
