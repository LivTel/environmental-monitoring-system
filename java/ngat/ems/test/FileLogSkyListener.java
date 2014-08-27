package ngat.ems.test;

import java.io.*;
import java.text.*;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;

import ngat.ems.*;

public class FileLogSkyListener implements SkyModelUpdateListener {

    public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private String fileName;

    public FileLogSkyListener(String fileName) {
	this.fileName = fileName;
	sdf.setTimeZone(UTC);
    }

    /** Notification that a seeing update was made.
     * 
     * @param time When the update was made.
     * @param rawSeeing The raw seeing value (asec).
     * @param correctedSeeing The corrected (for elevation and wavelength) seeing (asec).
     * @throws RemoteException If anything goes awry.
     */
    public void seeingUpdated(long time, double raw, double corrected, double prediction, double alt, double azm, double wavelength, boolean std, String source, String targetName) throws RemoteException {}

    /** Notification that an extinction update was made.
     * @param time When the update was made.
     * @param extinction The extinction value.
     * @throws RemoteException If anything goes awry.
     */
    public void extinctionUpdated(long time, double extinction) throws RemoteException {

	// 0.0 = Photom
	// 1.0 = Spectro

	try {
	PrintWriter pout = new PrintWriter(new FileWriter(new File(fileName)));

	String extStr = "UNKNOWN";
	if (extinction < 0.5)
	    extStr = "PHOTOM";
	else
	    extStr = "SPECTRO";
	
	// e.g. 2014-01-28T16:24:00 PHOTOM 0.0
   
	pout.println(sdf.format(new Date())+" "+extStr+" "+extinction);
	pout.close();
	pout = null;
	} catch (Exception e) {
	    throw new RemoteException("Error writing photom data to file: "+e);
	}
    }
}