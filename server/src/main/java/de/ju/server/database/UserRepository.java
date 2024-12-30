package de.ju.server.database;

import de.ju.server.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    public User getUser(String username) {
        String query = "SELECT * FROM users WHERE email = (?)";
        try (Connection connection = DatabaseConnector.getInstance().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    return new User(id, email, password);
                }
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }
}
