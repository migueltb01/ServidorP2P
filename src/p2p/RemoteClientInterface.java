package p2p;// Packages


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

// Remote P2P client interface
public interface RemoteClientInterface extends Remote {

    void notifyOnline(String username);
    void requestFriendship(String requester);

}
