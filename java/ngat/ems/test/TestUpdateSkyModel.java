package ngat.ems.test;

import ngat.ems.*;
import ngat.util.*;
import java.rmi.*;

public class TestUpdateSkyModel {

    public static void main(String args[]) {

	try {	

	    ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);
	    String host = cfg.getProperty("host", "localhost");
	    int    port = cfg.getIntValue("port", 1099);
	    String ref  = cfg.getProperty("ref", "SkyModel");
	    
	    double seeing = cfg.getDoubleValue("seeing", 0.77);
	    double wavelength = cfg.getDoubleValue("wavelength", 700.0);

	    String xref = "rmi://"+host+":"+port+"/"+ref;
	    SkyModel sm = (SkyModel)Naming.lookup(xref);
	    long now = System.currentTimeMillis();

	    double alt = Math.toRadians(cfg.getDoubleValue("alt", 90.0));

	    boolean std = (cfg.getProperty("std") != null);
	    
	    System.err.println("Lookup: "+xref+" found: "+sm);

	    // set
	    ((MutableSkyModel)(sm)).updateSeeing(seeing, wavelength, alt, 0.0, now, std, "test", "star");

	    // get
	    double talt = 20.0;
	    while (talt <= 90.0) {
		double pseeing = sm.getSeeing(700.0, Math.toRadians(talt), 0.0, System.currentTimeMillis());	   
		System.err.println("Elevation: "+talt+", Seeing: "+pseeing);
		talt += 10.0;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}