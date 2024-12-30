package de.ju.server.database;

import de.ju.server.entity.Email;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmailRepository {
    public List<Email> getAllEmailsFromUser(String username) {
        List<Email> emails = new ArrayList<>();
        String query = "SELECT * FROM Emails WHERE recipient = ?";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Email email = new Email(
                            resultSet.getInt("id"),
                            resultSet.getString("sender"),
                            resultSet.getString("recipient"),
                            resultSet.getString("subject"),
                            resultSet.getString("body"),
                            resultSet.getString("sentAt")
                    );
                    emails.add(email);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving emails: " + e.getMessage());
        }

        return emails;
    }

    public Email getEmailById(int id) {
        String query = "SELECT * FROM Emails WHERE id = ?";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Construct and return the Email object
                    return new Email(
                            resultSet.getInt("id"),
                            resultSet.getString("sender"),
                            resultSet.getString("recipient"),
                            resultSet.getString("subject"),
                            resultSet.getString("body"),
                            resultSet.getString("sentAt")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving email by ID: " + e.getMessage());
        }
        return null;
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

    public void transferEmails(String oldEmail, String newEmail) {
        String updateRecipientQuery = "UPDATE Emails SET recipient = ? WHERE recipient = ?";
        String updateSenderQuery = "UPDATE Emails SET sender = ? WHERE sender = ?";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement updateRecipientStatement = connection.prepareStatement(updateRecipientQuery);
             PreparedStatement updateSenderStatement = connection.prepareStatement(updateSenderQuery)) {

            connection.setAutoCommit(false);

            updateRecipientStatement.setString(1, newEmail);
            updateRecipientStatement.setString(2, oldEmail);
            updateRecipientStatement.executeUpdate();

            updateSenderStatement.setString(1, newEmail);
            updateSenderStatement.setString(2, oldEmail);
            updateSenderStatement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            System.err.println("Error while transferring emails: " + e.getMessage());
            try (Connection connection = DatabaseConnector.getInstance().getConnection()) {
                if (connection != null && !connection.getAutoCommit()) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback error: " + rollbackEx.getMessage());
            }
            throw new RuntimeException(e);
        }
    }

    public void deleteEmail(int id) {
        String query = "DELETE FROM Emails WHERE id = ?";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

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

