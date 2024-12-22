package de.ju.client.controller.account;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ManageAccountController {
    @FXML
    public VBox overlay;
    @FXML
    public MFXTextField emailField;
    @FXML
    public MFXTextField passwordField;

    @FXML
    private void closeOverlay(ActionEvent event) {
        Pane root = (Pane) overlay.getScene().getRoot();
        root.getChildren().remove(this.overlay);
    }
}
