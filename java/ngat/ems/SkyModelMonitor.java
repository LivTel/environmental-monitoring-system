/**
 * 
 */
package ngat.ems;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Provides a registry for classes which wish to recieve notification of sky model updates. These will typically be 
 * associated to an instance of MutableSkyModel.
 * @author snf
 *
 */
public interface SkyModelMonitor extends Remote {

	/** Register a listener for sky model update notification.
	 * @param l A listener to add to the list of registered sky model update listeners.
	 */
	public void addSkyModelUpdateListener(SkyModelUpdateListener l) throws RemoteException;
	
	/** Remove a listener from the list of listeners registered for sky model update notification.
	 * @param l A listener to remove from the list of registered sky model update listeners.
	 */
	public void removeSkyModelUpdateListener(SkyModelUpdateListener l) throws RemoteException;
	
}
