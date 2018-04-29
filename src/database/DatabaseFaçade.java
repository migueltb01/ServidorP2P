// Packages
package database;

// Imports
import java.io.*;
import java.util.Properties;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import p2p.User;

// Database façade
public class DatabaseFaçade {

	// Atributes
	private Connection connection; // Connection to database
	private UsersDAO usersDAO; // Users DAO
	private FriendsDAO friendsDAO; // Friends DAO
	private PendingRequestsDAO requestsDAO; // Pending requests DAO

	// Methods
	// Constructor
	public DatabaseFaçade() throws SQLException {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println(e);
			System.exit(-1);
		}

		// Define properties
		Properties usuario = new Properties();
		usuario.setProperty("user", "postgres");
		usuario.setProperty("password", "postgres");

		// Establish connection with the database server
		connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/dingdingDB", usuario);

		// Instantiate DAOs
		usersDAO = new UsersDAO(connection);
		friendsDAO = new FriendsDAO(connection);
		requestsDAO = new PendingRequestsDAO(connection);
	}

	// Retrieves all users from the database
	public HashMap<String, User> getUsers() throws SQLException {
		return usersDAO.getUsers();
	}

	// Add new user
	public void addUser(String username, String password) throws SQLException {
		usersDAO.addUser(username, password);
	}

	// Delete user
	public void deleteUser(String username) throws SQLException {
		usersDAO.deleteUser(username);
	}

	// Change the specified user's password
	public void updatePassword(String username, String password) throws SQLException {
		usersDAO.updatePassword(username, password);
	}

	// Get pending requests for the specified user and return a list of the requesters' usernames
	public ArrayList<String> getPendingRequests(String user) throws SQLException {
		return requestsDAO.getPendingRequests(user);
	}

	// Add request
	public void addRequest(String username1, String username2) throws SQLException {
		requestsDAO.addRequest(username1, username2);
	}

	// Delete request
	public void deleteRequest(String username1, String username2) throws SQLException {
		requestsDAO.deleteRequest(username1, username2);
	}

	// Add friend
	public void addFriend(String username1, String username2) throws SQLException {
		friendsDAO.addFriend(username1, username2);
	}

	// Delete friend
	public void deleteFriend(String username1, String username2) throws SQLException {
		friendsDAO.deleteFriend(username1, username2);
	}
}
