package de.ju.server;

import de.ju.server.networking.Socket;

import java.io.IOException;
import java.util.*;

public class SMTPServer extends Server {
    public SMTPServer(int port, String serverDomain) {
        super(port, serverDomain);
    }

    @Override
    protected void handleClient(Socket client) {
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

    private String decodeBase64(String encoded) {
        return new String(Base64.getDecoder().decode(encoded));
    }
}
