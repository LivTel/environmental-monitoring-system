package ngat.ems;

import java.rmi.Remote;
import java.rmi.RemoteException;



/**  Classes which provide access to the telescope, site and atmospheric variables
 * should implement this interface. It provides methods to interrogate these variables
 * for current, past and predicted future values (NOT) including statistical products.
 * 
 * 
*/
public interface SkyModel extends Remote {

	/**
	 *  Obtains the seeing at the specified sky coordinates for given wavelength at the specified time.
	 * @param wavelength The wavelength (nm) for which we want the seeing.
	 * @param altitude The altitude for which we want the seeing (rads).
	 * @param azimuth The azimuth for which we want the seeing (rads).
	 * @param time The time when we want the seeing.
	 * @return The seeing (arcsec) at wavelength for (alt,az) ate time.
	 */
	public double getSeeing(double wavelength, double altitude, double azimuth, long time) throws RemoteException;
	/**
	 *  Obtains the extinction at the specified sky coordinates for given wavelength at the specified time.
	 * @param wavelength The wavelength (nm) for which we want the extinction.
	 * @param altitude The altitude for which we want the extinction (rads).
	 * @param azimuth The azimuth for which we want the extinction (rads).
	 * @param time The time when we want the extinction.
	 * @return The extinction (arcsec) at wavelength for (alt,az) ate time.
	 */
	public double getExtinction(double wavelength, double altitude, double azimuth, long time) throws RemoteException;
	
	/**
	 *  Obtains the sky-brightness at the specified sky coordinates for given wavelength at the specified time.
	 * @param wavelength The wavelength (nm) for which we want the sky-brightness.
	 * @param altitude The altitude for which we want the sky-brightness (rads).
	 * @param azimuth The azimuth for which we want the sky-brightness (rads).
	 * @param time The time when we want the sky-brightness.
	 * @return The sky-brightness (arcsec) at wavelength for (alt,az) ate time.
	 */
	public double getSkyBrightness(double wavelength, double altitude, double azimuth, long time) throws RemoteException;
	
	
}


