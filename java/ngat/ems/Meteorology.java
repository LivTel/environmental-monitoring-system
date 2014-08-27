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
public interface Meteorology extends Remote {
	
	
	public MeteorologyStatusProvider getMeteorologyProvider() throws RemoteException;

}
