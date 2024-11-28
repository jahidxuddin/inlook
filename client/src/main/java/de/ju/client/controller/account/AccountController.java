package de.ju.client.controller.account;

import de.ju.client.lib.ui.Overlay;
import io.github.palexdev.materialfx.controls.MFXListView;
import io.github.palexdev.materialfx.effects.DepthLevel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import java.util.Objects;
import java.util.Optional;

public class AccountController {
    private static final String DEFAULT_EMAIL = "jahid.uddin@hotfemail.com";
    private static final String HOME_VIEW_PATH = "/de/ju/client/fxml/view/HomeView.fxml";
    private static final String MANAGE_ACCOUNT_OVERLAY_PATH = "/de/ju/client/fxml/overlay/ManageAccountOverlay.fxml";
    private static final String ADD_ACCOUNT_OVERLAY_PATH = "/de/ju/client/fxml/overlay/AddAccountOverlay.fxml";

    private Overlay manageAccountOverlay;
    private Overlay addAccountOverlay;

    @FXML
    private Text currentAccountEmail;
    @FXML
    private Text accountsHeading;
    @FXML
    private MFXListView<String> accountListView;

    @FXML
    public void initialize() {
        initializeOverlays();
        initializeAccountListView();
        setupAccountEmail();
        setupAccountsHeadingVisibility();
        setupSelectionListener();
    }

    private void initializeOverlays() {
        manageAccountOverlay = new Overlay(MANAGE_ACCOUNT_OVERLAY_PATH);
        addAccountOverlay = new Overlay(ADD_ACCOUNT_OVERLAY_PATH);
    }

    private void setupAccountEmail() {
        currentAccountEmail.setText(DEFAULT_EMAIL);
    }

    private void initializeAccountListView() {
        accountListView.setDepthLevel(DepthLevel.LEVEL0);
        accountListView.getItems().addAll(
                "user1@hotfemail.com",
                "user2@hotfemail.com",
                "user3@hotfemail.com",
                "user4@hotfemail.com"
        );
    }

    private void setupAccountsHeadingVisibility() {
        accountsHeading.setVisible(!accountListView.getItems().isEmpty());
        accountListView.getItems().addListener((ListChangeListener<String>) change ->
                accountsHeading.setVisible(!accountListView.getItems().isEmpty())
        );
    }

    private void setupSelectionListener() {
        accountListView.getSelectionModel().selectionProperty().addListener((observable, oldValue, newValue) -> {
            String selectedEmail = accountListView.getSelectionModel().getSelectedValue();
            Platform.runLater(() -> {

                if (selectedEmail != null) {
                    swapEmails(selectedEmail);
                }
                FXCollections.sort(accountListView.getItems());
                accountListView.getSelectionModel().clearSelection();
            });
        });
    }

    private void swapEmails(String selectedEmail) {
        String currentEmail = currentAccountEmail.getText();
        accountListView.getItems().add(currentEmail);
        accountListView.getItems().remove(selectedEmail);
        currentAccountEmail.setText(selectedEmail);
    }

    @FXML
    private void onManageAccount(ActionEvent event) {
        manageAccountOverlay.showOverlay((Node) event.getSource());
    }

    @FXML
    private void onRedirectToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(HOME_VIEW_PATH)));
            Stage dashboardStage = createDashboardStage(root);
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            currentStage.close();
            dashboardStage.show();
        } catch (IOException e) {
            showErrorDialog("Error", "Could not load the dashboard view.");
        }
    }

    private Stage createDashboardStage(Parent root) {
        Stage dashboardStage = new Stage();
        dashboardStage.setTitle("hotfemail.com - Home");
        dashboardStage.setScene(new Scene(root, 1024, 720));
        dashboardStage.setMinWidth(1024);
        dashboardStage.setMinHeight(720);
        dashboardStage.setMaximized(true);
        return dashboardStage;
    }

    @FXML
    private void onAddAccount(ActionEvent event) {
        addAccountOverlay.showOverlay((Node) event.getSource());
    }

    @FXML
    private void onLogoutAll(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Are you sure you want to log out all accounts?");
        alert.setContentText("This will log out all accounts currently active.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            logoutAllAccounts();
        } else {
            System.out.println("Logout canceled.");
        }
    }

    private void logoutAllAccounts() {
        // Actual logout logic goes here
        System.out.println("All users have been logged out!");
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
