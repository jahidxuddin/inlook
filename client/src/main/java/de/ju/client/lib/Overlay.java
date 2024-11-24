package de.ju.client.lib;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class Overlay {
    public static void showOverlay(Stage stage, Consumer<StackPane> content) {
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 1); -fx-padding: 18px");
        overlay.setPrefSize(stage.getWidth(), stage.getHeight());

        StackPane contentPane = new StackPane();
        contentPane.setPrefSize(overlay.getWidth(), overlay.getHeight());
        contentPane.setStyle("-fx-background-color: rgba(255, 255, 255, 1);");
        content.accept(contentPane);
        overlay.getChildren().add(contentPane);

        AnchorPane root = (AnchorPane) stage.getScene().getRoot();
        root.getChildren().add(overlay);
    }

    public static void removeOverlay(Stage stage, Pane overlay) {
        AnchorPane root = (AnchorPane) stage.getScene().getRoot();
        root.getChildren().remove(overlay.getParent());
    }
}
