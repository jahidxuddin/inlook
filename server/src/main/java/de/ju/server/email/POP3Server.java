package de.ju.server.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ju.server.database.EmailRepository;
import de.ju.server.entities.Email;
import de.ju.server.entities.User;
import de.ju.server.networking.HTTPClient;
import de.ju.server.networking.Socket;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class POP3Server extends Server {
    private static final String OK_RESPONSE = "+OK\n";
    private static final String GREETING_REQUEST = "+OK POP3 Server Ready\n";

    private final EmailRepository emailRepository;

    public POP3Server(int port, String serverDomain, EmailRepository emailRepository) {
        super(port, serverDomain);
        this.emailRepository = emailRepository;
    }

    @Override
    protected void handleClient(Socket client) throws IOException {
        greetClient(client);
        User user = authenticateClient(client);
        if (user == null) return;
        processMailCommands(client, user);
    }

    private void greetClient(Socket client) throws IOException {
        client.write(GREETING_REQUEST);
    }

    private User authenticateClient(Socket client) throws IOException {
        if (!waitForData(client, 5000)) {
            client.write("AUTH_FAILED: No data received within timeout.\n");
            return null;
        }
        String response = client.readLine();
        if (!response.startsWith("JWT")) {
            client.write("AUTH_FAILED: Missing or invalid JWT command.\n");
            return null;
        }
        String jwtToken = response.substring(4);
        HttpClient httpClient = HTTPClient.getInstance();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:9000/api/v1/find-user-by-jwt")).header("Content-Type", "application/json").header("Authorization", jwtToken).GET().build();

        User user = new User();
        try {
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                client.write("AUTH_FAILED: Invalid jwt token.\n");
                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = objectMapper.readValue(httpResponse.body(), Map.class);

            int id = (int) jsonMap.get("id");
            String email = (String) jsonMap.get("email");
            String password = (String) jsonMap.get("password");

            user.setId(id);
            user.setEmail(email);
            user.setPassword(password);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        client.write(OK_RESPONSE);
        return user;
    }

    private void processMailCommands(Socket client, User user) throws IOException {
        List<Integer> emailsToDelete = new ArrayList<>();
        while (waitForData(client, 5000)) {
            String commandLine = client.readLine();
            String status = commandLine.substring(0, 4).trim();
            if (status.startsWith("STAT")) {
                handleStat(client, user.getEmail());
            } else if (status.startsWith("LIST")) {
                handleList(client, user.getEmail());
            } else if (status.startsWith("RETR")) {
                String id = commandLine.substring(4).trim();
                handleRetr(client, Integer.parseInt(id));
            } else if (status.startsWith("DELE")) {
                String id = commandLine.substring(4).trim();
                handleDele(client, emailsToDelete, Integer.parseInt(id));
            } else if (status.startsWith("QUIT")) {
                handleQuit(client, emailsToDelete);
            }
        }
    }

    private void handleStat(Socket client, String userEmail) throws IOException {
        List<Email> allEmails = emailRepository.getAllEmailsFromUser(userEmail);
        int amountOfAllEmails = allEmails.size();

        int sizeOfAllEmail = 0;
        for (Email email : allEmails) {
            sizeOfAllEmail += email.calcSize();
        }

        client.write("+OK " + amountOfAllEmails + " " + sizeOfAllEmail + "\n");
    }

    private void handleList(Socket client, String userEmail) throws IOException {
        client.write(OK_RESPONSE);

        List<Email> allEmails = emailRepository.getAllEmailsFromUser(userEmail);
        for (Email email : allEmails) {
            client.write(email.getId() + " " + email.calcSize() + "\n");
        }

        client.write(".\n");
    }

    private void handleRetr(Socket client, int emailId) throws IOException {
        Email email = emailRepository.getEmailById(emailId);
        if (email == null) return;
        client.write("+OK " + email.calcSize() + " octets " + email + "\n");
    }

    private void handleDele(Socket client, List<Integer> emailsToDelete, int emailId) throws IOException {
        emailsToDelete.add(emailId);
        client.write(OK_RESPONSE);
    }

    private void handleQuit(Socket client, List<Integer> emailsToDelete) throws IOException {
        client.write("+OK Server signing off\n");
        for (Integer id : emailsToDelete) {
            if (emailRepository.getEmailById(id) == null) continue;
            emailRepository.deleteEmail(id);
        }
    }
}
