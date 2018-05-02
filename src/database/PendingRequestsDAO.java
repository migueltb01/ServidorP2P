// Packages
package database;

// Imports
import java.sql.*;
import java.util.ArrayList;
import p2p.User;

// DAO for pending requests
public class PendingRequestsDAO extends AbstractDAO {

	// Methods
	// Constructor
	public PendingRequestsDAO(Connection connection) {
		this.connection = connection; // Database connection
	}

	// Get pending requests for the specified user and return a list of the requesters' usernames
	public ArrayList<String> getPendingRequestsTo(String user) throws SQLException {
		// Instantiate ArrayList
		ArrayList<String> requesters = new ArrayList<>();

		// Prepare query
		PreparedStatement statement = connection.prepareStatement("select * from pendingRequests where requestedUser = ?;");

		// Set parameters
		statement.setString(1, user);

		// Execute query
		ResultSet resultSet = statement.executeQuery();

		// While we don't reach the end of the results...
		while (resultSet.next()) {
			// Add user to user list
			requesters.add(resultSet.getString("requesterUser"));
		}

		// Return list
		return requesters;
	}

	public ArrayList<String> getPendingRequestsFrom(String user) throws SQLException {
		// Instantiate ArrayList
		ArrayList<String> requesters = new ArrayList<>();

		// Prepare query
		PreparedStatement statement = connection.prepareStatement("select * from pendingRequests where requesterUser = ?;");

		// Set parameters
		statement.setString(1, user);

		// Execute query
		ResultSet resultSet = statement.executeQuery();

		// While we don't reach the end of the results...
		while (resultSet.next()) {
			// Add user to user list
			requesters.add(resultSet.getString("requestedUser"));
		}

		// Return list
		return requesters;
	}

	// Add request
	public void addRequest(String username1, String username2) throws SQLException {
		// Prepare statement
		PreparedStatement statement = connection.prepareStatement("insert into pendingrequests values (?, ?);");

		// Set parameters: username and password
		statement.setString(1, username1);
		statement.setString(2, username2);

		// Execute update
		statement.executeUpdate();
	}

	// Delete request
	public void deleteRequest(String username1, String username2) throws SQLException {
		// Prepare statement
		PreparedStatement statement = connection.prepareStatement("delete from pendingrequests where requesterUser = ? and requestedUser = ?");

		// Set parameters: username and password
		statement.setString(1, username1);
		statement.setString(2, username2);

		// Execute update
		statement.executeUpdate();
	}
}
