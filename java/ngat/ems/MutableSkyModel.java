/**
 * 
 */
package ngat.ems;

import java.rmi.RemoteException;


/** Mutable version of skymodel.
 * @author snf
 *
 */
public interface MutableSkyModel extends SkyModel {

	/** Update the value of the Seeing.
	 * @param seeing The new seeing value.
	 * @param wavelength Wavelength at which the new value was measured (nm).
	 * @param alt Altitude at which the new value was measured (rads).
	 * @param az Azimuth at which the new value was measured (rads).
	 * @param time Time at which the new value was measured.
	 * @param standard True if the new value refers to a standard (otherwise a normal exposure).
	 * @throws RemoteException
	 */
	public void updateSeeing(double seeing, double wavelength, double alt, double az, long time, boolean standard, String source, String targetName) throws RemoteException;
	
	/** Update the value of extinction.
	 * @param extinction The new value.
	 * @param wavelength Wavelength at which the new value was measured (nm).
	 * @param altitude Altitude at which the new value was measured (rads).
	 * @param azimuth Azimuth at which the new value was measured (rads).
	 * @param time Time at which the new value was measured.
	 * @param standard True if the new value refers to a standard (otherwise a normal exposure).
	 * @throws RemoteException
	 */
	public void updateExtinction(double extinction, double wavelength, double altitude, double azimuth, long time, boolean standard) throws RemoteException;
	
	/** Update the value of sky brightness.
	 * @param skyBrightnessThe new value.
	 * @param wavelength Wavelength at which the new value was measured (nm).
	 * @param altitude Altitude at which the new value was measured (rads).
	 * @param azimuth Azimuth at which the new value was measured (rads).
	 * @param time Time at which the new value was measured.
	 * @throws RemoteException
	 */
	public void updateSkyBrightness(double skyBrightness, double wavelength, double altitude, double azimuth, long time) throws RemoteException;
	
}
