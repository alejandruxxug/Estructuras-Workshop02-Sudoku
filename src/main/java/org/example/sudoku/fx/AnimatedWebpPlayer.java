package org.example.sudoku.fx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Lector + reproductor de imágenes WebP animadas usando javax.imageio
 * con el plugin de TwelveMonkeys. Decodifica todos los frames a memoria
 * y los reproduce sobre un {@link ImageView} mediante un {@link Timeline}.
 */
public final class AnimatedWebpPlayer {

    private final List<Image> frames;
    private final List<Integer> delaysMs;

    private AnimatedWebpPlayer(List<Image> frames, List<Integer> delaysMs) {
        this.frames = frames;
        this.delaysMs = delaysMs;
    }

    /**
     * Carga un .webp animado desde el classpath. Si solo hay un frame se
     * retorna igualmente una instancia válida que se mostrará estática.
     */
    public static AnimatedWebpPlayer load(String resourcePath) {
        try (InputStream in = Objects.requireNonNull(
                AnimatedWebpPlayer.class.getResourceAsStream(resourcePath),
                "Recurso no encontrado: " + resourcePath)) {

            ImageInputStream iis = ImageIO.createImageInputStream(in);
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("webp");
            if (!readers.hasNext()) {
                throw new IllegalStateException(
                        "No hay un ImageReader para WebP. ¿Falta la dependencia imageio-webp?");
            }

            ImageReader reader = readers.next();
            reader.setInput(iis, false, false);

            int count = reader.getNumImages(true);
            List<Image> imgs = new ArrayList<>(count);
            List<Integer> delays = new ArrayList<>(count);

            for (int i = 0; i < count; i++) {
                IIOImage iio = reader.readAll(i, null);
                BufferedImage bi = (BufferedImage) iio.getRenderedImage();
                imgs.add(SwingFXUtils.toFXImage(bi, null));
                delays.add(readDelayMs(iio.getMetadata()));
            }
            reader.dispose();
            return new AnimatedWebpPlayer(imgs, delays);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cargar WebP: " + resourcePath, e);
        }
    }

    private static int readDelayMs(IIOMetadata md) {
        // Buscar cualquier atributo que pueda contener el delay del frame
        // (TwelveMonkeys lo expone como "duration" en metadata nativa WebP).
        if (md != null) {
            for (String fmt : md.getMetadataFormatNames()) {
                Integer found = scanForDelay(md.getAsTree(fmt));
                if (found != null && found > 0) return found;
            }
        }
        // Fallback: 40 ms ≈ 25 fps (la mayoría de webp animados son ~24-30 fps).
        return 40;
    }

    private static Integer scanForDelay(Node node) {
        if (node == null) return null;
        NamedNodeMap attrs = node.getAttributes();
        if (attrs != null) {
            String[] keys = {"duration", "delay", "delayTime", "durationMS", "frameDelay"};
            for (String key : keys) {
                Node a = attrs.getNamedItem(key);
                if (a == null) continue;
                try {
                    int v = Integer.parseInt(a.getNodeValue().trim());
                    // Algunos formatos miden en centésimas de segundo (GIF style).
                    if ("delayTime".equals(key)) v *= 10;
                    if (v > 0) return v;
                } catch (NumberFormatException ignored) {}
            }
        }
        Node child = node.getFirstChild();
        while (child != null) {
            Integer found = scanForDelay(child);
            if (found != null) return found;
            child = child.getNextSibling();
        }
        return null;
    }

    public Image firstFrame() {
        return frames.get(0);
    }

    public int frameCount() {
        return frames.size();
    }

    /**
     * Construye un Timeline que recorre los frames sobre el ImageView dado
     * una sola vez. El Timeline se devuelve sin iniciar; el llamador debe
     * invocar {@link Timeline#play()} y, opcionalmente, sobreescribir el
     * ciclo con {@link Timeline#setCycleCount(int)}.
     */
    public Timeline buildTimeline(ImageView view) {
        view.setImage(frames.get(0));

        if (frames.size() == 1) {
            return new Timeline();
        }

        Timeline tl = new Timeline();
        double elapsed = 0;
        for (int i = 0; i < frames.size(); i++) {
            final Image frame = frames.get(i);
            tl.getKeyFrames().add(new KeyFrame(
                    Duration.millis(elapsed),
                    ev -> view.setImage(frame)
            ));
            elapsed += delaysMs.get(i);
        }
        // KeyFrame final para definir la duración total del ciclo
        tl.getKeyFrames().add(new KeyFrame(Duration.millis(elapsed)));
        tl.setCycleCount(1);
        return tl;
    }

    /** Duración total estimada de una reproducción del clip, en milisegundos. */
    public long totalDurationMs() {
        long sum = 0;
        for (Integer d : delaysMs) sum += d;
        return sum;
    }
}
