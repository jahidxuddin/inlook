package de.ju.client.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ju.client.service.store.DataStore;
import de.ju.client.service.store.TokenFileHandler;
import de.ju.client.networking.HTTPClient;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class ManageAccountController {

    @FXML
    private VBox overlay;

    @FXML
    private MFXTextField emailField;

    @FXML
    private MFXTextField passwordField;

    @FXML
    private void closeOverlay(ActionEvent event) {
        Pane root = (Pane) overlay.getScene().getRoot();
        root.getChildren().remove(this.overlay);
    }

    @FXML
    private void onSave(ActionEvent event) {
        String email = emailField.getText() == null ? "" : emailField.getText();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        if (email.isEmpty() && password.isEmpty()) {
            return;
        }

        try {
            HttpClient client = HTTPClient.getInstance();
            updateUser(client, email, password);
            clearFields();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void updateUser(HttpClient client, String newEmail, String newPassword) throws IOException, InterruptedException {
        if (newEmail.equals(DataStore.getInstance().getEmail())) {
            String requestBody = String.format("{\"oldEmail\": \"%s\", \"newEmail\": \"%s\"}", DataStore.getInstance().getEmail(), newEmail);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/v1/email-transfer")).header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(requestBody)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return;
            }
        }

        String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", newEmail, newPassword);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:9000/api/v1/update-user")).header("Content-Type", "application/json").header("Authorization", DataStore.getInstance().getJwtToken()).PUT(HttpRequest.BodyPublishers.ofString(requestBody)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            return;
        }

        String jwtToken = (String) extractBody(response.body()).get("token");
        removeOldToken(client, jwtToken);
        DataStore.getInstance().setEmail(newEmail);
        DataStore.getInstance().setJwtToken(jwtToken);
    }

    private void removeOldToken(HttpClient client, String newJwtToken) {
        TokenFileHandler handler = new TokenFileHandler("tokens.txt");

        List<String> tokens = handler.readTokens();
        String oldToken = DataStore.getInstance().getJwtToken();
        if (tokens.contains(oldToken)) {
            handler.removeTokenByLine(tokens.indexOf(oldToken) + 1);
        }

        handler.writeToken(newJwtToken);
    }

    private String fetchToken(HttpClient client, String email, String password) throws IOException, InterruptedException {
        String requestBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:9000/api/v1/login")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return (String) extractBody(response.body()).get("token");
        }
        return null;
    }

    private Map<String, Object> extractBody(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseBody, Map.class);
    }

    private void clearFields() {
        emailField.setText("");
        passwordField.setText("");
    }
}
