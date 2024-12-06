package de.ju.client.email;

import de.ju.client.email.exception.FailedAuthenticationException;
import de.ju.client.email.exception.FailedConnectionException;

import java.io.IOException;

public class POP3Client extends Client {
    private final String email;

    public POP3Client(String hostname, int port, String email) throws FailedConnectionException {
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

            if (!response.equals("+OK POP3 Server Ready")) {
                throw new FailedConnectionException("Server did not provide a +OK response on connection initiation.");
            }
        } catch (IOException e) {
            throw new FailedConnectionException("Failed during connection initiation.", e);
        }
    }

    public void authenticate(String password) throws FailedAuthenticationException, FailedConnectionException {
        if (sendAndCheck("USER " + email, "+OK")) {
            throw new FailedAuthenticationException("Failed to start authentication.");
        }

        if (sendAndCheck("PASS " + encodeBase64(password), "+OK")) {
            throw new FailedAuthenticationException("Failed to start authentication.");
        }
    }

    public void getStat() throws FailedConnectionException {
        String response = sendCommand("STAT");
        if (!response.startsWith("+OK")) return;
        String[] responseParts = response.split(" ");
        String emailsAmount = responseParts[1];
        String emailsSize = responseParts[2];
    }

    public void getList() throws FailedConnectionException {
        if (sendAndCheck("LIST", "+OK")) return;
        try {
            String response;
            while (!(response = this.socket.readLine()).equals(".")) {
                logServerResponse(response);
                String[] responseParts = response.split(" ");
                String id = responseParts[0];
                String emailSize = responseParts[1];
            }
            logServerResponse(response);
        } catch (IOException e) {
            throw new FailedConnectionException("Failed to get list.", e);
        }
    }

    public void getRetr(int emailId) throws FailedConnectionException {
        String response = sendCommand("RETR " + emailId);
        if (!response.startsWith("+OK")) {
            return;
        }
        String[] responseParts = response.split(" ");
        String emailSize = responseParts[1];
        String email = responseParts[3];
    }

    public boolean dele(int emailId) throws FailedConnectionException {
        return !sendAndCheck("DELE " + emailId, "+OK");
    }

    public boolean quit() throws FailedConnectionException {
        return !sendAndCheck("QUIT", "+OK Server signing off");
    }

    public static void main(String[] args) {
        try {
            POP3Client pop3Client = new POP3Client("localhost", 110, "jahid.uddin@hotfemail.com");
            pop3Client.authenticate("hallo1234");
            pop3Client.getStat();
            pop3Client.getList();
            pop3Client.getRetr(7);
            if (pop3Client.dele(7)) {
                System.out.println("Deletion succed");
            }
            if (pop3Client.quit()) {
                System.out.println("Quit successful");
            }
        } catch (FailedConnectionException | FailedAuthenticationException e) {
            throw new RuntimeException(e);
        }
    }
}
