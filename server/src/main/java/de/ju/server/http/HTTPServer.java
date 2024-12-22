package de.ju.server.http;

import com.sun.net.httpserver.HttpServer;
import de.ju.server.database.UserRepository;
import de.ju.server.http.handlers.UserHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HTTPServer extends Thread {
    private final int PORT;
    private final HttpServer server;
    private final UserRepository userRepository;

    public HTTPServer(int port, UserRepository userRepository) throws IOException {
        this.PORT = port;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.userRepository = userRepository;
    }

    private void startServer() {
        initHandlers();
        this.server.setExecutor(null);
        this.server.start();
        System.out.println("HTTP Server started on port " + this.PORT);
    }

    private void initHandlers() {
        this.server.createContext("/api/v1/user", new UserHandler(this.userRepository));
    }

    @Override
    public void run() {
        startServer();
    }
}
