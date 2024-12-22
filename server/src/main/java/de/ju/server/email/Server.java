package de.ju.server.email;

import de.ju.server.networking.ServerSocket;
import de.ju.server.networking.Socket;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Server {
    private final ServerSocket serverSocket;
    private final Queue<Socket> clientQueue;
    protected final String serverDomain;
    private volatile boolean isRunning;

    public Server(int port, String serverDomain) {
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
                new Thread(() -> {
                    try {
                        handleClient(client);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } finally {
                        cleanUpClient(client);
                    }
                }).start();
            }
        }
    }

    protected abstract void handleClient(Socket client) throws IOException;

    private void cleanUpClient(Socket client) {
        try {
            client.close();
        } catch (IOException e) {
            System.err.println("Error closing client connection: " + e.getMessage());
        }
    }

    protected boolean waitForData(Socket client, long timeoutMillis) throws IOException {
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

    protected String decodeBase64(String encoded) {
        return new String(Base64.getDecoder().decode(encoded));
    }
}
