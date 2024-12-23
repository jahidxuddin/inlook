package de.ju.client.controller.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ju.client.data.DataStore;
import de.ju.client.data.TokenFileHandler;
import de.ju.client.lib.networking.HTTPClient;
import de.ju.client.lib.ui.Overlay;
import io.github.palexdev.materialfx.controls.MFXButton;
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
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class AccountController {
    private static final String HOME_VIEW_PATH = "/de/ju/client/fxml/view/HomeView.fxml";
    private static final String MANAGE_ACCOUNT_OVERLAY_PATH = "/de/ju/client/fxml/overlay/ManageAccountOverlay.fxml";
    private static final String ADD_ACCOUNT_OVERLAY_PATH = "/de/ju/client/fxml/overlay/AddAccountOverlay.fxml";
    private static final String EMAIL_API_URL = "http://localhost:9000/api/v1/email";
    private static final String ICON_PATH = "/de/ju/client/icon/icon.png";

    private Overlay manageAccountOverlay;
    private Overlay addAccountOverlay;
    private boolean isSelectionProcessing = false;

    @FXML
    private Text currentAccountEmail;
    @FXML
    private Text accountsHeading;
    @FXML
    private MFXListView<String> accountListView;
    @FXML
    private MFXButton manageAccountBtn;
    @FXML
    private MFXButton redirectToDashboardBtn;
    @FXML
    private MFXButton logoutAllBtn;

    @FXML
    public void initialize() {
        initializeOverlays();
        setupAccountEmail();
        initializeAccountListView();
        setupAccountsHeadingVisibility();
        setupSelectionListener();

        if (currentAccountEmail.getText().isEmpty()) {
            manageAccountBtn.setVisible(false);
            redirectToDashboardBtn.setVisible(false);
            logoutAllBtn.setVisible(false);
        }
    }

    private void initializeOverlays() {
        manageAccountOverlay = new Overlay(MANAGE_ACCOUNT_OVERLAY_PATH);
        addAccountOverlay = new Overlay(ADD_ACCOUNT_OVERLAY_PATH);
    }

    private void setupAccountEmail() {
        String token = getPrimaryToken();
        if (token == null || token.isEmpty()) return;

        DataStore.getInstance().setJwtToken(token);

        String email = fetchEmailForToken(token);
        if (email != null) {
            currentAccountEmail.setText(email);
            DataStore.getInstance().setEmail(email);
        }
    }

    private String getPrimaryToken() {
        TokenFileHandler handler = new TokenFileHandler("tokens.txt");
        return handler.getTokenByLine(1);
    }

    private String fetchEmailForToken(String token) {
        HttpRequest request = createEmailRequest(token);
        try {
            HttpResponse<String> response = HTTPClient.getInstance().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return parseEmailFromResponse(response.body());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching email: " + e.getMessage());
        }
        return null;
    }

    private HttpRequest createEmailRequest(String token) {
        return HttpRequest.newBuilder().uri(URI.create(EMAIL_API_URL)).header("Content-Type", "application/json").header("Authorization", token).GET().build();
    }

    private String parseEmailFromResponse(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(responseBody, Map.class);
        return (String) jsonMap.get("email");
    }

    private void initializeAccountListView() {
        accountListView.getItems().clear();
        accountListView.setDepthLevel(DepthLevel.LEVEL0);

        List<String> tokens = new TokenFileHandler("tokens.txt").readTokens();
        if (tokens.size() <= 1) return;

        List<String> emails = fetchEmailsForTokens(tokens);
        accountListView.getItems().addAll(emails);
    }

    private List<String> fetchEmailsForTokens(List<String> tokens) {
        List<String> emails = new ArrayList<>();
        for (String token : tokens) {
            if (DataStore.getInstance().getJwtToken().equals(token)) {
                continue;
            }
            String email = fetchEmailForToken(token);
            if (email != null) {
                emails.add(email);
            }
        }
        return emails;
    }

    private void setupAccountsHeadingVisibility() {
        accountsHeading.setVisible(!accountListView.getItems().isEmpty());
        accountListView.getItems().addListener((ListChangeListener<String>) change -> accountsHeading.setVisible(!accountListView.getItems().isEmpty()));
    }

    private void setupSelectionListener() {
        accountListView.getSelectionModel().selectionProperty().addListener((observable, oldValue, newValue) -> {
            if (isSelectionProcessing) {
                return;
            }

            isSelectionProcessing = true;
            try {
                String selectedEmail = accountListView.getSelectionModel().getSelectedValue();
                if (selectedEmail != null) {
                    swapEmails(selectedEmail);
                    Platform.runLater(() -> {
                        FXCollections.sort(accountListView.getItems());
                        accountListView.getSelectionModel().clearSelection();
                    });
                }
            } finally {
                isSelectionProcessing = false; // Freigabe des Flags, um weitere Events zuzulassen
            }
        });
    }

    private void swapEmails(String selectedEmail) {
        String currentEmail = currentAccountEmail.getText();
        accountListView.getItems().add(currentEmail);
        accountListView.getItems().remove(selectedEmail);
        currentAccountEmail.setText(selectedEmail);
        DataStore.getInstance().setEmail(selectedEmail);

        TokenFileHandler handler = new TokenFileHandler("tokens.txt");
        List<String> tokens = handler.readTokens();

        String matchingToken = "";
        for (String token : tokens) {
            String email = extractEmailFromToken(token);
            if (email != null && email.equals(selectedEmail)) {
                matchingToken = token;
                break;
            }
        }
        DataStore.getInstance().setJwtToken(matchingToken);
    }

    private String extractEmailFromToken(String token) {
        HttpClient client = HTTPClient.getInstance();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:9000/api/v1/find-user-by-jwt")).header("Content-Type", "application/json").header("Authorization", token).GET().build();

        HttpResponse<String> httpResponse;
        try {
            httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (httpResponse.statusCode() != 200) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap;
        try {
            jsonMap = objectMapper.readValue(httpResponse.body(), Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return (String) jsonMap.get("email");
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
            closeCurrentStage(event);
            dashboardStage.show();
        } catch (IOException e) {
            showErrorDialog("Error", "Could not load the dashboard view.");
        }
    }

    private Stage createDashboardStage(Parent root) {
        Stage dashboardStage = new Stage();
        dashboardStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(ICON_PATH))));
        dashboardStage.setTitle("Inlook - " + extractEmailPrefix(currentAccountEmail.getText()));
        dashboardStage.setScene(new Scene(root, 1024, 720));
        dashboardStage.setResizable(false);
        return dashboardStage;
    }

    private String extractEmailPrefix(String email) {
        return email.substring(0, email.indexOf("@"));
    }

    private void closeCurrentStage(ActionEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void onAddAccount(ActionEvent event) {
        addAccountOverlay.showOverlay((Node) event.getSource());
    }

    @FXML
    private void onLogoutAll(ActionEvent event) {
        if (confirmLogoutAll()) {
            logoutAllAccounts();
        }
    }

    private boolean confirmLogoutAll() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Abmeldung bestätigen");
        alert.setHeaderText("Sind Sie sicher, dass Sie alle Konten abmelden möchten?");
        alert.setContentText("Dadurch werden alle derzeit aktiven Konten abgemeldet.");
        return alert.showAndWait().filter(result -> result == ButtonType.OK).isPresent();
    }

    private void logoutAllAccounts() {
        new TokenFileHandler("tokens.txt").clearTokens();
        DataStore.getInstance().setJwtToken("");
        DataStore.getInstance().setEmail("");
        initializeAccountListView();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
