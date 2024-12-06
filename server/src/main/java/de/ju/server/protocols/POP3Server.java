package de.ju.server.protocols;

import de.ju.server.database.EmailRepository;
import de.ju.server.database.UserRepository;
import de.ju.server.entities.Email;
import de.ju.server.entities.User;
import de.ju.server.networking.Socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class POP3Server extends Server {
    private static final String OK_RESPONSE = "+OK\n";
    private static final String GREETING_REQUEST = "+OK POP3 Server Ready\n";

    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    public POP3Server(int port, String serverDomain, EmailRepository emailRepository, UserRepository userRepository) {
        super(port, serverDomain);
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
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
        if (!waitForData(client, 5000)) return null;
        String response = client.readLine();
        if (!response.startsWith("USER")) return null;

        String username = response.substring(5);
        User user = userRepository.getUser(username);
        if (user == null) return null;
        client.write(OK_RESPONSE);

        if (!waitForData(client, 5000)) return null;
        response = client.readLine();
        if (!response.startsWith("PASS")) return null;

        String password = decodeBase64(response.substring(5));
        if (!user.getPassword().equals(password)) return null;
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
