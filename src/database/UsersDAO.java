// Packages
package database;

// Imports
import java.sql.*;
import java.util.HashMap;
import p2p.User;

// DAO for the users' data
public class UsersDAO extends AbstractDAO {

	// Methods
	// Constructor
	public UsersDAO(Connection connection) {
		this.connection = connection; // Database connection
	}

	// Retrieves all users from the database
	public HashMap<String, User> getUsers() throws SQLException {
		// Instantiate ArrayList
		HashMap<String, User> users = new HashMap();

		// Prepare and execute query
		PreparedStatement statement = connection.prepareStatement("select * from users;");
		ResultSet resultSet = statement.executeQuery();

		// While we don't reach the end of the results...
		while (resultSet.next()) {
			// Add user to user list
			users.put(resultSet.getString("username"), new User(resultSet.getString("username"), resultSet.getString("pass")));
		}

		// Prepare and execute query
		statement = connection.prepareStatement("select * from friends;");
		resultSet = statement.executeQuery();

		// While we don't reach the end of the results...
		while (resultSet.next()) {
			// Retrieve usernames
			String user1 = resultSet.getString("username1");
			String user2 = resultSet.getString("username2");

			// Add each user to the other's friends list
			users.get(user1).addFriend(users.get(user2));
		}

		// Return list of users found
		return users;
	}

	// Add new user
	public void addUser(String username, String password) throws SQLException {
		// Prepare statement
		PreparedStatement statement = connection.prepareStatement("insert into users values (?, ?);");

		// Set parameters: username and password
		statement.setString(1, username);
		statement.setString(2, password);

		// Execute update
		statement.executeUpdate();
	}

	// Delete user
	public void deleteUser(String username) throws SQLException {
		// Prepare statement
		PreparedStatement statement = connection.prepareStatement("delete from users where username = ?;");

		// Set parameters: username and password
		statement.setString(1, username);

		// Execute update
		statement.executeUpdate();

		// It is not necessary to delete any friendships or requests involving
		// the deleted user as the on delete/on update policy is set to cascade
	}

	// Change the specified user's password
	public void updatePassword(String username, String password) throws SQLException {
		// Prepare statement
		PreparedStatement statement = connection.prepareStatement("update users set pass = ? where username = ?;");

		// Set parameters: username and password
		statement.setString(1, password);
		statement.setString(2, username);

		// Execute update
		statement.executeUpdate();
	}
}
