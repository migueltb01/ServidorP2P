// Packages
package p2p;

// Imports

import database.DatabaseFaçade;
import exceptions.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

// P2P server implementation
public class RemoteServerImplementation extends UnicastRemoteObject implements RemoteServerInterface {

    // Atributes
    private HashMap<String, User> users; // User list
    private DatabaseFaçade database; // Database façade

    // Métodos
    // Constructor
    public RemoteServerImplementation() throws RemoteException, SQLException {
        // Instantiate database façade
        database = new DatabaseFaçade();

        // Get users from database
        this.users = database.getUsers();
    }

    // Register a new user
    @Override
    public synchronized void registerUser(String username, String password) throws RepeatedUsernameException,
            RemoteException,
            SQLException {
        // If the user does not exist...
        if (!users.containsKey(username)) {

            // Insert user into database
            database.addUser(username, password);

            // Insert user into list
            users.put(username, new User(username, password));

            // Print message
            System.out.println("\nAdded user " + username + ".");

        } else { // Else, throw exception
            throw new RepeatedUsernameException();
        }
    }

    // Delete registered user
    @Override
    public synchronized void deleteUser(RemoteClientInterface client, String username, String password) throws UserNotFoundException,
            IncorrectPasswordException,
            IncorrectSessionException,
            RemoteException,
            SQLException {

        // If the user exists...
        if (users.containsKey(username)) {

            // If the specified password is correct...
            if (users.get(username).getPassword().equals(password)) {

                // If the operation has been invoked from the correct client...
                if (users.get(username).getClient().equals(client)) {
                    // Log user out
                    this.logOut(client, username, password);

                    // Delete user from database
                    database.deleteUser(username);

                    // Retrieve user to delete
                    User deletedUser = users.get(username);

                    // Unfriend all of their friends
                    //for (User friend : deletedUser.getFriends()) {
                    //   friend.deleteFriend(deletedUser);
                    //}

                    users.remove(username); // Delete user from register

                    System.out.println("\nDeleted user " + username + "."); // Print message

                } else { // If the client does not match the user's, throw exception
                    throw new IncorrectSessionException();
                }

            } else { // If password is incorrect, throw exception
                throw new IncorrectPasswordException();
            }

        } else { // If user does not exist, throw exception
            throw new UserNotFoundException();
        }
    }

    // Log in as specified user through specified client
    @Override
    public synchronized ArrayList<String> logIn(String username, String password, RemoteClientInterface client) throws UserNotFoundException,
            IncorrectPasswordException,
            RemoteException,
            SQLException {

        // If user exists...
        if (users.containsKey(username)) {

            // If password is correct...
            if (users.get(username).getPassword().equals(password)) {

                // If user is logged in...
                if (users.get(username).getClient() != null) {
                    // @TODO: notify old client that its session will be closed
                }

                // Connect user with client
                users.get(username).connect(client);

                // List of online friends
                ArrayList<String> onlineFriends = new ArrayList();

                // Iterate over friends list
                for (User friend : users.get(username).getFriends()) {
                    // If friend is online, add them to the online friends list
                    if (friend.isOnline()) {
                        onlineFriends.add(friend.getUsername());
                        friend.getClient().notifyOnline(username);
                    }
                }

                // Retrieve list of users who have sent a friend request to this user
                ArrayList<String> requesters = database.getPendingRequestsTo(username);

                // Send requests to the client
                for (String requester : requesters) {
                    users.get(username).getClient().requestFriendship(requester);
                }

                // Print message
                System.out.println(username + " has logged in.");

                return onlineFriends;

            } else { // If password is incorrect, throw exception
                throw new IncorrectPasswordException();
            }

        } else { // If user does not exist, throw exception
            throw new UserNotFoundException();
        }
    }

