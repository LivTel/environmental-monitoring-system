/**
 * 
 */
package ngat.ems;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import java.util.Vector;

import ngat.net.cil.CilService;
import ngat.net.cil.tcs.CollatorResponseListener;
import ngat.net.cil.tcs.TcsStatusPacket;
import ngat.net.cil.tcs.TcsStatusPacket.Segment;
import ngat.net.cil.tcs.CilStatusCollator;
import ngat.util.ControlThread;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;

/**
 * @author eng
 * 
 */
public class BasicMeteorologyProvider extends UnicastRemoteObject implements MeteorologyStatusProvider,
		CollatorResponseListener {

	/** A list of MeteorologyStatusUpdateListeners. */
	private List<MeteorologyStatusUpdateListener> listeners;

	/** Collects meteo status. */
	private CilStatusCollator wmsCollator;

	// private URLStatusClient cloudCollator;

	private LogGenerator logger;

	/**
     * 
     */
	public BasicMeteorologyProvider() throws RemoteException {
		super();

		listeners = new Vector<MeteorologyStatusUpdateListener>();

		Logger alogger = LogManager.getLogger("EMS");
		logger = alogger.generate().system("EMS").subSystem("Meteorology").srcCompClass(getClass().getSimpleName())
				.srcCompId("Meteo");
	}

	/**
	 * Registers an instance of MeteorologyStatusUpdateListener to receive
	 * notification when the telescope status changes.
	 * 
	 * @param listener
	 *            An instance of MeteorologyStatusUpdateListener to register. If
	 *            listener is already registered this method returns silently.
	 * @throws RemoteException
	 *             If anything goes wrong. Meteorology implementations may
	 *             decide to automatically de-register listeners which throw an
	 *             exeception on updating.
	 */
	public void addMeteorologyStatusUpdateListener(MeteorologyStatusUpdateListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		if (listeners.contains(listener))
			return;
		listeners.add(listener);

		logger.create().info().level(3).extractCallInfo().msg("Adding listener: " + listener).send();

	}

	/**
	 * De-registers an instance of MeteorologyStatusUpdateListener from
	 * receiving notification when the telescope status changes.
	 * 
	 * @param listener
	 *            An instance of MeteorologyStatusUpdateListener to de-register.
	 *            If listener is not-already registered this method returns
	 *            silently.
	 * @throws RemoteException
	 *             If anything goes wrong.
	 */
	public void removeMeteorologyStatusUpdateListener(MeteorologyStatusUpdateListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		if (!listeners.contains(listener))
			return;
		listeners.remove(listener);
		logger.create().info().level(4).extractCallInfo().msg("Removedlistener: " + listener).send();
	}

	/**
	 * Request telescope to start monitoring its various feeds. This call starts
	 * a seperate thread/threads and returns immediately.
	 */
	public void startMonitoring(CilService cil) {

		// try {
		// System.err.println("BT: StartMonitoring: Lookup local CIL service...");
		// cil = (CilService)Naming.lookup("rmi://localhost/TCSCilService");
		// logger.create().info().level(2).extractCallInfo()
		// .msg("Located CIL service: "+cil).send();
		System.err.println("BT: StartMonitoring: cil service; " + cil);
		// } catch (Exception e) {
		// System.err.println("BT: Failed to locate CIL service, stack trace follows...");
		// e.printStackTrace();
		// }

		// start MECH monitor
		wmsCollator = new ngat.net.cil.tcs.CilStatusCollator(this, "METEOROLOGY", cil, 10000L);
		wmsCollator.start();
	}

	public void tcsStatusPacketUpdate(Segment data) throws RemoteException {
		// update local data
		logger.create().info().level(4).extractCallInfo()
				.msg("Recieved status packet, class: " + data.getClass().getName()).send();

		if (data instanceof TcsStatusPacket.Meteorology) {

			TcsStatusPacket.Meteorology wmsData = (TcsStatusPacket.Meteorology) data;
			logger.create().info().level(4).extractCallInfo().msg("Recieved: " + wmsData).send();

			// create a WmsStatus object
			WmsStatus wmsStatus = new WmsStatus();
			wmsStatus.setStatusTimeStamp(data.getTimeStamp());
			// populate it with the stuff extracted from the packet
			// eg wmsStatus.setHumidity(wmsData.humidity);
			wmsStatus.setHumidity(wmsData.humidity);
			wmsStatus.setExtTemperature(wmsData.extTemperature);
			wmsStatus.setWindSpeed(wmsData.windSpeed);
			wmsStatus.setRainState(wmsData.rainState);
			wmsStatus.setMoistureFraction(wmsData.moistureFraction);
			wmsStatus.setDewPointTemperature(wmsData.dewPointTemperature);
			wmsStatus.setLightLevel(wmsData.lightLevel);
			wmsStatus.setPressure(wmsData.pressure);
			wmsStatus.setWindDirn(wmsData.windDirn);
			wmsStatus.setWmsStatus(wmsData.wmsStatus);

			// update local status
			// eg updateWmsStatus(wmsStatus);

			// notify any meteo listeners
			notifyListeners(wmsStatus);
		}
	}

	/**
	 * Notify registered listeners that a status change has occcurred. Any
	 * listeners which fail to take the update are summarily removed from the
	 * listener list.
	 * 
	 * @param status
	 *            A status object.
	 */
	public void notifyListeners(MeteorologyStatus status) {

		MeteorologyStatusUpdateListener msul = null;
		Iterator ilist = listeners.iterator();
		while (ilist.hasNext()) {
			msul = (MeteorologyStatusUpdateListener) ilist.next();

			logger.create().info().level(4).extractCallInfo().msg("Notify listener: " + msul).send();

			try {
				msul.meteorologyStatusUpdate(status);
			} catch (Exception e) {
				e.printStackTrace();
				logger.create().info().level(1).extractCallInfo()
						.msg("Removing unresponsive listener: " + msul + " due to: " + e).send();
				ilist.remove();
			}
		}

	}

	public void tcsStatusPacketFailure(String message) throws RemoteException {
		logger.create().info().level(4).extractCallInfo().msg("Recieved status packet failure: " + message).send();

	}

	public void startMonitoringBcs(URL url) {

		// every minute
		UrlCloudStatusMonitorThread bcsCollator = new UrlCloudStatusMonitorThread(url, 65000L);
		bcsCollator.start();

	}

	public void startMonitoringDust(URL url) {

		// every 2 hours
		// UrlDustStatusMonitorThread dustCollator = new
		// UrlDustStatusMonitorThread(url, 2*3650*1000L);
		// every 2 minutes
		UrlDustStatusMonitorThread dustCollator = new UrlDustStatusMonitorThread(url, 120000L);
		dustCollator.start();

	}

	private class UrlCloudStatusMonitorThread extends Thread {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

		/** Polling interval. */
		private long interval;

		/** URL. */
		private URL url;

		/**
		 * @param url
		 * @param interval
		 */
		public UrlCloudStatusMonitorThread(URL url, long interval) {
			super();
			this.url = url;
			this.interval = interval;
			sdf.setTimeZone(UTC);
		}

		/** Poll the status source URL with requests. */
		public void run() {

			int nstat = 0; // count requests
			while (true) {
				try {
					Thread.sleep(interval);
				} catch (InterruptedException ix) {
				}
				nstat++;

				CloudStatus cloudStatus = new CloudStatus();

				try {

					URLConnection uc = url.openConnection();

					uc.setDoInput(true);
					uc.setAllowUserInteraction(false);

					InputStream in = uc.getInputStream();
					BufferedReader din = new BufferedReader(new InputStreamReader(in));

					String line = din.readLine();
					// System.err.println("CLOUD: received: "+line);

					StringTokenizer st = new StringTokenizer(line);

					// extract the BCS data
					String stime = st.nextToken();

					long time = sdf.parse(stime).getTime();
					double skyMinusAmb = Double.parseDouble(st.nextToken());
					double amb = Double.parseDouble(st.nextToken());
					double sensor = Double.parseDouble(st.nextToken());
					double heater = Double.parseDouble(st.nextToken());
					double wet = Double.parseDouble(st.nextToken());

					cloudStatus.setStatusTimeStamp(time);
					cloudStatus.setSkyMinusAmb(skyMinusAmb);
					cloudStatus.setAmbientTemp(amb);
					cloudStatus.setSensorTemp(sensor);
					cloudStatus.setHeater(heater);
					cloudStatus.setWetFlag(wet);

					try {
						in.close();
						din.close();
						uc = null;
						
					} catch (Exception e) {
						System.err.println(("CLOUD: WARNING: Failed to close URL input stream: " + e));
					}

				} catch (Exception e) {
					System.err.println("CLOUD: ERROR: " + e);
				}
				// System.err.println("Sending out: "+cloudStatus);
				notifyListeners(cloudStatus);

			} // next sample
		} // run

	}

	// Dust status
	private class UrlDustStatusMonitorThread extends Thread {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

		/** Polling interval. */
		private long interval;

		/** URL. */
		private URL url;

		/**
		 * @param url
		 * @param interval
		 */
		public UrlDustStatusMonitorThread(URL url, long interval) {
			super();
			this.url = url;
			this.interval = interval;
			sdf.setTimeZone(UTC);
		}

		/** Poll the status source URL with requests. */
		public void run() {

			int nstat = 0; // count requests
			while (true) {
				try {
					Thread.sleep(interval);
				} catch (InterruptedException ix) {
				}
				nstat++;

				DustStatus dustStatus = new DustStatus();

				try {

					URLConnection uc = url.openConnection();

					uc.setDoInput(true);
					uc.setAllowUserInteraction(false);

					InputStream in = uc.getInputStream();
					BufferedReader din = new BufferedReader(new InputStreamReader(in));

					String line = din.readLine();
					// System.err.println("TNG_DUST: received: "+line);

					StringTokenizer st = new StringTokenizer(line);

					// extract the BCS data
					String stime = st.nextToken();

					long time = sdf.parse(stime).getTime();
					double dust = Double.parseDouble(st.nextToken());

					dustStatus.setStatusTimeStamp(time);
					dustStatus.setDust(dust);

					try {
						in.close();
						din.close();
						uc = null;
					} catch (Exception e) {
						System.err.println(("TNG_DUST: WARNING: Failed to close URL input stream: " + e));
					}

				} catch (Exception e) {
					System.err.println("TNG_DUST: ERROR: " + e);
				}
				// System.err.println("Sending out: "+dustStatus);
				notifyListeners(dustStatus);

			} // next sample
		} // run

	} // urlclient

}
