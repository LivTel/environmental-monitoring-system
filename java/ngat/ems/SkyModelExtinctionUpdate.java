/**
 * 
 */
package ngat.ems;

/**
 * @author eng
 *
 */
public class SkyModelExtinctionUpdate extends SkyModelUpdate {

	/** Extinction value (0.0(=clear) -> 1.0(=wipedout)).*/
	private double extinction;
	
	
	/**
	 * @param statusTimeStamp
	 * @param extinction
	 */
	public SkyModelExtinctionUpdate(long statusTimeStamp, double extinction) {
		super(statusTimeStamp);
		this.extinction = extinction;
	}

	/**
	 * @return the extinction
	 */
	public double getExtinction() {
		return extinction;
	}

	/**
	 * @param extinction the extinction to set
	 */
	public void setExtinction(double extinction) {
		this.extinction = extinction;
	}

}
