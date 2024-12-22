package de.ju.client.controller.home;

import de.ju.client.data.DataStore;
import de.ju.client.email.client.SMTPClient;
import de.ju.client.email.exception.FailedAuthenticationException;
import de.ju.client.email.exception.FailedConnectionException;
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
    public MFXButton attachButton;

    @FXML
    private void closeOverlay(ActionEvent event) {
        Pane root = (Pane) overlay.getScene().getRoot();
        root.getChildren().remove(this.overlay);
    }

    @FXML
    private void onSendMail(ActionEvent event) {
        String recipientEmail = recipientField.getText();
        String subject = subjectField.getText();
        String body = contentField.getText();

        if (recipientEmail.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            return;
        }

        try {
            SMTPClient smtpClient = SMTPClient.getInstance("localhost", 587, DataStore.getInstance().getEmail());
            smtpClient.initiateConnection();
            smtpClient.authenticate(DataStore.getInstance().getJwtToken());
            smtpClient.sendMail(recipientEmail.trim(), subject.trim(), body.trim());
        } catch (FailedConnectionException | FailedAuthenticationException e) {
            System.err.println(e.getMessage());
        }

        recipientField.setText("");
        subjectField.setText("");
        contentField.setText("");
    }
}
