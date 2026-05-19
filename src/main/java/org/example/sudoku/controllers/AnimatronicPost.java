package org.example.sudoku.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * Carga animatronic-post.fxml y rellena nombre, handle, color de avatar
 * y cuerpo del post. Devuelve el {@link Node} listo para insertar en el
 * feed.
 */
public final class AnimatronicPost {

    private AnimatronicPost() {}

    public static Node build(String displayName, String handle,
                             String body, Color avatarColor) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    AnimatronicPost.class.getResource("/org/example/sudoku/animatronic-post.fxml"));
            Node node = loader.load();

            ((Label) loader.getNamespace().get("displayName")).setText(displayName);
            ((Label) loader.getNamespace().get("handle")).setText(handle + " · ahora");
            ((Label) loader.getNamespace().get("body")).setText(body);

            String letter = displayName.isEmpty() ? "?" :
                    displayName.substring(0, 1).toUpperCase();
            ((Label) loader.getNamespace().get("avatarLetter")).setText(letter);

            StackPane avatar = (StackPane) loader.getNamespace().get("avatar");
            avatar.setStyle("-fx-background-color: " + toHex(avatarColor)
                    + "; -fx-background-radius: 999;");
            return node;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar animatronic-post.fxml", e);
        }
    }

    private static String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) Math.round(c.getRed() * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue() * 255));
    }
}
