/**
 * 
 */
package ngat.ems.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleEdge;

import ngat.ems.SkyModel;
import ngat.ems.SkyModelMonitor;
import ngat.ems.SkyModelUpdateListener;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/**
 * @author snf
 * 
 */
public class TestSkyModelMonitor extends UnicastRemoteObject implements SkyModelUpdateListener {

	/** How often we check for registration. */
	public static final long REGISTRATION_CHECK_INTERVAL = 5 * 60 * 1000L;

	public static final Color GRAPH_BGCOLOR = new Color(204, 204, 153);

	public static final Color CHART_BGCOLOR = new Color(153, 203, 203);

	public static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss z");

	public static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");

	public static NumberFormat nf = NumberFormat.getInstance();

	/** Where/what is this scope. */
	private String scope;

	/** How often to ask for seeing predictions (ms). */
	private long cadence;

	private TimeSeries tsPrediction;

	private TimeSeries tsRaw;

	private TimeSeries tsCorrected;

	private TimeSeries tsRawStd;

	private TimeSeries tsCorrectedStd;


	public TestSkyModelMonitor(String scope, long cadence) throws RemoteException {
		super();
		this.scope = scope;		
		this.cadence = cadence;
	}

	/** Create a graphing frame for plotting updates. */
	public void createGraphFrame() {

	// Create chart frame.
		JFrame f = new JFrame("Seeing update monitor: " + scope);
	
		ChartPanel cp = createChartPanel();
		f.getContentPane().add(cp);

		f.pack();
		f.setVisible(true);

	}

	public ChartPanel createChartPanel() {


		// Create time series
		TimeSeriesCollection tsc = new TimeSeriesCollection();
		tsPrediction = new TimeSeries("Prediction", Second.class);
		tsPrediction.setMaximumItemCount(3500);
		tsRaw = new TimeSeries("Raw Sci", Second.class);
		tsRaw.setMaximumItemAge(24 * 3600 * 1000L);
		tsCorrected = new TimeSeries("Corrected Sci", Second.class);
		tsCorrected.setMaximumItemAge(24 * 3600 * 1000L);
		tsRawStd = new TimeSeries("Raw Std", Second.class);
		tsRawStd.setMaximumItemAge(24 * 3600 * 1000L);
		tsCorrectedStd = new TimeSeries("Corrected Std", Second.class);
		tsCorrectedStd.setMaximumItemAge(24 * 3600 * 1000L);

		tsc.addSeries(tsPrediction);
		tsc.addSeries(tsRaw);
		tsc.addSeries(tsCorrected);
		tsc.addSeries(tsRawStd);
		tsc.addSeries(tsCorrectedStd);

		// Create plots
		JFreeChart chart = ChartFactory.createTimeSeriesChart("Seeing Prediction Monitor", "Time [UT]",
				"Seeing [arcsec]", tsc, false, true, false);
		
		XYPlot plot = chart.getXYPlot();
		
		LegendTitle legendTitle = new LegendTitle(plot);
		legendTitle.setPosition(RectangleEdge.RIGHT);
		chart.addLegend(legendTitle);
	
		plot.setBackgroundPaint(GRAPH_BGCOLOR);
		plot.setDomainGridlinePaint(Color.BLUE);
		plot.setDomainGridlineStroke(new BasicStroke(0.3f));
		plot.setRangeGridlinePaint(Color.blue);
		plot.setRangeGridlineStroke(new BasicStroke(0.3f));

		XYItemRenderer renderer = plot.getRenderer();
	
		renderer.setSeriesStroke(0, new BasicStroke(2.0f));
		
		renderer.setSeriesShape(1, new Polygon(new int[] { -2, 2, 0 }, new int[] { -2, -2, 2 }, 3));
		renderer.setSeriesShape(2, new Rectangle(3, 3));
		renderer.setSeriesShape(3, new Polygon(new int[] { -2, 2, 0 }, new int[] { -2, -2, 2 }, 3));
		renderer.setSeriesShape(4, new Rectangle(3, 3));

		renderer.setSeriesStroke(1, new BasicStroke(1.0f));	
		renderer.setSeriesStroke(2, new BasicStroke(1.0f));	
		renderer.setSeriesStroke(3, new BasicStroke(1.0f));	
		renderer.setSeriesStroke(4, new BasicStroke(1.0f));	


		XYLineAndShapeRenderer lsr = (XYLineAndShapeRenderer)renderer;	
		lsr.setDrawOutlines(true);
		
		lsr.setSeriesShapesVisible(1, true);
		lsr.setSeriesLinesVisible(1, false);	

		lsr.setSeriesShapesVisible(2, true);
		lsr.setSeriesLinesVisible(2, false);	

		lsr.setSeriesShapesVisible(3, true);
		lsr.setSeriesLinesVisible(3, false);	

		lsr.setSeriesShapesVisible(4, true);
		lsr.setSeriesLinesVisible(4, false);	

		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(6 * 3600 * 1000.0); // 6 hours

		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setRange(0.0, 5.0);

		ChartPanel cp = new ChartPanel(chart);
		return cp;

	}

