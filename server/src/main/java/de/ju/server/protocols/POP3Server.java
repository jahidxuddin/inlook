package de.ju.server.protocols;

import de.ju.server.database.EmailRepository;
import de.ju.server.database.UserRepository;
import de.ju.server.networking.Socket;

public class POP3Server extends Server {
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    public POP3Server(int port, String serverDomain, EmailRepository emailRepository, UserRepository userRepository) {
        super(port, serverDomain);
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected void handleClient(Socket client) {
    }
}
