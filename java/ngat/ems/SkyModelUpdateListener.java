/**
 * 
 */
package ngat.ems;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author snf
 *
 */
public interface SkyModelUpdateListener extends Remote {

	/** Notification that a seeing update was made.
	 * 
	 * @param time When the update was made.
	 * @param rawSeeing The raw seeing value (asec).
	 * @param correctedSeeing The corrected (for elevation and wavelength) seeing (asec).
	 * @throws RemoteException If anything goes awry.
	 */
	//public void seeingUpdated(long time, double rawSeeing, double correctedSeeing) throws RemoteException;
	public void seeingUpdated(long time, double raw, double corrected, double prediction, double altitude, double azimuth, double wavelength, boolean std, String source, String targetName) throws RemoteException;
	
	/** Notification that an extinction update was made.
	 * @param time When the update was made.
	 * @param extinction The extinction value.
	 * @throws RemoteException If anything goes awry.
	 */
	public void extinctionUpdated(long time, double extinction) throws RemoteException;
}
