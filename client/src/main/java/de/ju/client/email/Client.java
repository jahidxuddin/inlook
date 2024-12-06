package de.ju.client.email;

import de.ju.client.email.exception.FailedConnectionException;
import de.ju.client.lib.networking.Socket;

import java.io.IOException;
import java.util.Base64;

public abstract class Client {
    protected final Socket socket;

    public Client(String hostname, int port) {
        this.socket = new Socket(hostname, port);
    }

    protected abstract void initiateConnection() throws FailedConnectionException;

    protected boolean sendAndCheck(String command, String expectedStart) throws FailedConnectionException {
        String response = sendCommand(command);
        return !response.startsWith(expectedStart);
    }

    protected String sendCommand(String command) throws FailedConnectionException {
        try {
            logClientCommand(command);
            this.socket.write(command + "\n");

            waitForServerResponse();
            String response = this.socket.readLine();
            logServerResponse(response);

            return response;
        } catch (IOException e) {
            throw new FailedConnectionException("Failed to communicate with the server.", e);
        }
    }

    protected void waitForServerResponse() throws IOException {
        while (this.socket.dataAvailable() <= 0) ;
    }

    protected void writeWithDelay(String message, int delay) throws IOException {
        this.socket.write(message + "\n");
        logClientCommand(message);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }

    protected void logClientCommand(String command) {
        System.out.println("C: " + command);
    }

    protected void logServerResponse(String response) {
        System.out.println("S: " + response);
    }
}
