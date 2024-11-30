package de.ju.client.controller.home;

import de.ju.client.model.Email;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class EmailDetailsController {
    private Email emailData;

    @FXML
    private VBox overlay;
    @FXML
    public Text details;
    @FXML
    private Label subjectText;
    @FXML
    private Text contentText;

    @FXML
    public void initialize() {
        new Thread(() -> {
            while (true) {
                Object userData = overlay.getUserData();
                if (emailData != userData) {
                    Platform.runLater(() -> {
                        this.emailData = (Email) userData;
                        subjectText.setText(emailData.getSubject());
                        details.setText(emailData.getSender().substring(0, emailData.getSender().indexOf("@")) + " - " + emailData.getDate());
                        contentText.setText(emailData.getContent());
                    });
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
        }).start();
    }

    @FXML
    private void closeOverlay(ActionEvent event) {
        Pane root = (Pane) overlay.getScene().getRoot();
        root.getChildren().remove(this.overlay);
    }
}
