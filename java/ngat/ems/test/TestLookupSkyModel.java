package ngat.ems.test;

import ngat.ems.*;
import java.rmi.*;

public class TestLookupSkyModel {

    public static void main(String args[]) {

	try {	

	    String host = args[0];
	    int    port = Integer.parseInt(args[1]);
	    String ref  = args[2];

	    String xref = "rmi://"+host+":"+port+"/"+ref;
	    SkyModel sm = (SkyModel)Naming.lookup(xref);

	    System.err.println("Lookup: "+xref+" found: "+sm);

	    double alt = 20.0;
	    while (alt <= 90) {
		double seeing = sm.getSeeing(700.0, Math.toRadians(alt), 0.0, System.currentTimeMillis());
		double extinction = sm.getExtinction(0.0, 0.0, 0.0, System.currentTimeMillis());
		System.err.println("Elevation: "+alt+", Seeing: "+seeing+", Extinction="+extinction);
		alt += 10.0;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

}