	/** Start monitoring the seeing prediction. */
	public void run(String host) {
		
		SkyModel sky = null;
		long lastRegistered = 0L;
		while (true) {
			try {
				if (System.currentTimeMillis() - lastRegistered > REGISTRATION_CHECK_INTERVAL) {
					sky = (SkyModel) Naming.lookup("rmi://" + host + "/SkyModel");
					System.err.println("Located skymodel: " + sky);
					System.err.println("Attempting to re-register with SkyModel Monitor...");
					((SkyModelMonitor) sky).addSkyModelUpdateListener(this);
					System.err.println("Reregistration completed");
					lastRegistered = System.currentTimeMillis();
				}
				double s = sky.getSeeing(700.0, Math.toRadians(90.0), 0.0, System.currentTimeMillis());
				System.err.println("Predicted Seeing: " + s);
				tsPrediction.add(new Second(), s);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(cadence);
			} catch (InterruptedException ix) {

			}
		}
	}

	/** Notification that seeing was updated. */
	public void seeingUpdated(long time, double rawSeeing, double correctedSeeing, double prediction, double alt, double azm, double wavelength, boolean std, String source, String targetName) throws RemoteException {
		
		// TODO Add series for STD and instrument specifics
		
		if (std) {
			tsRawStd.add(new Second(new Date(time)), rawSeeing);
			tsCorrectedStd.add(new Second(new Date(time)), correctedSeeing);
		} else {
			tsRaw.add(new Second(new Date(time)), rawSeeing);
			tsCorrected.add(new Second(new Date(time)), correctedSeeing);
		}
		
		
		
		System.err.println((std ? "STD":"SCI")+" Seeing update: T=" + sdf.format(new Date(time)) + ", R="
				+ rawSeeing + ", C=" + correctedSeeing+", P="+prediction+", Src="+source);
	}
	
	public void extinctionUpdated(long time, double extinction) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestSkyModelMonitor monitor = null;
		SkyModel sky = null;
		try {

			ConfigurationProperties config = CommandTokenizer.use("--").parse(args);

			String scope = config.getProperty("scope", "Unknown");
			String host = config.getProperty("host", "localhost");
			long cadence = config.getLongValue("cadence", 10000L);

			monitor = new TestSkyModelMonitor(scope, cadence);
			monitor.createGraphFrame();
			
			System.err.println("Starting prediction mointoring...");
			monitor.run(host);
		
		} catch (Exception e) {
			e.printStackTrace();
			System.err
					.println("Usage: java ngat.ems.test.TestSkyModelMonitor --scope <scope-info> --host <sm-host> --cadence <millis>");
		}
				
	}

	

}
