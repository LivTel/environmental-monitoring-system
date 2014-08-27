/**
 * 
 */
package ngat.ems;

import java.io.Serializable;

/** Base class for sky model status updates.
 * @author eng
 *
 */
public class SkyModelUpdate implements Serializable {

	/** Time at which this status update was valid.*/
	 private long statusTimeStamp;
	 
	/**
	 * @param statusTimeStamp
	 */
	public SkyModelUpdate(long statusTimeStamp) {
		super();
		this.statusTimeStamp = statusTimeStamp;
	}

	/**
	 * @return the statusTimeStamp
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
	

}
