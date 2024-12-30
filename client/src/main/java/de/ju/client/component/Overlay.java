package de.ju.client.component;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class Overlay {
    private final Pane overlay;
    private Object data;

    public Overlay(String fxmlPath) {
        this.overlay = createOverlay(fxmlPath);
    }

    public Overlay(String fxmlPath, Object data) {
        this.overlay = createOverlay(fxmlPath, data);
    }

    private Pane createOverlay(String fxmlPath) {
        Pane overlay;
        try {
            FXMLLoader loader = new FXMLLoader(Overlay.class.getResource(fxmlPath));
            overlay = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load FXML: " + fxmlPath, e);
        }
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 1);");

        return overlay;
    }

    private Pane createOverlay(String fxmlPath, Object data) {
        Pane overlay;
        try {
            FXMLLoader loader = new FXMLLoader(Overlay.class.getResource(fxmlPath));
            overlay = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Unable to load FXML: " + fxmlPath, e);
        }
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 1);");
        overlay.setUserData(data);

        return overlay;
    }

    public void showOverlay(Node anyNode) {
        AnchorPane root = (AnchorPane) anyNode.getScene().getRoot();
        root.getChildren().add(this.overlay);
    }

    public void showOverlay(Node anyNode, Object data) {
        this.overlay.setUserData(data);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        AnchorPane root = (AnchorPane) anyNode.getScene().getRoot();
        root.getChildren().add(this.overlay);
    }
}
