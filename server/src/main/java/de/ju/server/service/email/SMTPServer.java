package de.ju.server.service.email;

import de.ju.server.database.EmailRepository;
import de.ju.server.database.UserRepository;
import de.ju.server.entity.Email;
import de.ju.server.networking.Socket;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SMTPServer extends Server {
    private static final String OK_RESPONSE = "250 OK\n";
    private static final String BYE_RESPONSE = "250 Bye\n";
    private static final String GREETING_REQUEST = "220 %s ESMTP Service Ready\n";
    private static final String GREETING_RESPONSE = "250 %s Hello %s\n";
    private static final String AUTH_LOGIN = "AUTH LOGIN";
    private static final String REQUEST_JWT = "334 SldU\n";
    private static final String AUTH_SUCCESS_RESPONSE = "235 Authentication successful\n";
    private static final String AUTH_FAILED_RESPONSE = "535 Authentication failed\n";
    private static final String DATA_RESPONSE = "354 End data with <CR><LF>.<CR><LF>\n";
    private static final String INVALID_COMMAND_RESPONSE = "502 Command not implemented\n";

    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    public SMTPServer(int port, String serverDomain, EmailRepository emailRepository, UserRepository userRepository) {
        super(port, serverDomain);
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void handleClient(Socket client) throws IOException {
        if (!greetClient(client)) return;
        if (!authenticateClient(client)) return;
        processMailCommands(client);
    }

    private boolean greetClient(Socket client) throws IOException {
        client.write(String.format(GREETING_REQUEST, super.serverDomain));

        if (!waitForData(client, 5000)) return false;
        String response = client.readLine();
        if (!response.startsWith("HELO")) {
            client.write(INVALID_COMMAND_RESPONSE);
            return false;
        }
        String clientDomain = response.substring(5);

        client.write(String.format(GREETING_RESPONSE, super.serverDomain, clientDomain));
        return true;
    }

    private boolean authenticateClient(Socket client) throws IOException {
        if (!waitForData(client, 5000) || !client.readLine().equals(AUTH_LOGIN)) return false;

        client.write(REQUEST_JWT);
        if (!waitForData(client, 5000)) {
            client.write(AUTH_FAILED_RESPONSE);
            return false;
        }
        String jwtToken = client.readLine().substring(4);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9000/api/v1/verify-jwt"))
                .header("Content-Type", "application/json")
                .header("Authorization", jwtToken)
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                client.write(AUTH_FAILED_RESPONSE);
                return false;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        client.write(AUTH_SUCCESS_RESPONSE);
        return true;
    }

    private void processMailCommands(Socket client) throws IOException {
        Email email = new Email();
        while (true) {
            if (!waitForData(client, 5000)) break;
            String commandLine = client.readLine();

            if (commandLine.startsWith("MAIL FROM:")) {
                String senderMail = handleMailFrom(client, commandLine);
                if (this.userRepository.getUser(senderMail) == null) {
                    client.write("550 Mailbox unavailable\n");
                    break;
                }
                email.setSender(senderMail);
            } else if (commandLine.startsWith("RCPT TO:")) {
                String recipientMail = handleRecipient(client, commandLine);
                if (this.userRepository.getUser(recipientMail) == null) {
                    client.write("550 Mailbox unavailable\n");
                    break;
                }
                email.setRecipient(recipientMail);
            } else if (commandLine.startsWith("DATA")) {
                String data = handleData(client);
                email.setSubject(data.substring(9, data.indexOf("\n")));
                email.setBody(data.substring(data.indexOf("\n")));
            } else if (commandLine.startsWith("QUIT")) {
                handleQuit(client);
                this.emailRepository.storeEmail(email);
                break;
            } else {
                client.write(INVALID_COMMAND_RESPONSE);
                break;
            }
        }
    }

    private String handleMailFrom(Socket client, String commandLine) throws IOException {
        System.out.println("C: MAIL FROM: " + commandLine.substring(10));
        client.write(OK_RESPONSE);
        System.out.println("S: " + OK_RESPONSE);
        String rawEmail = commandLine.substring(10);
        return rawEmail.substring(1, rawEmail.length() - 1);
    }

    private String handleRecipient(Socket client, String commandLine) throws IOException {
        System.out.println("C: RCPT TO: " + commandLine.substring(8));
        client.write(OK_RESPONSE);
        System.out.println("S: " + OK_RESPONSE);
        String rawEmail = commandLine.substring(8);
        return rawEmail.substring(1, rawEmail.length() - 1);
    }

    private String handleData(Socket client) throws IOException {
        client.write(DATA_RESPONSE);
        System.out.println(DATA_RESPONSE);

        StringBuilder emailContent = new StringBuilder();
        while (waitForData(client, 5000)) {
            String response = client.readLine();
            if (response.startsWith(".")) break;
            emailContent.append(response).append("\n");
        }
        System.out.println("C: " + emailContent);

        client.write(OK_RESPONSE);
        System.out.println("S: " + OK_RESPONSE);

        return emailContent.toString();
    }

    private void handleQuit(Socket client) throws IOException {
        System.out.println("C: QUIT");
        client.write(BYE_RESPONSE);
        System.out.println("S: " + BYE_RESPONSE);
    }
}
