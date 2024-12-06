package de.ju.client.controller.account;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ManageAccountController {
    @FXML
    public VBox overlay;

    @FXML
    private void closeOverlay(ActionEvent event) {
        Pane root = (Pane) overlay.getScene().getRoot();
        root.getChildren().remove(this.overlay);
    }
}
