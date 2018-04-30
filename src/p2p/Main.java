// Packages
package p2p;

// Imports
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.*;
import java.sql.SQLException;

// Main class
public class Main {

	// Attributes
	private static int portNumber; // Port number
	private static String host; // Hostname

	// Methods
	// Starts RMI registry in port portNumber
	private static void startRegistry() throws RemoteException {
		try {
			// Retrieve registry from port portNumber and try method list().
			// If registry doesn't exist, an exception will be thrown.
			Registry registry = LocateRegistry.getRegistry(portNumber);
			registry.list();

			// Si an exception occurs, there is no registry
		} catch (RemoteException ex) {
			System.err.println("Cannot locate RMI registry in port " + portNumber);
			Registry registry = LocateRegistry.createRegistry(portNumber); // Creamos el registro

			// Print message
			System.out.println("RMI registry created in port " + portNumber);
		}
	}

	// Main
	public static void main(String[] args) {
		// for testing purposes only
		portNumber = 1099;
		host = "localhost";

		try {

			// Instantiate remote server and start registry
			RemoteServerImplementation remoteServer = new RemoteServerImplementation();
			startRegistry();

			// Register remote server in registry
			Naming.rebind("rmi://" + host + ":" + portNumber + "/P2PServer", remoteServer);
			System.out.println("Started remote server in URL rmi://" + host + ":" + portNumber + "/remoteServer");
			
		// If an exception occurs, print message
		} catch (RemoteException ex) {
			System.err.println("Error: " + ex.getMessage());
			System.err.println("Cannot start remote server.");

		} catch (MalformedURLException | SQLException ex) {
			System.err.println("Error: " + ex.getMessage());
			System.err.println("Cannot start remote server.");
		}

	}
}
