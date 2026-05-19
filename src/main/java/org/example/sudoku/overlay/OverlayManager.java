package org.example.sudoku.overlay;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * Coordinador único de overlays. La raíz de la escena es un {@link StackPane}
 * cuyos hijos extras se renderizan encima del contenido del feed. Toda la app
 * comparte una sola instancia para evitar overlays simultáneos.
 */
public class OverlayManager {

    private static OverlayManager instance;

    public static void init(StackPane root) {
        instance = new OverlayManager(root);
    }

    public static OverlayManager get() {
        if (instance == null) {
            throw new IllegalStateException("OverlayManager no inicializado");
        }
        return instance;
    }

    private final StackPane root;
    private Node current;

    private OverlayManager(StackPane root) {
        this.root = root;
    }

    public void show(Node overlay) {
        dismiss();
        current = overlay;
        root.getChildren().add(overlay);
    }

    public void dismiss() {
        if (current != null) {
            root.getChildren().remove(current);
            current = null;
        }
    }

    public StackPane root() {
        return root;
    }
}
