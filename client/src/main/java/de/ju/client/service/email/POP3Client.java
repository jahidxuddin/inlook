package de.ju.client.service.email;

import de.ju.client.service.email.exception.FailedAuthenticationException;
import de.ju.client.service.email.exception.FailedConnectionException;
import de.ju.client.model.Email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class POP3Client extends Client {
    public POP3Client(String hostname, int port) throws FailedConnectionException {
        super(hostname, port);
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

    public void authenticate(String jwtToken) throws FailedAuthenticationException, FailedConnectionException {
        if (sendAndCheck("JWT " + jwtToken, "+OK")) {
            throw new FailedAuthenticationException("Failed authentication.");
        }
    }

    public void getStat() throws FailedConnectionException {
        String response = sendCommand("STAT");
        if (!response.startsWith("+OK")) return;
        String[] responseParts = response.split(" ");
        String emailsAmount = responseParts[1];
        String emailsSize = responseParts[2];
    }

    public List<String> getList() throws FailedConnectionException {
        if (sendAndCheck("LIST", "+OK")) return List.of();
        List<String> emailIds = new ArrayList<>();
        try {
            String response;
            while (!(response = this.socket.readLine()).equals(".")) {
                logServerResponse(response);
                String[] responseParts = response.split(" ");
                String id = responseParts[0];
                emailIds.add(id);
            }
            logServerResponse(response);
            return emailIds;
        } catch (IOException e) {
            throw new FailedConnectionException("Failed to get list.", e);
        }
    }

    public Email getRetr(int emailId) throws FailedConnectionException {
        String response = sendCommand("RETR " + emailId);
        if (!response.startsWith("+OK")) {
            return null;
        }
        String[] responseParts = response.split(" ");
        String emailJson = response.substring(response.indexOf(responseParts[3].charAt(0)));
        return Email.parseEmail(emailJson);
    }

    public boolean dele(int emailId) throws FailedConnectionException {
        return !sendAndCheck("DELE " + emailId, "+OK");
    }

    public boolean quit() throws FailedConnectionException {
        return !sendAndCheck("QUIT", "+OK Server signing off");
    }
}
