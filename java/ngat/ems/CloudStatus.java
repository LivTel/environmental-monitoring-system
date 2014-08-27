/**
 * 
 */
package ngat.ems;

import java.util.Date;

/**
 * @author eng
 *
 */
public class CloudStatus implements MeteorologyStatus {
	
	private long statusTimeStamp;
	
	/** Difference beteen sky and ambient temperature (degC).*/
	private double skyMinusAmb;
	
	/** Ambient temperature (degC).*/
	private double ambientTemp;
	
	/** Sensor temperature (degC).*/
	private double sensorTemp;
	
	/** Heater output (?).*/
	private double heater;
	
	/** Wetness (?).*/
	private double wetFlag;

	
	/**
	 * @return the skyMinusAmb
	 */
	public double getSkyMinusAmb() {
		return skyMinusAmb;
	}

	/**
	 * @param skyMinusAmb the skyMinusAmb to set
	 */
	public void setSkyMinusAmb(double skyMinusAmb) {
		this.skyMinusAmb = skyMinusAmb;
	}

	/**
	 * @return the ambientTemp
	 */
	public double getAmbientTemp() {
		return ambientTemp;
	}

	/**
	 * @param ambientTemp the ambientTemp to set
	 */
	public void setAmbientTemp(double ambientTemp) {
		this.ambientTemp = ambientTemp;
	}

	/**
	 * @return the sensorTemp
	 */
	public double getSensorTemp() {
		return sensorTemp;
	}

	/**
	 * @param sensorTemp the sensorTemp to set
	 */
	public void setSensorTemp(double sensorTemp) {
		this.sensorTemp = sensorTemp;
	}

	/**
	 * @return the heater
	 */
	public double getHeater() {
		return heater;
	}

	/**
	 * @param heater the heater to set
	 */
	public void setHeater(double heater) {
		this.heater = heater;
	}

	/**
	 * @return the wetFlag
	 */
	public double getWetFlag() {
		return wetFlag;
	}

	/**
	 * @param wetFlag the wetFlag to set
	 */
	public void setWetFlag(double wetFlag) {
		this.wetFlag = wetFlag;
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
		return "CLOUD"; // or BCS_CLOUD
	}

	

	/** @return A readable description of this status.*/
	public String toString() {
		// maybe a timestamp?
		return "BCS Sensor: "+sensorTemp+
		    ", Ambient: "+ambientTemp+
		    ", Sky-Amb: "+skyMinusAmb+
		    " , Heat: "+heater;
	}
}
