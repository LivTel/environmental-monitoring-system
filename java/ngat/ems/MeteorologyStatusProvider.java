/**
 * 
 */
package ngat.ems;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author eng
 *
 */
public interface MeteorologyStatusProvider extends Remote {
	/**
	 * Add a MeteorologyStatusUpdateListener to the list of registered listeners.
	 * If the listener is already registered, return silently,
	 * 
	 * @param l The listener to add.
	 * @throws RemoteException
	 */
	public void addMeteorologyStatusUpdateListener(MeteorologyStatusUpdateListener msl) throws RemoteException;
	
	/**
	 * Remove a MeteorologyStatusUpdateListener from the list of registered
	 * listeners. If the listener is not registered, return silently.
	 * 
	 * @param l The listener to remove.
	 * @throws RemoteException
	 */
	public void removeMeteorologyStatusUpdateListener(MeteorologyStatusUpdateListener msl) throws RemoteException;
	
}
