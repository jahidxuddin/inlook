package de.ju.server.http.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.ju.server.database.UserRepository;
import de.ju.server.entities.User;

import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;

public class UserHandler implements HttpHandler {
    private final UserRepository userRepository;

    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;

        if (method.equals("PUT")) {
            response = handleUpdateUser(exchange);
        } else {
            response = "Method Not Allowed";
            exchange.sendResponseHeaders(405, response.length());
        }

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private Map<String, String> getRequestBody(HttpExchange exchange) throws JsonProcessingException {
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        return new ObjectMapper().readValue(requestBody, new TypeReference<>() {
        });
    }

    private String handleUpdateUser(HttpExchange exchange) {
        try {
            Map<String, String> requestBody = getRequestBody(exchange);

            String email = requestBody.get("email");
            String newEmail = requestBody.get("newEmail");
            String newPassword = requestBody.get("newPassword");
            if (email == null || newEmail == null || newPassword == null || email.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
                throw new Exception("Error: Email, new email and new password are required.");
            }

            User existingUser = userRepository.getUser(email);
            if (existingUser == null) {
                throw new Exception("Error: User with this email does not exist.");
            }

            existingUser.setEmail(newEmail);
            existingUser.setPassword(newPassword);
            if (!userRepository.updateUser(existingUser)) {
                return "Error: Unable to update user. Please verify the details and try again.";
            }

            return "User Updated Successfully";
        } catch (IOException e) {
            return "Error: Unable to process the request.";
        } catch (Exception e) {
            return "Error: An unexpected error occurred.";
        }
    }
}
