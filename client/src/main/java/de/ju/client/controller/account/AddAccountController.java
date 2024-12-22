package de.ju.client.controller.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ju.client.data.TokenFileHandler;
import de.ju.client.lib.networking.HTTPClient;
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
import java.util.Map;

public class AddAccountController {
    @FXML
    public VBox overlay;
    @FXML
    public MFXTextField emailField;
    @FXML
    public MFXTextField passwordField;

    @FXML
    private void closeOverlay(ActionEvent event) {
        Pane root = (Pane) overlay.getScene().getRoot();
        root.getChildren().remove(this.overlay);
    }

    @FXML
    private void onCreate(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            return;
        }

        try {
            HttpClient client = HTTPClient.getInstance();

            if (registerUser(client, email, password) || loginUser(client, email, password)) {
                String token = fetchToken(client, email, password);
                if (token != null) {
                    TokenFileHandler handler = new TokenFileHandler("tokens.txt");
                    handler.writeToken(token);
                }
            }

            clearFields();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private boolean registerUser(HttpClient client, String email, String password) throws IOException, InterruptedException {
        String requestBody = createJsonRequestBody(email, password);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9000/api/v1/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    private boolean loginUser(HttpClient client, String email, String password) throws IOException, InterruptedException {
        String requestBody = createJsonRequestBody(email, password);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9000/api/v1/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    private String fetchToken(HttpClient client, String email, String password) throws IOException, InterruptedException {
        String requestBody = createJsonRequestBody(email, password);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9000/api/v1/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return extractTokenFromResponse(response.body());
        }
        return null;
    }

    private String createJsonRequestBody(String email, String password) {
        return String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);
    }

    private String extractTokenFromResponse(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(responseBody, Map.class);
        return (String) jsonMap.get("token");
    }

    private void clearFields() {
        emailField.setText("");
        passwordField.setText("");
    }
}