    // Logs specified user out
    @Override
    public synchronized void logOut(RemoteClientInterface client, String username, String password) throws UserNotFoundException,
            IncorrectPasswordException,
            IncorrectSessionException,
            RemoteException {

        // If user exists...
        if (users.containsKey(username)) {

            // If password is correct...
            if (users.get(username).getPassword().equals(password)) {

                // If the operation has been invoked from the correct client...
                if (users.get(username).getClient().equals(client)) {
                    // Disconnect user from client
                    users.get(username).desconectar();
                    System.out.println(username + " has logged out.");

                    for (User friend : users.get(username).getFriends()) {
                        if (friend.isOnline()) {
                            friend.getClient().notifyOffline(username);
                        }
                    }

                } else { // If the operation has been invoked from a different client or the user's not online,
                    // throw exception
                    throw new IncorrectSessionException();
                }

            } else { // If password is incorrect, throw exception
                throw new IncorrectPasswordException();
            }

        } else { // If user does not exist, throw exception
            throw new UserNotFoundException();
        }
    }

    // Search usernames containing a specific substring
    @Override
    public synchronized ArrayList<String> searchUsers(String substring, String username) throws RemoteException, SQLException {
        // Instantiate ArrayList that will save search results
        ArrayList<String> resultados = new ArrayList<>();

        // Iterate over user list
        for (String nombreUsuario : users.keySet()) {

            // If the current user's username contains the substring,
            // add it to search results
            if (nombreUsuario.contains(substring) && !nombreUsuario.equals(username) && !users.get(username).getFriends().contains(users.get(nombreUsuario))) {

                ArrayList<String> requesters = database.getPendingRequestsFrom(username);
                ArrayList<String> requested = database.getPendingRequestsFrom(nombreUsuario);

                if (!requesters.contains(nombreUsuario) && !requested.contains(username)) {
                    resultados.add(nombreUsuario);
                }
            }
        }

        // Return search results
        return resultados;
    }

    // Invoked by online user to add another user as a friend
    @Override
    public synchronized void addFriend(RemoteClientInterface client, String sourceUser, String password, String destinationUser)
            throws UserNotFoundException,
            IncorrectPasswordException,
            IncorrectSessionException,
            RepeatedFriendshipException,
            RemoteException,
            SQLException {

        // If both users exist...
        if (users.containsKey(sourceUser) && users.containsKey(destinationUser)) {

            // If password is correct
            if (users.get(sourceUser).getPassword().equals(password)) {

                // If the operation has been invoked from the correct client...
                if (users.get(sourceUser).getClient().equals(client)) {

                    // If users are not already friends...
                    if (!users.get(sourceUser).getFriends().contains(users.get(destinationUser))) {

                        ArrayList<String> requesters = database.getPendingRequestsFrom(sourceUser);
                        ArrayList<String> requested = database.getPendingRequestsFrom(destinationUser);

                        if (!requesters.contains(destinationUser) && !requested.contains(sourceUser)) {

                            // If destination user is online...
                            if (users.get(destinationUser).isOnline()) {
                                database.addRequest(sourceUser, destinationUser);
                                users.get(destinationUser).getClient().requestFriendship(sourceUser);

                            } else { // If destination user is offline...
                                database.addRequest(sourceUser, destinationUser);
                            }
                        }

                    } else { // If users are already friends, throw exception
                        throw new RepeatedFriendshipException();
                    }

                } else { // Si client is incorrect, throw exception
                    throw new IncorrectSessionException();
                }

            } else { // If password is incorrect, throw exception
                throw new IncorrectPasswordException();
            }

        } else { // If one or both of the users do not exist, throw exception
            throw new UserNotFoundException();
        }
    }

    // Invoked by online user to unfriend another user
    @Override
    public synchronized void deleteFriend(RemoteClientInterface client, String sourceUser, String password, String destinationUser)
            throws UserNotFoundException,
            IncorrectPasswordException,
            IncorrectSessionException,
            FriendNotFoundException,
            RemoteException,
            SQLException {

        // If user exists...
        if (users.containsKey(sourceUser) && users.containsKey(destinationUser)) {

            // If password is correct...
            if (users.get(sourceUser).getPassword().equals(password)) {

                // If the operation has been invoked from the correct client...
                if (users.get(sourceUser).getClient().equals(client)) {

                    // If users are friends...
                    if (users.get(sourceUser).getFriends().contains(users.get(destinationUser))) {

                        // Delete each user from each other's list
                        users.get(sourceUser).deleteFriend(users.get(destinationUser));

                        // Save changes to database
                        database.deleteFriend(sourceUser, destinationUser);

                    } else { // If users are not friends, throw exception
                        throw new FriendNotFoundException();
                    }

                } else { // If client is incorrect, throw exception
                    throw new IncorrectSessionException();
                }

            } else { // If password is incorrect, throw exception
                throw new IncorrectPasswordException();
            }

        } else { // If user does not exist, throw exception
            throw new UserNotFoundException();
        }
    }

