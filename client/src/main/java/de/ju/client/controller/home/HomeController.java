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
import java.util.Objects;

import static de.ju.client.service.email.EmailTimestampFormatter.formatEmailTimestamp;

public class HomeController {
    @FXML
    private MFXListView<Email> emailListView;
    @FXML
    private Overlay emailComposerOverlay;

    @FXML
    public void initialize() {
        emailComposerOverlay = new Overlay("/de/ju/client/fxml/overlay/EmailComposerOverlay.fxml");
        Overlay emailDetailsOverlay = new Overlay("/de/ju/client/fxml/overlay/EmailDetailsOverlay.fxml");
        emailListView.setDepthLevel(DepthLevel.LEVEL0);
        String emailContent = """
                Dear [Recipient’s Name],
               \s
                We hope this email finds you well! Our team is thrilled to share some exciting updates and new developments with you.
               \s
                1. **Launching Our New Feature** \s
                After months of hard work and collaboration, we’re proud to introduce our latest feature: *[Feature Name]*. This innovative solution is designed to make your experience even more seamless and enjoyable. \s
                Highlights include: \s
                - Enhanced performance and speed. \s
                - A sleek, user-friendly interface. \s
                - Comprehensive support and integration with your existing tools.
               \s
                To learn more, visit our website: [Link].
               \s
                2. **Upcoming Webinar: Join Us Live!** \s
                We’re hosting a live webinar on *[Date/Time]* to showcase our latest features and answer your questions. Don’t miss this opportunity to connect directly with our experts. \s
                *Topics covered:* \s
                - Overview of recent updates. \s
                - Best practices for maximizing value. \s
                - Live Q&A session.
               \s
                Register here: [Link].
               \s
                3. **Special Offer for Our Subscribers** \s
                As a valued member of our community, we’re excited to offer you an exclusive discount. For a limited time, enjoy *20% off* on our premium plans. Use the code *SPECIAL20* at checkout. \s
               \s
                Hurry, the offer expires on *[Date]*!
               \s
                Thank you for your support, \s
                [Your Name] \s
                *Position* \s
                *Company Name* \s
               \s""";
        emailListView.getItems().addAll(new Email("Welcome", "support@hotfemail.com", formatEmailTimestamp(LocalDateTime.now()), emailContent), new Email("Product Update", "updates@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusMinutes(30)), "We have released a new feature."), new Email("Meeting Reminder", "hr@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(2)), "Don't forget about the team meeting tomorrow."), new Email("New Subscription", "subscriptions@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(3)), "You have successfully subscribed to our service."), new Email("Security Alert", "security@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(8)), "Suspicious login detected. Please review your recent activity."), new Email("Password Reset", "no-reply@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(2)), "Click here to reset your password."), new Email("Invoice", "billing@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(1)), "Your invoice for November is attached."), new Email("Reminder: Payment Due", "billing@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(3)), "Your payment is due in 3 days."), new Email("Important Notification", "notifications@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(5)), "Please check your account for important updates."), new Email("Offer", "sales@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(6)), "Exclusive offer just for you!"), new Email("Event Invitation", "events@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(7)), "You're invited to our upcoming community event."), new Email("Feedback Request", "feedback@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(4)), "We value your feedback. Please take a moment to complete a survey."), new Email("Order Confirmation", "orders@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(10)), "Your order has been confirmed and will be shipped soon."), new Email("Newsletter", "newsletter@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusWeeks(1)), "Our latest newsletter is here!"), new Email("Welcome", "support@hotfemail.com", formatEmailTimestamp(LocalDateTime.now()), "Thank you for signing up!"), new Email("Product Update", "updates@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusMinutes(30)), "We have released a new feature."), new Email("Job Application", "careers@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusMonths(1)), "Your job application has been received."), new Email("Shipping Update", "shipping@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(2)), "Your package has been shipped and is on the way."), new Email("Welcome", "support@hotfemail.com", formatEmailTimestamp(LocalDateTime.now()), "Thank you for signing up!"), new Email("Product Update", "updates@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusMinutes(30)), "We have released a new feature."), new Email("Welcome", "support@hotfemail.com", formatEmailTimestamp(LocalDateTime.now()), "Thank you for signing up!"), new Email("Product Update", "updates@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusMinutes(30)), "We have released a new feature."), new Email("Welcome", "support@hotfemail.com", formatEmailTimestamp(LocalDateTime.now()), "Thank you for signing up!"), new Email("Product Update", "updates@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusMinutes(30)), "We have released a new feature."), new Email("Meeting Reminder", "hr@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(2)), "Don't forget about the team meeting tomorrow."), new Email("New Subscription", "subscriptions@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(3)), "You have successfully subscribed to our service."), new Email("Security Alert", "security@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(8)), "Suspicious login detected. Please review your recent activity."), new Email("Password Reset", "no-reply@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(2)), "Click here to reset your password."), new Email("Invoice", "billing@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(1)), "Your invoice for November is attached."), new Email("Welcome", "support@hotfemail.com", formatEmailTimestamp(LocalDateTime.now()), "Thank you for signing up!"), new Email("Product Update", "updates@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusMinutes(30)), "We have released a new feature."), new Email("Meeting Reminder", "hr@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(2)), "Don't forget about the team meeting tomorrow."), new Email("New Subscription", "subscriptions@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(3)), "You have successfully subscribed to our service."), new Email("Security Alert", "security@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(8)), "Suspicious login detected. Please review your recent activity."), new Email("Password Reset", "no-reply@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(2)), "Click here to reset your password."), new Email("Invoice", "billing@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(1)), "Your invoice for November is attached."), new Email("Welcome", "support@hotfemail.com", formatEmailTimestamp(LocalDateTime.now()), "Thank you for signing up!"), new Email("Product Update", "updates@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusMinutes(30)), "We have released a new feature."), new Email("Meeting Reminder", "hr@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(2)), "Don't forget about the team meeting tomorrow."), new Email("New Subscription", "subscriptions@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(3)), "You have successfully subscribed to our service."), new Email("Security Alert", "security@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusHours(8)), "Suspicious login detected. Please review your recent activity."), new Email("Password Reset", "no-reply@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(2)), "Click here to reset your password."), new Email("Invoice", "billing@hotfemail.com", formatEmailTimestamp(LocalDateTime.now().minusDays(1)), "Your invoice for November is attached."));
        emailListView.setCellFactory(email -> new EmailListCell(emailListView, email));
        emailListView.getSelectionModel().selectionProperty().addListener((observable, oldValue, newValue) -> {
            Email selectedEmail = emailListView.getSelectionModel().getSelectedValue();
            emailDetailsOverlay.showOverlay(emailListView, selectedEmail);
        });
    }

    @FXML
    private void onComposeEmail(ActionEvent event) {
        emailComposerOverlay.showOverlay((Node) event.getSource());
    }

    @FXML
    private void onAccountView(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/ju/client/fxml/view/AccountView.fxml"));
        Parent modalRoot;
        try {
            modalRoot = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(((Node) event.getSource()).getScene().getWindow()); // Blocks interaction with primary stage
        modalStage.setScene(new Scene(modalRoot, 400, 500));
        modalStage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/de/ju/client/css/style.css")).toExternalForm());
        modalStage.setTitle("Konto auswählen");
        modalStage.setResizable(false);
        modalStage.showAndWait();
    }
}
