/**
 * 
 */
package ngat.ems.test;

import java.net.URL;
import java.rmi.Naming;

import ngat.ems.BasicMeteorologyProvider;
import ngat.net.cil.CilService;
import ngat.net.cil.tcs.TcsStatusPacket;
import ngat.util.logging.BasicLogFormatter;
import ngat.util.logging.ConsoleLogHandler;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;
import ngat.util.ConfigurationProperties;
import ngat.util.CommandTokenizer;

/**
 * @author eng
 * 
 */
public class CheckMeteorology {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

		    ConfigurationProperties config = CommandTokenizer.use("--").parse(args);
		    
		    String cilHost = config.getProperty("cil-host", "localhost");
		    String cilServiceName = config.getProperty("cil-svc", "TcsCilService");

		    boolean checkWms = config.getBooleanValue("wms", false);
		    boolean checkBcs = config.getBooleanValue("bcs", false);
		    boolean checkTng = config.getBooleanValue("tng", false);

		    CilService cil = (CilService) Naming.lookup("rmi://" + cilHost + "/" + cilServiceName);
		    
		    System.err.println("Located CILService: "+cil);
		    
		    Logger alogger = LogManager.getLogger("EMS");
		    alogger.setLogLevel(5);
		    ConsoleLogHandler console = new ConsoleLogHandler(new BasicLogFormatter(150));
		    console.setLogLevel(5);
		    alogger.addExtendedHandler(console);
		    
		    TcsStatusPacket.mapCodes();
		    
		    BasicMeteorologyProvider bmp = new BasicMeteorologyProvider();

		    if (checkWms)
			bmp.startMonitoring(cil);
			
		    if (checkBcs) {
			String cloudUrlStr = config.getProperty("bcs-url", "file:///occ/data/cloud.dat");
			URL cloudUrl = new URL(cloudUrlStr);
			bmp.startMonitoringBcs(cloudUrl);
		    }
		    
		    if (checkTng) {
			String dustUrlStr = config.getProperty("tng-url", "file:///occ/data/tng_dust.dat");
			URL dustUrl = new URL(dustUrlStr);
			bmp.startMonitoringDust(dustUrl);
		    }
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
