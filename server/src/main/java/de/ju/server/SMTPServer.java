package de.ju.server;

import de.ju.server.networking.ServerSocket;
import de.ju.server.networking.Socket;

import java.io.IOException;
import java.util.*;

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
                return;
            }

            Map<String, String> mailCreationClientCommands = new LinkedHashMap<>();
            mailCreationClientCommands.put("MAIL FROM:", "250 OK");
            mailCreationClientCommands.put("RCPT TO:", "250 OK");
            mailCreationClientCommands.put("DATA", "354 OK");
            mailCreationClientCommands.put("Subject:", "250 OK");
            mailCreationClientCommands.put("QUIT", "250 Bye");
            for (Map.Entry<String, String> item : mailCreationClientCommands.entrySet()) {
                String command = item.getKey();
                String responseMessage = item.getValue();

                if (command.equals("QUIT")) {
                    System.out.println(command);
                    client.write(responseMessage + "\n");
                    break;
                }

                if (!waitForData(client, 5000)) break;
                response = client.readLine();

                if (!response.startsWith(command)) break;
                String data = response.substring(command.length());
                System.out.println(command + " " + data);
                client.write(responseMessage + "\n");
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
