package de.ju.server.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Socket {
    private final String hostname;
    private final int port;
    private java.net.Socket socket;
    private BufferedReader reader;

    public Socket(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public Socket(java.net.Socket socket) throws IOException {
        this.socket = socket;
        this.port = socket.getPort();
        this.hostname = socket.getRemoteSocketAddress().toString();
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void close() throws IOException {
        this.reader.close();
        this.socket.close();
    }

    public boolean connect() {
        try {
            this.socket = new java.net.Socket(this.hostname, this.port);
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            return true;
        } catch (Exception var2) {
            return false;
        }
    }

    public int dataAvailable() throws IOException {
        return this.socket.getInputStream().available();
    }

    public int read() throws IOException {
        return this.socket.getInputStream().read();
    }

    public int read(byte[] b, int len) throws IOException {
        return this.socket.getInputStream().read(b, 0, len);
    }

    public String readLine() throws IOException {
        return this.reader.readLine();
    }

    public void write(int b) throws IOException {
        this.socket.getOutputStream().write(b);
    }

    public void write(byte[] b, int len) throws IOException {
        this.socket.getOutputStream().write(b, 0, len);
    }

    public void write(String s) throws IOException {
        this.socket.getOutputStream().write(s.getBytes(StandardCharsets.UTF_8));
    }
}
