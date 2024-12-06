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

    public boolean deleteEmail(int id) {
        String query1 = "DELETE FROM Emails WHERE id = (?)";
        String query2 = "DELETE FROM UserEmails WHERE email_id = (?)";

        Connection connection = null;
        try {
            connection = DatabaseConnector.getInstance().getConnection();
            connection.setAutoCommit(false);  // Deaktiviert Auto-Commit für Transaktionen

            // Erstelle PreparedStatements
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(query1);
                 PreparedStatement preparedStatement2 = connection.prepareStatement(query2)) {

                // Setze Parameter für die erste Query
                preparedStatement1.setInt(1, id);

                // Setze Parameter für die zweite Query
                preparedStatement2.setInt(1, id);

                // Führe beide Queries aus
                preparedStatement1.executeUpdate();
                preparedStatement2.executeUpdate();

                // Commit der Transaktion
                connection.commit();
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();  // Rollback der Transaktion bei einem Fehler
                } catch (SQLException rollbackEx) {
                     System.out.println(rollbackEx.getMessage());
                }
            }
             System.out.println(e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);  // Stelle den Auto-Commit wieder her
                    connection.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return true;  // Alle Queries wurden erfolgreich ausgeführt
    }
}

