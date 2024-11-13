package de.ju.server;

import de.ju.server.networking.ServerSocket;
import de.ju.server.networking.Socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SMTPServer {
    private static final List<Email> emails = new ArrayList<>();
    private ServerSocket sSocket;
    private Socket cSocket;
    private final String name = "smtp.server.com";
    private final String expectedUser = "testuser";
    private final String expectedPassword = "testpassword";
    private boolean authenticated = false;

    public SMTPServer() {
        try {
            int port = 1234;
            sSocket = new ServerSocket(port);
            cSocket = sSocket.accept();
            cSocket.write("220 smtp.example.com ESMTP Service Ready");
            System.out.println("connected!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process() throws IOException {
        try {
            greeting();
            while (!authenticated) {
                System.out.println("in dich rein");
                auth();
                System.out.println("in dich raus");
            }
            mailCreation();
        } catch (IOException e) {
            throw new IOException();
        }

    }

    public void greeting() throws IOException {
        String temp = "";
        while (cSocket.dataAvailable() <= 0) ;
        System.out.println("sex");
        temp = cSocket.readLine();
        System.out.println("hallo");
        System.out.println("C: " + temp);
        if (temp.startsWith("EHLO") || temp.startsWith("HELO")) {
            cSocket.write("250-" + name + " Hello " + temp.substring(5) + "\n");
        }
    }

    public void auth() throws IOException {
        String temp;
        while (cSocket.dataAvailable() <= 0) ;
        temp = cSocket.readLine();
        System.out.println(temp);
        if (temp.startsWith("AUTH LOGIN")) {
            System.out.println("AUTH login empfangen");
            cSocket.write("334 VXNlcm5hbWU6\n");
            String tempUser = new String(Base64.getDecoder().decode(cSocket.readLine()));
            cSocket.write("334 UGFzc3dvcmQ6\n");
            String tempPassword = new String(Base64.getDecoder().decode(cSocket.readLine()));
            if (tempUser.equalsIgnoreCase(expectedUser) && tempPassword.equals(expectedPassword)) {
                cSocket.write("235 Authentication successful\n");
                authenticated = true;
            } else {
                cSocket.write("Authentication failed\n");
            }
        }
    }

    public void mailCreation() throws IOException {
        Email tempEmail = new Email();
        String temp;
        while (true) {
            System.out.println("again!!!: " + cSocket.dataAvailable());
            while (cSocket.dataAvailable() <= 0);
            System.out.println(cSocket.dataAvailable());
            temp = cSocket.readLine();
            if (authenticated && temp.startsWith("MAIL FROM:")) {
                System.out.println("mail from");
                tempEmail.setSender(temp.substring(9));
                System.out.println(tempEmail.getSender());
                cSocket.write("250 OK\n");
            } else if (authenticated && temp.startsWith("RCPT TO:")) {
                tempEmail.setRecipient(temp.substring(8));
                System.out.println(tempEmail.getRecipient());
                cSocket.write("250 OK\n");
            } else if (authenticated && temp.startsWith("DATA")) {
                tempEmail.setBody(temp.substring(5));
                emails.add(tempEmail);
                cSocket.write("250 OK: Message Accepted\n");
            } else if (temp.equals("QUIT")) {
                cSocket.write("221 Bye\n");
                break;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        SMTPServer smtpServer = new SMTPServer();
        smtpServer.process();
    }
}
