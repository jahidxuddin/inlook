package de.ju.client.data;

public class DataStore {
    private static final DataStore instance = new DataStore();
    private String jwtToken;
    private String email;

    private DataStore() { }

    public static DataStore getInstance() {
        return instance;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
