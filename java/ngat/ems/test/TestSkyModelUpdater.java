/**
 * 
 */
package ngat.ems.test;

import java.rmi.Naming;

import ngat.ems.MutableSkyModel;

/**
 * @author snf
 *
 */
public class TestSkyModelUpdater {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			
			String host = args[0];
			double value = Double.parseDouble(args[1]);
			MutableSkyModel sky = (MutableSkyModel)Naming.lookup("rmi://"+host+"/SkyModel");
			sky.updateSeeing(value,0.0,0.0,0.0,System.currentTimeMillis(), false, "test", "star");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
