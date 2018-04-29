// Packages
package p2p;

// Imports
import java.util.ArrayList;

// DingDing user.
public class User {

	// Atributes
	private String username; // Username
	private String password; // Password
	private ArrayList<User> friends; // Friends list
	private boolean online; // Indicates user's status (true for online, false for offline)
	private RemoteClientInterface client = null; // Client from which this user is logged in

	// Méthods
	// Constructor
	public User(String username, String password) {
		this.username = username;
		this.password = password;

		friends = new ArrayList<>();
		online = false;
	}

	// Username getter
	public String getUsername() {
		return username;
	}

	// Password getter
	public String getPassword() {
		return password;
	}

	// Friends list getter
	public ArrayList<User> getFriends() {
		return friends;
	}

	// Client getter
	public RemoteClientInterface getClient() {
		return client;
	}

	// Online flag getter
	public boolean isOnline() {
		return online;
	}

	// Password setter
	public void setPassword(String password) {
		this.password = password;
	}

	// Online flag setter
	public void setOnline(boolean status) {
		this.online = status;
	}

	// Connect user to client
	public void connect(RemoteClientInterface client) {
		this.client = client;
		online = true;
	}

	// Disconnect user
	public void desconectar() {
		this.client = null;
		online = false;
	}

	// Add user to this user's friends list
	public void addFriend(User user) {

		// If they are different users...
		if (!user.username.equals(username)) {
			// Add each user to the other's friends list
			this.friends.add(user);
			user.friends.add(this);
		}
	}

	// Delete user from this user's friends
	public void deleteFriend(User usuario) {
		// Deletes each user from the other's friends list
		this.friends.remove(usuario);
		usuario.friends.remove(this);
	}
}
