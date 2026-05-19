package org.example.sudoku.overlay;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Modal personalizado (no nativo). Fondo oscurecido + tarjeta centrada
 * con título, mensaje y botón Aceptar.
 */
public class ModalView extends StackPane {

    public ModalView(String title, String message) {
        Rectangle dim = new Rectangle();
        dim.widthProperty().bind(widthProperty());
        dim.heightProperty().bind(heightProperty());
        dim.setFill(Color.rgb(0, 0, 0, 0.5));

        VBox card = new VBox(16);
        card.getStyleClass().add("modal-card");
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(440);
        card.setMaxHeight(220);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("modal-title");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("modal-message");
        messageLabel.setWrapText(true);

        Button ok = new Button("Aceptar");
        ok.getStyleClass().add("primary-button");
        ok.setOnAction(e -> OverlayManager.get().dismiss());

        card.getChildren().addAll(titleLabel, messageLabel, ok);

        getChildren().addAll(dim, card);
        setAlignment(Pos.CENTER);
    }
}
