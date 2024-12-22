package de.ju.client.controller.home;

import de.ju.client.data.DataStore;
import de.ju.client.email.client.POP3Client;
import de.ju.client.email.exception.FailedAuthenticationException;
import de.ju.client.email.exception.FailedConnectionException;
import de.ju.client.email.model.Email;
import de.ju.client.lib.ui.Overlay;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeController {
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
            emails = emails.reversed();
            emailListView.getItems().addAll(emails);
        } catch (FailedConnectionException | FailedAuthenticationException e) {
            throw new RuntimeException(e);
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
