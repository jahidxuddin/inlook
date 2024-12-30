package de.ju.server.service.http;

import com.sun.net.httpserver.HttpServer;
import de.ju.server.database.EmailRepository;
import de.ju.server.database.UserRepository;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HTTPServer extends Thread {
    private final HttpServer server;
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;

    public HTTPServer(int port, UserRepository userRepository, EmailRepository emailRepository) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
    }

    private void startServer() {
        initHandlers();
        this.server.setExecutor(null);
        this.server.start();
    }

    private void initHandlers() {
        this.server.createContext("/api/v1/email-transfer", new EmailTransferHandler(userRepository, emailRepository));
    }

    @Override
    public void run() {
        startServer();
    }
}
