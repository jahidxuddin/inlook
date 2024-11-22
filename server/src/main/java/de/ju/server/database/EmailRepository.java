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

    public void storeEmail(Email email) {
        String query = "INSERT INTO Emails (sender, recipient, subject, body, size) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnector.getInstance().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email.getSender());
            preparedStatement.setString(2, email.getRecipient());
            preparedStatement.setString(3, email.getSubject());
            preparedStatement.setString(4, email.getBody());
            preparedStatement.setByte(5, email.calcSize());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        query = "INSERT INTO UserEmails (user_id, email_id) VALUES (?, ?)";
        try (Connection connection = DatabaseConnector.getInstance().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email.getSender());
            preparedStatement.setString(2, email.getRecipient());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
