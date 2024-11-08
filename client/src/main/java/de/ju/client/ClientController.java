package de.ju.client;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientController {

    @FXML
    private ListView<Email> emailListView;

    @FXML
    private Label welcomeText;

    // Beispiel für die E-Mail-Datenstruktur
    public static class Email {
        private String title;
        private String sender;
        private String date;

        public Email(String title, String sender, String date) {
            this.title = title;
            this.sender = sender;
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public String getSender() {
            return sender;
        }

        public String getDate() {
            return date;
        }
    }

    @FXML
    public void initialize() {
        ObservableList<Email> emails = FXCollections.observableArrayList(
            new Email("Betreff mit sehr viel Text, der umgebrochen werden soll, um das Design nicht zu überfluten", "email1@adresse.com", "01.11.2024"),
            new Email("Kurz betitelt", "email2@adresse.com", "02.11.2024")
        );

        emailListView.setItems(emails);

        // Definiere die cellFactory im Controller
        emailListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if (email != null) {
                    // Erstelle die Darstellung für jedes Listenelement
                    HBox hBox = new HBox(10);
                    Circle circle = new Circle(12);
                    circle.setStyle("-fx-fill: #0078d4;");

                    VBox vBox = new VBox();
                    Label titleLabel = new Label(email.getTitle());
                    titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-family: Arial, sans-serif;");
                    titleLabel.setWrapText(false); // Text wird nicht umgebrochen, sondern abgeschnitten
                    titleLabel.setMaxWidth(250);  // Maximale Breite für das Label

                    // Schränke die Länge des Textes ein und zeige "..." an, wenn der Text zu lang ist
                    titleLabel.setTextOverrun(OverrunStyle.ELLIPSIS);

                    Label senderLabel = new Label("Von: " + email.getSender());
                    senderLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777; -fx-font-family: Arial, sans-serif;");
                    Label dateLabel = new Label(email.getDate());
                    dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777; -fx-font-family: Arial, sans-serif;");

                    vBox.getChildren().addAll(titleLabel, senderLabel, dateLabel);
                    hBox.getChildren().addAll(circle, vBox);
                    setGraphic(hBox);
                } else {
                    setGraphic(null);
                }
            }
        });
    }
}
