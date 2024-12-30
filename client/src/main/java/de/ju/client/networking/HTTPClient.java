package de.ju.client.networking;

import java.net.http.HttpClient;

public class HTTPClient {
    private static final HttpClient INSTANCE = HttpClient.newHttpClient();

    private HTTPClient() {
        // Prevent instantiation
    }

    public static HttpClient getInstance() {
        return INSTANCE;
    }
}
