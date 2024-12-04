package de.ju.client.auth.token;

import java.util.HashMap;
import java.util.Map;

public class TokenManager {
    private final Map<String, String> tokenMap = new HashMap<>();

    public String getToken(String key) {
        return tokenMap.get(key);
    }

    public Map<String, String> getAllTokens() {
        return new HashMap<>(tokenMap);
    }

    public void setToken(String key, String token) {
        tokenMap.put(key, token);
    }

    public void removeToken(String key) {
        tokenMap.remove(key);
    }
}
