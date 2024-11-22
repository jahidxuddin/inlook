package de.ju.server.protocols;

import de.ju.server.database.EmailRepository;
import de.ju.server.database.UserRepository;
import de.ju.server.entities.Email;
import de.ju.server.entities.User;
import de.ju.server.networking.Socket;

import java.io.IOException;
import java.util.List;


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
            String status = response.substring(4);
            if (!waitForData(client, 5000)) break;
            switch (status) {
                case "STAT" : showStats();
                break;
                case "LIST" : list();
                break;
                case "RETR" : retr();
                break;
                case "DELE" : delete();
                break;
                case "QUIT" : close();
                break;
            }
        }
    }


    private String showStats(String username) {
        int size; //LETMECOOK
        for ()

        return (emailRepository.getAllEmailsFromUser(username).size() + " " + ); // 10
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

    }
}
