package de.ju.client.service.email;

import de.ju.client.service.email.exception.FailedAuthenticationException;
import de.ju.client.service.email.exception.FailedConnectionException;

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

    private static volatile SMTPClient instance;

    private SMTPClient(String hostname, int port, String email) {
        super(hostname, port);
        this.email = email;
    }

    @Override
    public void initiateConnection() throws FailedConnectionException {
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

    public static SMTPClient getInstance(String hostname, int port, String email) throws FailedConnectionException {
        if (instance == null) {
            synchronized (SMTPClient.class) {
                if (instance == null) {
                    instance = new SMTPClient(hostname, port, email);
                }
            }
        }
        return instance;
    }

    public void authenticate(String jwtToken) throws FailedAuthenticationException, FailedConnectionException {
        if (sendAndCheck(CMD_AUTH_LOGIN, RESPONSE_AUTH_USERNAME_PROMPT)) {
            throw new FailedAuthenticationException("Failed to start authentication.");
        }
        String response = sendCommand("JWT " + jwtToken);
        if (response.startsWith(RESPONSE_AUTH_FAILED) || !response.startsWith(RESPONSE_AUTH_SUCCESS)) {
            throw new FailedAuthenticationException("Invalid authentication credentials.");
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
}
