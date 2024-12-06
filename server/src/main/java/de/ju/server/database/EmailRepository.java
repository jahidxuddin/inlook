package de.ju.server.database;

import de.ju.server.entities.Email;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmailRepository {
    public List<Email> getAllEmailsFromUser(String username) {
        List<Email> emails = new ArrayList<>();

        String query = "SELECT * FROM Emails WHERE sender = (?)";
        try (Connection connection = DatabaseConnector.getInstance().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query); ResultSet resultSet = preparedStatement.executeQuery()) {
            preparedStatement.setString(1, username);

            while (resultSet.next()) {
                Email email = new Email(resultSet.getInt("id"), resultSet.getString("sender"), resultSet.getString("recipient"), resultSet.getString("subject"), resultSet.getString("body"));
                emails.add(email);
            }

            return emails;
        } catch (SQLException e) {
            return emails;
        }
    }

    public Email getEmailById(int id) {
        String query = "SELECT * FROM Emails WHERE id = (?)";
        try (Connection connection = DatabaseConnector.getInstance().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query); ResultSet resultSet = preparedStatement.executeQuery()) {
            preparedStatement.setString(1, Integer.toString(id));
            return new Email(resultSet.getInt("id"), resultSet.getString("sender"), resultSet.getString("recipient"), resultSet.getString("subject"), resultSet.getString("body"));
        } catch (SQLException e) {
            return null;
        }
    }

    public void storeEmail(Email email) {
        String query = "INSERT INTO Emails (sender, recipient, subject, body) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnector.getInstance().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email.getSender());
            preparedStatement.setString(2, email.getRecipient());
            preparedStatement.setString(3, email.getSubject());
            preparedStatement.setString(4, email.getBody());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteEmail(int id) {
        String query = "DELETE FROM Emails WHERE id = ?";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false); // Deaktiviert Auto-Commit für Transaktionen
            // Setze den Parameter und führe die Query aus
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            // Commit der Transaktion
            connection.commit();
        } catch (SQLException e) {
            System.err.println("Fehler beim Löschen der E-Mail: " + e.getMessage());
            try (Connection connection = DatabaseConnector.getInstance().getConnection()) {
                if (connection != null && !connection.getAutoCommit()) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback-Fehler: " + rollbackEx.getMessage());
            }
        }
    }
}

