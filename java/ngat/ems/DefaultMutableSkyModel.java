/**
 * 
 */
package ngat.ems;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.io.*;

import ngat.astrometry.AstroFormatter;
import ngat.astrometry.Position;
import ngat.util.logging.*;

/**
 * @author snf
 * 
 */
public class DefaultMutableSkyModel extends UnicastRemoteObject implements MutableSkyModel, SkyModelMonitor,
		Serializable {

	public static final double WAVELENGTH_RED = 700.0; // red wavelength (nm).

	public static final double ZENITH = 0.5 * Math.PI; // Zenith elevation
														// (rads).

	// level above which we wont allow.
	private static final double CLIP_LEVEL = 60.0;

	// level below which we wont allow non-standards to improve the seeing.
	private static final double IMPROVE_LEVEL = 0.25;

	private List seeingBuffer;
	private List predictSeeingBuffer;
	private long seeingUpdateTime;
	private double tau;
	private int bufferSize;

	private double extinction = 1.0; // assume bad

	private double skybright;

	private List listeners;

	private LogGenerator logger;

	public DefaultMutableSkyModel(int bufferSize, double tau) throws RemoteException {
		super();

		this.bufferSize = bufferSize;
		this.tau = tau;

		seeingBuffer = new Vector(bufferSize);
		predictSeeingBuffer = new Vector(bufferSize);

		listeners = new Vector();

		Logger alogger = LogManager.getLogger("EMS");
		logger = alogger.generate().system("EMS").subSystem("SkyModel").srcCompClass(this.getClass().getName())
				.srcCompId("skymodel");

		logger.create().extractCallInfo().info().level(3)
				.msg("New SkyModel using upto " + bufferSize + " samples, Tau: " + tau + "ms").send();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.sms.MutableSkyModel#updateSeeing(double, double, double,
	 * double, long, boolean)
	 */
	public void updateSeeing(double seeing, double wavelength, double alt, double az, long time, boolean standard,
			String source, String targetName) throws RemoteException {
	
		// throw away any above clipping level
		if (seeing > CLIP_LEVEL) {
			logger.create()
					.extractCallInfo()
					.info()
					.level(3)
					.msg("SkyModel:: Update exceeds clip level: T=" + (new Date(time)).toGMTString() + ", S=" + seeing
							+ ", W=" + wavelength + ", Alt=" + Position.toDegrees(alt, 3) + ", STD=" + standard + ")")
					.send();
			return;
		}

		// correct for wav and alt to w=550nm and z=0
		double cwav = (wavelength < 750.0 ? wavelength : 750.0);
		double corrsee = seeing * Math.pow(cwav / 700.0, 0.45) * Math.pow(Math.cos(0.5 * Math.PI - alt), 0.5);

		// remove when we hit the stop
		if (seeingBuffer.size() >= bufferSize)
			seeingBuffer.remove(0);

		// always add to general buffer
		seeingBuffer.add(new Entry(time, corrsee));

		// add to std buffer if its a STD
		if (standard) {
			if (predictSeeingBuffer.size() >= bufferSize)
				predictSeeingBuffer.remove(0);
			predictSeeingBuffer.add(new Entry(time, corrsee));
			logger.create().extractCallInfo().info().level(3).msg("SkyModel: Adding valid standard seeing sample").send();
		}

		// TODO awaiting confirmation to deploy...

		// Add to predict buffer if its non-std and improves but not below lower
		// limit.
		// ie we let n-s improve on current, but not if they suggest better than
		// 0.25
		// and we never allow them to worsen the prediction.
		double prediction = getSeeing(750.0, 0.5 * Math.PI, 0.0, time);
		if ((!standard) && (corrsee < prediction) && (corrsee >= IMPROVE_LEVEL)) {
			if (predictSeeingBuffer.size() >= bufferSize)
				predictSeeingBuffer.remove(0);
			predictSeeingBuffer.add(new Entry(time, corrsee));
			logger.create().extractCallInfo().info().level(3)
					.msg("SkyModel: Adding valid non-standard improving seeing sample")
					.send();
		}

		logger.create()
				.extractCallInfo()
				.info()
				.level(2)
				.msg("SkyModel:: SeeingUpdated: T=" + (new Date(time)) + ", RS=" + seeing + ", W=" + wavelength
						+ ", Alt=" + Position.toDegrees(alt, 2) + ", Azm: "+Position.toDegrees(az, 2)+
						", CS=" + corrsee + ", STD=" + standard + ", SRC+"+source+":"+targetName+")")
				.send();

		notifyListenersSeeingUpdated(seeing, wavelength, alt, az, time, standard, source, targetName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.sms.MutableSkyModel#updatePhotometricity(double, double,
	 * double, double, long, boolean)
	 */
	public void updateExtinction(double extinction, double wavelength, double altitude, double azimuth, long time,
			boolean standard) throws RemoteException {

		this.extinction = extinction;

		notifyListenersExtinctionUpdated(extinction, wavelength, altitude, azimuth, time, standard);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.sms.MutableSkyModel#updateSkyBrightness(double, double, double,
	 * double, long)
	 */
	public void updateSkyBrightness(double skyBrightness, double wavelength, double altitude, double azimuth, long time)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	/**
	 * Genenrates a prediction based on exponential decay from last reading
	 * 
	 * @see ngat.sms.SkyModel#getSeeing(double, double, double, long)
	 */
	public double getSeeing(double wavelength, double altitude, double azimuth, long time) throws RemoteException {

		logger.create().extractCallInfo().info().level(3)
				.msg("Exec getSeeing() using: w=" + wavelength + ", t=" + (new Date(time))).send();
		double mseeing = 0.0;
		double norm = 0.0;
		for (int i = 0; i < predictSeeingBuffer.size(); i++) {
			Entry e = (Entry) predictSeeingBuffer.get(i);
			double cc = Math.exp((double) (e.time - time) / tau);
			mseeing += e.value * cc;
			norm += cc;
		}
		logger.create()
				.extractCallInfo()
				.info()
				.level(3)
				.msg("Exec getSeeing() returning using STD: mseeing = " + mseeing + ", norm=" + norm + " cs="
						+ (mseeing / norm)).send();

		double corrsee = mseeing / norm;

		double lambda = (wavelength < 750.0 ? wavelength : 750.0);
		double cosz = Math.cos(0.5 * Math.PI - altitude);
		double correction = Math.pow(lambda / 700.0, 0.45) * Math.pow(cosz, 0.5);

		double uncorsee = corrsee / correction;

		return uncorsee;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.sms.SkyModel#getPhotometricity(double, double, double, long)
	 */
	public double getExtinction(double wavelength, double altitude, double azimuth, long time) throws RemoteException {
		// basically just echo back the one that was set.
		logger.create().extractCallInfo().info().level(3).msg("Exec getExtinction(): " + extinction).send();

		return extinction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.sms.SkyModel#getSkyBrightness(double, double, double, long)
	 */
	public double getSkyBrightness(double wavelength, double altitude, double azimuth, long time)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Register a listener for sky model update notification.
	 * 
	 * @param l
	 *            A listener to add to the list of registered sky model update
	 *            listeners.
	 */
	public void addSkyModelUpdateListener(SkyModelUpdateListener l) throws RemoteException {
		// TODO Auto-generated method stub
		if (listeners.contains(l))
			return;
		listeners.add(l);
			
	}

	/**
	 * Remove a listener from the list of listeners registered for sky model
	 * update notification.
	 * 
	 * @param l
	 *            A listener to remove from the list of registered sky model
	 *            update listeners.
	 */
	public void removeSkyModelUpdateListener(SkyModelUpdateListener l) throws RemoteException {
		// TODO Auto-generated method stub
		if (!listeners.contains(l))
			return;
		logger.create().extractCallInfo().info().level(2).msg("Removing listener: " + l).send();

		listeners.remove(l);
	}

	/**
	 * Notify any registered listeners that a seeing update has been received
	 * and processed.
	 * 
	 * @param seeing
	 *            The seeing update (asec).
	 * @param wavelength
	 *            Instrument's wavelength (nm).
	 * @param alt
	 *            Target altitude (deg).
	 * @param az
	 *            Target azimuth (deg).
	 * @param time
	 *            Time when seeing update was generated.
	 * @param standard
	 *            True if the observation was of a photometric standard.
	 */
	private void notifyListenersSeeingUpdated(double seeing, double wavelength, double alt, double az, long time,
			boolean standard, String source, String targetName) {
		double cosz = Math.cos(0.5 * Math.PI - alt);
		double cwav = (wavelength < 750.0 ? wavelength : 750.0);
		double correction = Math.pow(cwav / 700.0, 0.45) * Math.pow(cosz, 0.5);

		Iterator list = listeners.iterator();
		while (list.hasNext()) {
			SkyModelUpdateListener l = (SkyModelUpdateListener) list.next();
			try {
				double srz = getSeeing(WAVELENGTH_RED, ZENITH, 0.0, time);
				l.seeingUpdated(time, seeing, seeing * correction, srz, alt, az, wavelength, standard, source, targetName);
				logger.create().extractCallInfo().info().level(2).msg("Notify seeingUpdated to listener: "+l).send();
			} catch (Exception e) {
				list.remove();
				logger.create().extractCallInfo().error().level(5).msg("Removed unresponsive listener: " + l).send();
			}
		}

	}

	/**
	 * Notify any registered listeners that an extinction update has been
	 * received and processed.
	 * 
	 * @param seeing
	 *            The extinction update (u).
	 * @param wavelength
	 *            Instrument's wavelength (nm).
	 * @param alt
	 *            Target altitude (deg).
	 * @param az
	 *            Target azimuth (deg).
	 * @param time
	 *            Time when extinction update was generated.
	 * @param standard
	 *            True if the observation was of a photometric standard.
	 */
	private void notifyListenersExtinctionUpdated(double extinction, double wavelength, double altitude,
			double azimuth, long time, boolean standard) {
		Iterator list = listeners.iterator();
		while (list.hasNext()) {
			SkyModelUpdateListener l = (SkyModelUpdateListener) list.next();
			try {
				System.err.println("EMS:SkyModel:notifyListnersExtinctionUpdate: " + l);
				l.extinctionUpdated(time, extinction);
			} catch (Exception e) {
				list.remove();
				logger.create().extractCallInfo().error().level(5).msg("Removed unresponsive listener: " + l).send();
			}
		}
	}

	private class Entry {
		public double value;
		public double time;

		public Entry(double time, double value) {
			this.time = time;
			this.value = value;
		}
	}

}
