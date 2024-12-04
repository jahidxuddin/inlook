package de.ju.client.auth.token;

import java.nio.file.Files;
import java.nio.file.Path;

public class TokenFileManager {
    public static Path getTokenFilePath() throws Exception {
        Path appDir = Path.of(System.getProperty("user.home"), ".myapp");
        if (!Files.exists(appDir)) {
            Files.createDirectories(appDir);
        }
        return appDir.resolve("tokens.json.enc");
    }
}
