package de.ju.server.protocols;

import de.ju.server.database.EmailRepository;
import de.ju.server.database.UserRepository;
import de.ju.server.entities.Email;
import de.ju.server.entities.User;
import de.ju.server.networking.Socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


//Cursor resting area
//_________________
//|               |
//|               |
//|               |
//|_______________|
public class POP3Server extends Server {
    private static final String OK_RESPONSE = "+OK";
    private static final String GREETING_REQUEST = "+OK POP3 Server Ready";
    private final String USER = "USER";
    private final String PASS = "PASS";
    private Integer num_message;
    private byte size_message;
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;
    private User currentUser;
    List<Integer> emailsPrepredForDeletion = new ArrayList<>();



    public POP3Server(int port, String serverDomain, EmailRepository emailRepository, UserRepository userRepository) {
        super(port, serverDomain);
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void handleClient(Socket client) throws IOException {
        if (!greetClient(client)) return;
        if(!authenticateClient(client)) return;
        processMailCommands(client);
    }

    private void processMailCommands(Socket client) throws IOException {
        while (true) {
            String response = client.readLine();
            String status = response.substring(0, 3);
            int emailId;
            if (!waitForData(client, 5000)) break;
            switch (status) {
                case "STAT" :
                    client.write(OK_RESPONSE + " " + showStats());
                break;
                case "LIST" :
                    client.write(OK_RESPONSE);
                    client.write(list());
                    client.write(".");
                break;
                case "RETR" :
                    client.write(OK_RESPONSE + " " + retr(Integer.parseInt(response.substring(3))));
                break;
                case "DELE" :
                    emailsPrepredForDeletion.add(Integer.parseInt(response.substring(3)));
                break;
                case "QUIT" :
                    client.write(OK_RESPONSE);
                    close();
                break;
            }
        }
    }

    public boolean emailFound(int id) {
        if(emailRepository.getEmailById(id) != null) return true;
        return false;
    }

    private String retr(int emailId) {
        StringBuilder emailString = new StringBuilder();

        if (!emailFound(emailId)) return null;

        Email email = emailRepository.getEmailById(emailId);

        // Daten mit StringBuilder aneinanderhängen
        emailString.append(email.calcSize()).append(" octets\n")
                .append("From: ").append(email.getSender()).append("\n")
                .append("Recipient: ").append(email.getRecipient()).append("\n")
                .append("Body: ").append(email.getBody());

        return emailString.toString();
    }

    private boolean delete(int emailId) {
        if (!emailFound(emailId)) return false;
        return emailRepository.deleteEmail(emailId);
    }

    private void close() {
        for (Integer x : emailsPrepredForDeletion) {
            delete(x);
        }
        //hier muss die verbindung ordnungsgemäß geschlossen werden/ kb zu machen!
    }


    private String list() {
        List<Email> allEMails = emailRepository.getAllEmailsFromUser(currentUser.getEmail());
        StringBuilder output = new StringBuilder();
        for (Email e : allEMails) {
            output.append(e.getId()).append(" ").append(e.calcSize()).append("\n");
        }
        return output.toString();
    }


    private String showStats() {
        System.out.println();
        List<Email> allEMails = emailRepository.getAllEmailsFromUser(currentUser.getEmail());
        String output = Integer.toString(allEMails.size());
        byte tempSize = 0;

        // Normale for-Schleife
        for (Email e : allEMails) {
            tempSize += e.calcSize();
        }

        return output + " " + tempSize;
    }


    private boolean greetClient(Socket client) throws IOException {
        client.write(String.format(GREETING_REQUEST, super.serverDomain));
        return true;
    }

    private boolean authenticateClient(Socket client) throws IOException {
        if (!waitForData(client, 5000)) return false;
        String request = client.readLine();
        String username = request.substring(4);
        if (!request.startsWith(USER)) return false;
        if (userRepository.getUser(username) == null) return false;
        client.write(OK_RESPONSE);

        if (!waitForData(client, 5000)) return false;
        request = client.readLine();
        String password = request.substring(4);
        if (!request.startsWith(PASS)) return false;
        if (userRepository.getUser(password) == null) return false;
        client.write(OK_RESPONSE);
        currentUser = new User(userRepository.getUser(username));
        return true;
    }
}
