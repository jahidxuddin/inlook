package de.ju.client.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Overlay {
    private final Pane overlay;

    public Overlay(Node anyNode, String fxmlPath) {
        this.overlay = createOverlay(anyNode, fxmlPath);
    }

    private Pane createOverlay(Node anyNode, String fxmlPath) {
        Pane overlay;
        try {
            FXMLLoader loader = new FXMLLoader(Overlay.class.getResource(fxmlPath));
            overlay = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load FXML: " + fxmlPath, e);
        }

        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 1);");
        overlay.setPrefSize(
                anyNode.getScene().getWindow().getWidth(),
                anyNode.getScene().getWindow().getHeight()
        );

        return overlay;
    }

    public void showOverlay(Node anyNode) {
        AnchorPane root = (AnchorPane) anyNode.getScene().getRoot();
        root.getChildren().add(this.overlay);
    }
}
