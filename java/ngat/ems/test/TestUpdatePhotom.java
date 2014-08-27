package ngat.ems.test;

import ngat.ems.*;
import ngat.util.*;
import java.rmi.*;

public class TestUpdatePhotom {

    public static void main(String args[]) {

	try {	

	    ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);
	    String host = cfg.getProperty("host", "localhost");
	    int    port = cfg.getIntValue("port", 1099);
	    String ref  = cfg.getProperty("ref", "SkyModel");
	    
	    double seeing = cfg.getDoubleValue("seeing", 0.77);

	    boolean photometric = (cfg.getProperty("photometric") != null);
	    double ext = 1.0;

	    if (photometric) {
		System.err.println("Updating skymodel with extinction as: PHOTOMETRIC");
		ext = 0.0;
	    } else {
		System.err.println("Updating skymodel with extinction as: SPECTOMETRIC");
		ext = 1.0;
	    }

	    String xref = "rmi://"+host+":"+port+"/"+ref;
	    SkyModel sm = (SkyModel)Naming.lookup(xref);
	    long now = System.currentTimeMillis();

	    System.err.println("Lookup: "+xref+" found: "+sm);

	    // set - most params are ignored
	    ((MutableSkyModel)(sm)).updateExtinction(ext, 0.0, 0.0, 0.0, now, true);

	    // get
	    double pext = sm.getExtinction(700.0, Math.toRadians(90.0), 0.0, System.currentTimeMillis());	   
	    System.err.println("Model extinction set to: "+pext);
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}