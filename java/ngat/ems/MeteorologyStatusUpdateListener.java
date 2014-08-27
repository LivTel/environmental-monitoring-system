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
public interface MeteorologyStatusUpdateListener extends Remote {
	
	/** Handle an update of meteorology status information.
	 * @param status The status to handle.
	 * @throws RemoteException
	 */
	public void meteorologyStatusUpdate(MeteorologyStatus status) throws RemoteException;
	
}
