package de.ju.client.controller.home;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class EmailComposerController {
    @FXML
    public VBox overlay;
    @FXML
    public MFXTextField recipientField;
    @FXML
    public MFXTextField subjectField;
    @FXML
    public TextArea contentField;
    @FXML
    public MFXButton sendButton;
    @FXML
    public MFXButton attachButton;

    @FXML
    private void closeOverlay(ActionEvent event) {
        Pane root = (Pane) overlay.getScene().getRoot();
        root.getChildren().remove(this.overlay);
    }
}
