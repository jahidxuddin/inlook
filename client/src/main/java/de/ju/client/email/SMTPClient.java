package de.ju.client.email;

import de.ju.client.email.exception.FailedAuthenticationException;
import de.ju.client.email.exception.FailedConnectionException;

import java.io.IOException;

public class SMTPClient extends Client {
    private static final String CMD_HELO = "HELO %s";
    private static final String CMD_MAIL_FROM = "MAIL FROM:<%s>";
    private static final String CMD_RCPT_TO = "RCPT TO:<%s>";
    private static final String CMD_DATA = "DATA";
    private static final String CMD_QUIT = "QUIT";
    private static final String CMD_AUTH_LOGIN = "AUTH LOGIN";
    private static final String RESPONSE_READY = "220";
    private static final String RESPONSE_OK = "250";
    private static final String RESPONSE_START_INPUT = "354";
    private static final String RESPONSE_AUTH_SUCCESS = "235";
    private static final String RESPONSE_AUTH_FAILED = "535";
    private static final String RESPONSE_AUTH_USERNAME_PROMPT = "334";

    private final String email;

    public SMTPClient(String hostname, int port, String email) throws FailedConnectionException {
        super(hostname, port);
        this.email = email;
        initiateConnection();
    }

    @Override
    protected void initiateConnection() throws FailedConnectionException {
        if (!this.socket.connect()) {
            throw new FailedConnectionException("Connection initiation failed: server did not respond as expected.");
        }

        try {
            waitForServerResponse();
            String response = this.socket.readLine();
            logServerResponse(response);

            if (!response.startsWith(RESPONSE_READY)) {
                throw new FailedConnectionException("Server did not provide a 220 response on connection initiation.");
            }

            if (sendAndCheck(String.format(CMD_HELO, this.email), RESPONSE_OK)) {
                throw new FailedConnectionException("Server did not respond with 250 to HELO command.");
            }
        } catch (IOException e) {
            throw new FailedConnectionException("Failed during connection initiation.", e);
        }
    }

    public void authenticate(String password) throws FailedAuthenticationException, FailedConnectionException {
        if (sendAndCheck(CMD_AUTH_LOGIN, RESPONSE_AUTH_USERNAME_PROMPT)) {
            throw new FailedAuthenticationException("Failed to start authentication.");
        }
        if (sendAndCheck(encodeBase64(this.email), RESPONSE_AUTH_USERNAME_PROMPT)) {
            throw new FailedAuthenticationException("Email rejected during authentication.");
        }

        String response = sendCommand(encodeBase64(password));
        if (response.startsWith(RESPONSE_AUTH_FAILED)) {
            throw new FailedAuthenticationException("Invalid authentication credentials.");
        }
        if (!response.startsWith(RESPONSE_AUTH_SUCCESS)) {
            throw new FailedAuthenticationException("Unexpected response during authentication: " + response);
        }
    }

    public void sendMail(String to, String subject, String body) throws FailedConnectionException {
        if (sendAndCheck(String.format(CMD_MAIL_FROM, this.email), RESPONSE_OK)) {
            throw new FailedConnectionException("Failed to set sender address.");
        }
        if (sendAndCheck(String.format(CMD_RCPT_TO, to), RESPONSE_OK)) {
            throw new FailedConnectionException("Failed to set recipient address.");
        }
        if (sendAndCheck(CMD_DATA, RESPONSE_START_INPUT)) {
            throw new FailedConnectionException("Failed to initiate message data input.");
        }

        sendMailBody(subject, body);

        if (sendAndCheck(CMD_QUIT, RESPONSE_OK)) {
            throw new FailedConnectionException("Failed to terminate session properly.");
        }
    }

    private void sendMailBody(String subject, String body) throws FailedConnectionException {
        try {
            writeWithDelay("Subject: " + subject, 100);
            writeWithDelay(body, 100);
            writeWithDelay(".", 100);

            waitForServerResponse();
            String response = this.socket.readLine();
            logServerResponse(response);

            if (!response.startsWith(RESPONSE_OK)) {
                throw new FailedConnectionException("Failed to send message body.");
            }
        } catch (IOException e) {
            throw new FailedConnectionException("Error during message body transmission.", e);
        }
    }

    public static void main(String[] args) {
        try {
            SMTPClient smtpClient = new SMTPClient("localhost", 587, "jahid.uddin@hotfemail.com");
            smtpClient.authenticate("hallo1234");
            smtpClient.sendMail("muharrem.avci@hotfemail.com", "Hello World", "Das ist ein Test.");
        } catch (FailedConnectionException | FailedAuthenticationException e) {
            System.err.println(e.getMessage());
        }
    }
}
