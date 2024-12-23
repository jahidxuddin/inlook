package de.ju.client.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TokenFileHandler {
    private final String filePath;

    public TokenFileHandler(String resourceFileName) {
        this.filePath = Objects.requireNonNull(getClass().getResource("/de/ju/client/" + resourceFileName)).getPath();
    }

    private void createFile() {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error creating file: " + e.getMessage());
        }
    }

    public void writeToken(String token) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(token);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing token: " + e.getMessage());
        }
    }

    public List<String> readTokens() {
        List<String> tokens = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tokens.add(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
        } catch (IOException e) {
            System.err.println("Error reading tokens: " + e.getMessage());
        }

        return tokens;
    }

    public void clearTokens() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

        } catch (IOException e) {
            System.err.println("Error clearing tokens: " + e.getMessage());
        }
    }

    public String getTokenByLine(int lineNumber) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentLine = 1;
            while ((line = reader.readLine()) != null) {
                if (currentLine == lineNumber) {
                    return line;
                }
                currentLine++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
        } catch (IOException e) {
            System.err.println("Error reading token by line: " + e.getMessage());
        }
        return null;
    }

    public void removeTokenByLine(int lineNumber) {
        List<String> tokens = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentLine = 1;
            while ((line = reader.readLine()) != null) {
                if (currentLine != lineNumber) {
                    tokens.add(line);
                }
                currentLine++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
            return;
        } catch (IOException e) {
            System.err.println("Error reading tokens for removal: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String token : tokens) {
                if (token != null) {
                    writer.write(token);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing tokens after removal: " + e.getMessage());
        }
    }
}
