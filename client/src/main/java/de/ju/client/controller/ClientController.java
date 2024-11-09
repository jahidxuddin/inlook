package de.ju.client.controller;

import de.ju.client.model.Email;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class ClientController {
    @FXML
    private ListView<Email> emailListView;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Label emailTitleLabel;
    @FXML
    private Label emailSenderLabel;
    @FXML
    private Label emailDateLabel;
    @FXML
    private TextArea emailContentTextArea;
    @FXML
    private VBox emailContentArea;

    private void loadEmails() {
        ObservableList<Email> emails = FXCollections.observableArrayList(
                new Email("Intervieweinladung: Marketing-Manager Position", "marcus.fischer22@careers.com", "22.11.2024"),
                new Email("Probleme beim Versand der letzten Bestellung", "thomas.hartmann21@shopservice.com", "21.11.2024"),
                new Email("Kostenlose Webinare für den kommenden Monat", "claudia.martin20@events.com", "20.11.2024"),
                new Email("Überarbeitete Version des Marketingplans", "daniela.schmidt19@marketingteam.com", "19.11.2024"),
                new Email("Wichtige Änderungen der Datenschutzbestimmungen", "lars.petersen18@privacy.com", "18.11.2024"),
                new Email("Überarbeitung der Arbeitszeiten: Feedback erbeten", "julia.hoffmann17@hr.com", "17.11.2024"),
                new Email("Verschiebung des Meetings auf nächste Woche", "tobias.lang16@consulting.net", "16.11.2024"),
                new Email("Verbesserungen am Dashboard", "helena.schreiber15@productteam.com", "15.11.2024"),
                new Email("Neue Partnerschaft mit Innovators Inc.", "patrick.koch14@partnerships.com", "14.11.2024"),
                new Email("Woche der offenen Tür - Einladung", "katja.stein13@marketing.de", "13.11.2024"),
                new Email("Fragen zu den aktuellen Rechnungen", "michael.bauer12@finance.org", "12.11.2024"),
                new Email("Update: Neue Funktionen in der App", "sandra.reiter11@mobiletech.com", "11.11.2024"),
                new Email("E-Mail-Archivierung: Wie Sie Daten sicher aufbewahren", "oliver.becker10@itservice.com", "10.11.2024"),
                new Email("Bestellung für Bürobedarf: Lieferung erwartet", "nina.wagner9@office.de", "09.11.2024"),
                new Email("Neue Öffnungszeiten ab nächster Woche", "mario.schneider8@shop.com", "08.11.2024"),
                new Email("Jahresbericht 2024: Erste Entwürfe", "lena.koch7@firma.org", "07.11.2024"),
                new Email("Erinnerung: Abgabe der Projektberichte bis Freitag", "benjamin.schulz6@consulting.de", "06.11.2024"),
                new Email("Feedback zum neuen Design-Entwurf", "anna.mueller5@designstudio.com", "05.11.2024"),
                new Email("Urlaubsantrag für Dezember", "clara.meier4@unternehmen.com", "04.11.2024"),
                new Email("Wichtige Sicherheitsupdates für Ihre Software", "luca.schmidt3@tech.org", "03.11.2024"),
                new Email("Neuigkeiten zur Produktveröffentlichung", "julia.musterfrau2@web.de", "02.11.2024"),
                new Email("Meeting am Montag: Besprechung zur Projektstrategie", "max.mustermann1@firma.com", "01.11.2024")
        );
        this.emailListView.setItems(emails);
    }

    @FXML
    public void initialize() {
        emailTitleLabel.setWrapText(true);
        loadEmails();
        initEmailListView();
        initSplitPane();

        // Initially hide the email content area
        emailContentArea.setVisible(false);
        emailContentArea.setManaged(false);
        // Key listener to open email on ENTER key only
        emailListView.setOnKeyPressed(this::handleKeyPress);
        // Enable keyboard focus for ListView to detect ENTER key
        emailListView.setFocusTraversable(true);
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            Email selectedEmail = emailListView.getSelectionModel().getSelectedItem();
            if (selectedEmail != null) {
                showEmailDetails(selectedEmail);
                emailContentArea.setVisible(true);
                emailContentArea.setManaged(true);
            }
        }
    }

    private void initEmailListView() {
        // Set the custom cell factory
        emailListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if (email == null) {
                    setGraphic(null);
                    return;
                }

                // Create the display for each email
                HBox hBox = new HBox(10);
                Circle circle = new Circle(12);
                circle.setStyle("-fx-fill: #0078d4;");

                VBox vBox = new VBox();
                Label titleLabel = new Label(email.title());
                titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: Arial, sans-serif;");
                titleLabel.setWrapText(true);

                Label senderLabel = new Label("Von: " + email.sender());
                senderLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777; -fx-font-family: Arial, sans-serif;");
                Label dateLabel = new Label(email.date());
                dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777; -fx-font-family: Arial, sans-serif;");

                vBox.getChildren().addAll(titleLabel, senderLabel, dateLabel);
                hBox.getChildren().addAll(circle, vBox);
                setGraphic(hBox);

                // Handle hover effect
                setOnMouseEntered(this::onMouseEntered);
                setOnMouseExited(this::onMouseExited);
                setOnMouseClicked(event -> {
                    showEmailDetails(email);
                    emailContentArea.setVisible(true);
                    emailContentArea.setManaged(true);
                });

                // Handle selection state changes
                selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                    if (isSelected) {
                        setStyle("-fx-background-color: lightblue;"); // Selected color
                    } else {
                        setStyle("-fx-background-color: transparent;"); // Unselected color
                    }
                });
            }

            // Hover effect when mouse enters
            private void onMouseEntered(MouseEvent event) {
                if (!isSelected()) {
                    setStyle("-fx-background-color: lightgray;");
                }
                setCursor(Cursor.HAND);
            }

            // Reset style when mouse exits
            private void onMouseExited(MouseEvent event) {
                if (!isSelected()) {
                    setStyle("-fx-background-color: transparent;");
                }
                setCursor(Cursor.DEFAULT);
            }
        });
    }

    private void initSplitPane() {
        // Restrict the SplitPane's divider position to 50% (maximum resizing)
        splitPane.setDividerPosition(0, 0.5);  // Ensure initial position is 50%

        // Prevent resizing beyond 50% of the width
        splitPane.getDividers().getFirst().positionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > 0.5) {
                splitPane.setDividerPosition(0, 0.5); // Reset if it exceeds 50%
            }
        });
    }

    private void showEmailDetails(Email email) {
        emailTitleLabel.setText(email.title());
        emailSenderLabel.setText("Von: " + email.sender());
        emailDateLabel.setText(email.date());
        emailContentTextArea.setText("""
                Dear John Pork,
                
                We are writing to inform you of an important update regarding your account with us.
                
                Our records show that there has been some unusual activity detected in your account. For your security, we have temporarily suspended your account until we can verify your recent transactions. We take account security very seriously and would like to resolve this issue as quickly as possible.
                
                To restore full access to your account, please follow these steps:
                
                Visit the account recovery page here.
                Verify your identity by answering the security questions.
                Once verified, your account will be reactivated immediately.
                If you did not attempt any of the transactions listed below, we recommend contacting us immediately to report any suspicious activity:
                
                $500 transfer to account ending in 1234
                Purchase of $150 at XYZ Electronics
                Login attempt from a new device in New York
                Your security is our top priority, and we are here to assist you every step of the way. Should you have any questions or need assistance with the account verification process, please do not hesitate to contact our support team at support@banking.com or call us directly at (800) 123-4567.
                
                Thank you for your prompt attention to this matter.
                
                Sincerely,
                
                The Banking.com Support Team""");
    }
}
