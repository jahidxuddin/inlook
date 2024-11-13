package de.ju.server;

import de.ju.server.networking.ServerSocket;
import de.ju.server.networking.Socket;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Queue;

public class SMTPServer {
    private final ServerSocket serverSocket;
    private final Queue<Socket> clientQueue;
    private final String serverDomain;
    private volatile boolean isRunning;

    public SMTPServer(int port, String serverDomain) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.clientQueue = new LinkedList<>();
        this.serverDomain = serverDomain;
        this.isRunning = false;
    }

    public void run() {
        Thread clientListener = new Thread(this::listenForClients);
        Thread clientHandler = new Thread(this::handleClients);

        this.isRunning = true;
        clientListener.start();
        clientHandler.start();
    }

    private void listenForClients() {
        while (isRunning) {
            try {
                Socket client = this.serverSocket.accept();
                this.clientQueue.add(client);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleClients() {
        while (isRunning) {
            Socket client = this.clientQueue.poll();
            if (client != null) {
                new Thread(() -> handleClient(client)).start();
            }
        }
    }

    private void handleClient(Socket client) {
        try {
            client.write("220 " + this.serverDomain + " ESMTP Service Ready");

            if (!waitForData(client, 5000)) return;
            String response = client.readLine();
            if (response.startsWith("EHLO") || response.startsWith("HELO")) {
                client.write("250-" + this.serverDomain + " Hello " + response.substring(5) + "\n");
            }

            if (!waitForData(client, 5000) || !client.readLine().startsWith("AUTH LOGIN")) return;
            client.write("334 VXNlcm5hbWU6\n");

            if (!waitForData(client, 5000)) return;
            String clientEmail = decodeBase64(client.readLine());
            client.write("334 UGFzc3dvcmQ6\n");

            if (!waitForData(client, 5000)) return;
            String clientPassword = decodeBase64(client.readLine());

            if ("username".equals(clientEmail) && "password".equals(clientPassword)) {
                client.write("235 Authentication successful\n");
            } else {
                client.write("535 Authentication failed\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean waitForData(Socket client, long timeoutMillis) throws IOException {
        long startTime = System.currentTimeMillis();
        while (client.dataAvailable() <= 0) {
            if (System.currentTimeMillis() - startTime > timeoutMillis) {
                Thread.currentThread().interrupt();
                return false;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return true;
    }

    private String decodeBase64(String encoded) {
        return new String(Base64.getDecoder().decode(encoded));
    }

    public static void main(String[] args) {
        SMTPServer smtpServer = new SMTPServer(1234, "smtp.example.com");
        smtpServer.run();
    }
}

/* TODO: Integrate this algorithm
    public void mailCreation() throws IOException {
        Email tempEmail = new Email();
        while (true) {
            while (this.socket.dataAvailable() <= 0) ;
            String temp = this.socket.readLine();
            if (authenticated && temp.startsWith("MAIL FROM:")) {
                System.out.println("mail from");
                tempEmail.setSender(temp.substring(9));
                System.out.println(tempEmail.getSender());
                this.socket.write("250 OK\n");
            } else if (authenticated && temp.startsWith("RCPT TO:")) {
                tempEmail.setRecipient(temp.substring(8));
                System.out.println(tempEmail.getRecipient());
                this.socket.write("250 OK\n");
            } else if (authenticated && temp.startsWith("DATA")) {
                tempEmail.setBody(temp.substring(5));
                emails.add(tempEmail);
                this.socket.write("250 OK: Message Accepted\n");
            } else if (temp.equals("QUIT")) {
                this.socket.write("221 Bye\n");
                break;
            }
        }
    }
*/