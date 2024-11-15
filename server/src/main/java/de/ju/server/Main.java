package de.ju.server;

public class Main {
    public static void main(String[] args) {
        String serverDomain = "smtp.example.com";

        SMTPServer smtpServer = new SMTPServer(587, serverDomain);
        smtpServer.run();

        POP3Server pop3Server = new POP3Server(110, serverDomain);
        pop3Server.run();
    }
}
