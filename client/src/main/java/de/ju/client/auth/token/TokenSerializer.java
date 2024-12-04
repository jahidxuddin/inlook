package de.ju.client.auth.token;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class TokenSerializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(Map<String, String> tokenMap) throws Exception {
        return objectMapper.writeValueAsString(tokenMap);
    }

    public static Map<String, String> deserialize(String json) throws Exception {
        return objectMapper.readValue(json, HashMap.class);
    }
}
