package de.ju.client.controller.account;

import de.ju.client.lib.Overlay;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.effects.DepthLevel;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

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
        accountListView.getSelectionModel().selectionProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentAccountEmail.setText(newValue.values().stream().toList().getFirst());
            }
        });
        accountsHeading.setVisible(!accountListView.getItems().isEmpty());
    }

    @FXML
    private void onManageAccoutButton(ActionEvent event) {
        Overlay overlay = new Overlay((Node) event.getSource(), "/de/ju/client/fxml/overlay/ManageAccountOverlay.fxml");
        overlay.showOverlay((Node) event.getSource());
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
        Overlay overlay = new Overlay((Node) event.getSource(), "/de/ju/client/fxml/overlay/AddAccountOverlay.fxml");
        overlay.showOverlay((Node) event.getSource());
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
