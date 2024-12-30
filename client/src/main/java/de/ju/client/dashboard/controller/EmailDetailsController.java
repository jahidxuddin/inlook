package de.ju.client.dashboard.controller;

import de.ju.client.model.Email;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EmailDetailsController {
    private volatile boolean running = true;
    private Thread workerThread;
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
        overlay.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Stage stage = (Stage) overlay.getScene().getWindow();
                stage.setOnCloseRequest(event -> stopWorkerThread());
            }
        });

        workerThread = new Thread(() -> {
            while (running) {
                Object userData = overlay.getUserData();
                if (emailData != userData) {
                    Platform.runLater(() -> {
                        this.emailData = (Email) userData;
                        subjectText.setText(emailData.getSubject());
                        details.setText(emailData.getSender().substring(0, emailData.getSender().indexOf("@")) + " - " + emailData.getSentAt());
                        contentText.setText(emailData.getBody());
                    });
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    running = false;
                    Thread.currentThread().interrupt();
                }
            }
        });
        workerThread.start();
    }

    private void setStage(Stage stage) {
        stage.setOnCloseRequest(event -> stopWorkerThread());
    }

    public void stopWorkerThread() {
        running = false;
        if (workerThread != null && workerThread.isAlive()) {
            workerThread.interrupt();
        }
    }

    @FXML
    private void closeOverlay(ActionEvent event) {
        Pane root = (Pane) overlay.getScene().getRoot();
        root.getChildren().remove(this.overlay);
    }
}
