package de.ju.server.service.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.ju.server.database.EmailRepository;
import de.ju.server.database.UserRepository;

import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;

public class EmailTransferHandler implements HttpHandler {
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    public EmailTransferHandler(UserRepository userRepository, EmailRepository emailRepository) {
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        if (!method.equals("PUT")) {
            response = "Method Not Allowed";
            exchange.sendResponseHeaders(405, response.length());
        } else {
            response = handleTransferEmailsToAnother(getRequestBody(exchange));
        }

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String handleTransferEmailsToAnother(Map<String, String> requestBody) {
        try {
            String oldEmail = requestBody.get("oldEmail");
            String newEmail = requestBody.get("newEmail");
            if (userRepository.getUser(oldEmail) == null || userRepository.getUser(newEmail) == null) {
                return "Old user or new user was not found";
            }

            emailRepository.transferEmails(oldEmail, newEmail);
            return "Email transfered successfully";
        } catch (Exception e) {
            return "An unexpected error occurred";
        }
    }

    private Map<String, String> getRequestBody(HttpExchange exchange) throws JsonProcessingException {
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        return new ObjectMapper().readValue(requestBody, new TypeReference<>() {
        });
    }
}