    // Invoked by client to confirm or deny friendship to another user
    @Override
    public synchronized void resolveRequest(RemoteClientInterface client, String password, String sourceUser, String destinationUser, boolean decision)
            throws UserNotFoundException,
            IncorrectPasswordException,
            IncorrectSessionException,
            RemoteException,
            SQLException {

        // If user exists...
        if (users.containsKey(sourceUser) && users.containsKey(destinationUser)) {

            // If password is correct...
            if (users.get(destinationUser).getPassword().equals(password)) {

                // If the operation has been invoked from the correct client...
                if (users.get(destinationUser).getClient().equals(client)) {

                    // True means that the requested user has accepted the request
                    if (decision) {
                        // Add user as friend
                        users.get(destinationUser).addFriend(users.get(sourceUser));

                        // Save changes to database
                        database.addFriend(sourceUser, destinationUser);

                        User friend = users.get(destinationUser);
                        User user = users.get(sourceUser);
                        if (friend.isOnline()) {
                            user.getClient().notifyOnline(destinationUser);
                            friend.getClient().notifyOnline(sourceUser);
                        }
                    }

                    // False means that the requested user has denied the request;
                    // no actions are necessary
                    // Delete request from database
                    database.deleteRequest(sourceUser, destinationUser);

                } else { // If client is incorrect, throw exception
                    throw new IncorrectSessionException();
                }

            } else { // If password is incorrect, throw exception
                throw new IncorrectPasswordException();
            }

        } else { // If user does not exist, throw exception
            throw new UserNotFoundException();
        }
    }

    // Invoked by an online user to change their password
    @Override
    public synchronized void changePassword(RemoteClientInterface client, String username, String oldPassword, String newPassword)
            throws UserNotFoundException,
            IncorrectPasswordException,
            IncorrectSessionException,
            RemoteException,
            SQLException {

        // If user exists...
        if (users.containsKey(username)) {

            // If password is correct...
            if (users.get(username).getPassword().equals(oldPassword)) {

                // If the operation has been invoked from the correct client...
                if (users.get(username).getClient().equals(client)) {

                    // Change password to new password
                    users.get(username).setPassword(newPassword);

                    // Save changes to database
                    database.updatePassword(username, newPassword);

                } else { // If the client is incorrect, throw exception
                    throw new IncorrectSessionException();
                }

            } else { // If the password is incorrect, throw exception
                throw new IncorrectPasswordException();
            }

        } else { // If the user does not exist, throw exception
            throw new UserNotFoundException();
        }
    }

    // Invoked by an online user to start a chat with another user
    @Override
    public synchronized RemoteClientInterface startChat(RemoteClientInterface client, String sourceUser, String destinationUser, String password)
            throws UserNotFoundException,
            IncorrectPasswordException,
            IncorrectSessionException,
            FriendNotFoundException,
            OfflineFriendException,
            RemoteException {

        // If user exists...
        if (users.containsKey(sourceUser) && users.containsKey(destinationUser)) {

            // If password is correct...
            if (users.get(sourceUser).getPassword().equals(password)) {

                // If the operation has been invoked from the correct client...
                if (users.get(sourceUser).getClient().equals(client)) {

                    // If users are friends...
                    if (users.get(sourceUser).getFriends().contains(users.get(destinationUser))) {

                        // If the destination user is online...
                        if (users.get(destinationUser).isOnline()) {

                            // Return reference to source user
                            return users.get(destinationUser).getClient();

                        } else { // If destination user is not online, throw exception
                            throw new OfflineFriendException();
                        }

                    } else { // If users are not friends, throw exception
                        throw new FriendNotFoundException();
                    }

                } else { // If the client is incorrect, throw exception
                    throw new IncorrectSessionException();
                }

            } else { // If password is incorrect, throw exception
                throw new IncorrectPasswordException();
            }

        } else { // If user does not exist, throw exception
            throw new UserNotFoundException();
        }
    }
}
