package de.ju.client.smtp;

import de.ju.client.networking.Socket;
import de.ju.client.smtp.exceptions.FailedAuthenticationException;
import de.ju.client.smtp.exceptions.FailedConnectionException;

import java.io.IOException;
import java.util.Base64;

public class SMTPClient {
    private final Socket socket;
    private final String email;

    public SMTPClient(String hostname, int port, String email) throws FailedConnectionException {
        this.socket = new Socket(hostname, port);
        this.email = email;
        initiateConnection();
    }

    private void initiateConnection() throws FailedConnectionException {
        if (!this.socket.connect() || !sendAndCheck("HELO " + this.email, "250")) {
            throw new FailedConnectionException("Connection initiation failed: server did not respond as expected.");
        }
    }

    public void authenticate(String password) throws FailedAuthenticationException, FailedConnectionException {
        if (sendAndCheck("AUTH LOGIN", "334 VXNlcm5hbWU6") || sendAndCheck(encodeBase64(this.email), "334 UGFzc3dvcmQ6")) {
            throw new FailedAuthenticationException("Authentication initiation failed or username rejected");
        }
        String response = sendCommand(encodeBase64(password));
        if (response.startsWith("535")) throw new FailedAuthenticationException("Invalid authentication credentials");
        if (!response.startsWith("235"))
            throw new FailedAuthenticationException("Unexpected response during authentication: " + response);
    }

    public void sendMail(String to, String subject, String body) throws FailedConnectionException {
        if (!sendAndCheck("MAIL FROM: <" + this.email + ">", "250"))
            throw new FailedConnectionException("Failed to set sender address");
        if (!sendAndCheck("RCPT TO: <" + to + ">", "250"))
            throw new FailedConnectionException("Failed to set recipient address");
        if (!sendAndCheck("DATA", "354")) throw new FailedConnectionException("Failed to start message data input");
        if (!sendAndCheck("Subject: " + subject + "\r\n\r\n" + body + "\r\n.", "250"))
            throw new FailedConnectionException("Failed to send message body");
    }

    private boolean sendAndCheck(String command, String expectedStart) throws FailedConnectionException {
        String response = sendCommand(command);
        return !response.startsWith(expectedStart);
    }

    private String sendCommand(String command) throws FailedConnectionException {
        try {
            this.socket.write(command);
            return this.socket.readLine();
        } catch (IOException e) {
            throw new FailedConnectionException("Failed to communicate with the server", e);
        }
    }

    private String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    public static void main(String[] args) {
        try {
            SMTPClient smtpClient = new SMTPClient("localhost", 1234, "john.pork@fls-wiesbaden.de");
            smtpClient.authenticate("password");
            smtpClient.sendMail("abishan.arankesan@outlook.de", "TEST", "Among Us");
        } catch (FailedConnectionException | FailedAuthenticationException e) {
            throw new RuntimeException(e);
        }
    }
}
