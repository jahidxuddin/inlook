package de.ju.client.dashboard.controller;

import de.ju.client.dashboard.component.EmailListCell;
import de.ju.client.service.store.DataStore;
import de.ju.client.service.email.POP3Client;
import de.ju.client.service.email.exception.FailedAuthenticationException;
import de.ju.client.service.email.exception.FailedConnectionException;
import de.ju.client.model.Email;
import de.ju.client.component.Overlay;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.effects.DepthLevel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class DashboardController {
    @FXML
    private MFXListView<Email> emailListView;
    private Overlay emailComposerOverlay;
    private Overlay emailDetailsOverlay;

    @FXML
    public void initialize() {
        initializeOverlays();
        populateEmailListView();
        initializeEmailListView();
    }

    private void initializeOverlays() {
        emailComposerOverlay = new Overlay("/de/ju/client/fxml/overlay/EmailComposerOverlay.fxml");
        emailDetailsOverlay = new Overlay("/de/ju/client/fxml/overlay/EmailDetailsOverlay.fxml");
    }

    private void initializeEmailListView() {
        emailListView.setDepthLevel(DepthLevel.LEVEL0);
        emailListView.setCellFactory(email -> new EmailListCell(emailListView, email));
        emailListView.getSelectionModel().selectionProperty().addListener((observable, oldValue, newValue) -> {
            Email selectedEmail = emailListView.getSelectionModel().getSelectedValue();
            if (selectedEmail != null) {
                emailDetailsOverlay.showOverlay(emailListView, selectedEmail);
            }
        });
    }

    @FXML
    private void populateEmailListView() {
        try {
            POP3Client pop3Client = new POP3Client("localhost", 110);
            pop3Client.authenticate(DataStore.getInstance().getJwtToken());

            List<String> emailIds = pop3Client.getList();
            List<Email> emails = new ArrayList<>();
            for (String id : emailIds) {
                Email email = pop3Client.getRetr(Integer.parseInt(id));
                if (email != null) {
                    emails.add(email);
                }
            }

            Collections.reverse(emails);

            emailListView.getItems().clear();
            emailListView.getItems().setAll(emails);
        } catch (FailedConnectionException | FailedAuthenticationException e) {
            throw new RuntimeException("Error while populating the email list view", e);
        }
    }

    @FXML
    private void onComposeEmail(ActionEvent event) {
        emailComposerOverlay.showOverlay((Node) event.getSource());
    }

    @FXML
    private void onAccountView(ActionEvent event) {
        openModal("/de/ju/client/fxml/view/AccountView.fxml", "Konto auswählen", 400, 500);
    }

    private void openModal(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent modalRoot = loader.load();

            Stage modalStage = new Stage();
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/de/ju/client/icon/icon.png")));
            modalStage.getIcons().add(icon);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle(title);
            modalStage.setResizable(false);

            Scene scene = new Scene(modalRoot, width, height);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/de/ju/client/css/style.css")).toExternalForm());
            modalStage.setScene(scene);
            modalStage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException("Error loading modal: " + fxmlPath, e);
        }
    }
}
