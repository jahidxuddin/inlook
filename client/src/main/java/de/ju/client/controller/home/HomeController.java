package de.ju.client.controller.home;

import de.ju.client.lib.ui.Overlay;
import de.ju.client.model.Email;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.effects.DepthLevel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static de.ju.client.email.EmailTimestampFormatter.formatEmailTimestamp;

public class HomeController {
    @FXML
    private MFXListView<Email> emailListView;
    private Overlay emailComposerOverlay;
    private Overlay emailDetailsOverlay;

    @FXML
    public void initialize() {
        initializeOverlays();
        initializeEmailListView();
        populateEmailListView();
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
        List<Email> emails = List.of(new Email("Welcome", "support@hotfemail.com", formatEmailTimestamp(LocalDateTime.now()), "Thank you for signing up!"), new Email("Product Update", "updates@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusMinutes(30)), "We have released a new feature."), new Email("Meeting Reminder", "hr@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(2)), "Don't forget about the team meeting tomorrow."), new Email("Security Alert", "security@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(8)), "Suspicious login detected. Please review your recent activity."), new Email("Password Reset", "no-reply@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(2)), "Click here to reset your password."), new Email("Invoice", "billing@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(1)), "Your invoice for November is attached."), new Email("Offer", "sales@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(6)), "Exclusive offer just for you!"), new Email("Event Invitation", "events@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(7)), "You're invited to our upcoming community event."));
        emailListView.getItems().addAll(emails);
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
