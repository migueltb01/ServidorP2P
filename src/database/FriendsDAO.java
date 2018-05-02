// Packages
package database;

// Imports

import java.sql.*;

// DAO for pairs of friends
public class FriendsDAO extends AbstractDAO {

    // Methods
    // Constructor
    public FriendsDAO(Connection connection) {
        this.connection = connection; // Database connection
    }

    // Add friend
    public void addFriend(String username1, String username2) throws SQLException {
        // Prepare statement
        PreparedStatement statement = connection.prepareStatement("insert into friends values (?, ?);");

        // Set parameters: username and password
        statement.setString(1, username1);
        statement.setString(2, username2);

        // Execute update
        statement.executeUpdate();
    }

    // Delete friend
    public void deleteFriend(String username1, String username2) throws SQLException {
        // Prepare statement
        PreparedStatement statement = connection.prepareStatement("delete from friends where (username1 = ? and username2 = ?) or (username2 = ? and username1 = ?);");

        // Set parameters: username
        statement.setString(1, username1);
        statement.setString(2, username2);

        statement.setString(3, username1);
        statement.setString(4, username2);

        // Execute update
        statement.executeUpdate();

        // Close statement
        try {
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
