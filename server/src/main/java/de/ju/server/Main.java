package de.ju.server;

import de.ju.server.database.EmailRepository;
import de.ju.server.database.UserRepository;
import de.ju.server.protocols.POP3Server;
import de.ju.server.protocols.SMTPServer;

public class Main {
    public static void main(String[] args) {
        String serverDomain = "truyou.com";
        EmailRepository emailRepository = new EmailRepository();
        UserRepository userRepository = new UserRepository();

        SMTPServer smtpServer = new SMTPServer(587, serverDomain, emailRepository, userRepository);
        POP3Server pop3Server = new POP3Server(110, serverDomain, emailRepository, userRepository);
        smtpServer.run();
        pop3Server.run();
    }
}
