package de.ju.server;

import de.ju.server.database.EmailRepository;
import de.ju.server.database.UserRepository;
import de.ju.server.email.POP3Server;
import de.ju.server.email.SMTPServer;
import de.ju.server.http.HTTPServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String serverDomain = "inlook.com";
        EmailRepository emailRepository = new EmailRepository();
        UserRepository userRepository = new UserRepository();

        HTTPServer httpServer = new HTTPServer(8080, userRepository);
        SMTPServer smtpServer = new SMTPServer(587, serverDomain, emailRepository, userRepository);
        POP3Server pop3Server = new POP3Server(110, serverDomain, emailRepository);

        httpServer.start();
        smtpServer.run();
        pop3Server.run();
    }
}
