/**
 * 
 */
package ngat.ems;

import java.util.Date;

/**
 * @author eng
 *
 */
public class DustStatus implements MeteorologyStatus {
	
    private long statusTimeStamp;
	
    /** Dust concentration (mu-g/m3).*/
	private double dust;
	
	
	/**
	 * @return the dust concentration.
	 */
	public double getDust() {
		return dust;
	}

	/**
	 * @param dust the dust concentration to set.
	 */
	public void setDust(double dust) {
	    this.dust = dust;
	}
	
	/**
	 * @see ngat.net.telemetry.StatusCategory#getStatusTimeStamp()
	 */
	public long getStatusTimeStamp() {
		return statusTimeStamp;
	}
	
	/**
	 * @param statusTimeStamp the statusTimeStamp to set
	 */
	public void setStatusTimeStamp(long statusTimeStamp) {
		this.statusTimeStamp = statusTimeStamp;
	}

	
	/**
	 * @see ngat.net.telemetry.StatusCategory#getCategoryName()
	 */
	public String getCategoryName() {
		return "DUST"; // or TNG_DUST
	}

	

	/** @return A readable description of this status.*/
	public String toString() {
	    // maybe a timestamp?
	    return "TNG Dust Monitor: "+dust;
	}
}
