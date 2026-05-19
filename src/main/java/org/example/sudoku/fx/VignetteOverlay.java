package org.example.sudoku.fx;

import javafx.scene.layout.Region;

/**
 * Capa que oscurece sutilmente las esquinas mediante un gradiente
 * radial (definido en {@code app.css}, clase {@code vignette-overlay}).
 * Es transparente al ratón para no interferir con la interacción.
 */
public class VignetteOverlay extends Region {

    public VignetteOverlay() {
        getStyleClass().add("vignette-overlay");
        setMouseTransparent(true);
        setPickOnBounds(false);
    }
}
