package de.ju.client.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.effects.DepthLevel;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import static de.ju.client.lib.Overlay.removeOverlay;
import static de.ju.client.lib.Overlay.showOverlay;

public class AccountController {
    @FXML
    private Text currentAccountEmail;
    @FXML
    private Text accountsHeading;
    @FXML
    private MFXListView<String> accountListView;

    @FXML
    public void initialize() {
        currentAccountEmail.setText("jahid.uddin@hotfemail.com");
        accountListView.setDepthLevel(DepthLevel.LEVEL0);
        accountListView.getItems().addAll("user1@hotfemail.com", "user2@hotfemail.com", "user3@hotfemail.com", "user4@hotfemail.com", "user5@hotfemail.com");
        accountListView.getItems().addListener((ListChangeListener<String>) change -> {
            // Toggle the visibility of the accountsHeading based on the size of the list
            accountsHeading.setVisible(!accountListView.getItems().isEmpty());
        });
        // Initial check to handle preloaded items
        accountsHeading.setVisible(!accountListView.getItems().isEmpty());
    }

    @FXML
    private void onManageAccoutButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        showOverlay(stage, (StackPane pane) -> {
            javafx.scene.text.Text overlayText = new javafx.scene.text.Text("Konto verwalten");
            overlayText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            MFXButton closeOverlayButton = new MFXButton("");
            closeOverlayButton.setStyle("-fx-background-color: transparent;");
            closeOverlayButton.setGraphic(new MFXFontIcon("fas-arrow-left", 22).setColor(new Color(0.102, 0.451, 0.910, 1.0)));
            closeOverlayButton.setOnAction(_ -> removeOverlay(stage, pane));

            HBox hBox = new HBox(5);
            hBox.setStyle("-fx-padding: 12px;");
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.getChildren().addAll(closeOverlayButton, overlayText);

            pane.getChildren().add(hBox);
        });
    }

    @FXML
    private void onRedirectToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/ju/client/fxml/view/HomeView.fxml"));
            Parent root = loader.load();

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("hotfemail.com - Home");
            dashboardStage.setScene(new Scene(root, 1024, 720));
            dashboardStage.setMinWidth(1024);
            dashboardStage.setMinHeight(720);
            dashboardStage.setMaximized(true);
            dashboardStage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onAddAccount(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        showOverlay(stage, (StackPane pane) -> {
            javafx.scene.text.Text overlayText = new javafx.scene.text.Text("Konto hinzufügen");
            overlayText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            MFXButton closeOverlayButton = new MFXButton("");
            closeOverlayButton.setStyle("-fx-background-color: transparent;");
            closeOverlayButton.setGraphic(new MFXFontIcon("fas-arrow-left", 22).setColor(new Color(0.102, 0.451, 0.910, 1.0)));
            closeOverlayButton.setOnAction(_ -> removeOverlay(stage, pane));

            HBox hBox = new HBox(5);
            hBox.setStyle("-fx-padding: 12px;");
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.getChildren().addAll(closeOverlayButton, overlayText);

            pane.getChildren().add(hBox);
        });
    }

    @FXML
    private void onLogoutAll(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Abmelden bestätigen");
        alert.setHeaderText("Sind Sie sicher, dass Sie alle abmelden möchten?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("Alle Benutzer wurden abgemeldet!");
        } else {
            System.out.println("Abmeldung wurde abgebrochen.");
        }
    }
}
