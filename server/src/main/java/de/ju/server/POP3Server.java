package de.ju.server;

import de.ju.server.networking.Socket;

public class POP3Server extends Server {
    public POP3Server(int port, String serverDomain) {
        super(port, serverDomain);
    }

    @Override
    protected void handleClient(Socket client) {

    }
}
