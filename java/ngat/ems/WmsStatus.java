/**
 * 
 */
package ngat.ems;

import ngat.net.cil.tcs.TcsStatusPacket;

/**
 * @author eng
 *
 */
public class WmsStatus implements MeteorologyStatus {

	private long statusTimeStamp;
	
	 /** Records the WMS status.*/
    private int wmsStatus;

    /** Records the external rain state.*/
    private int rainState;

    /** Records the fraction of moisture covering the rain-sensor.*/
    private double moistureFraction = 1.0;

    /** Records the external air temperature (K).*/
    private double extTemperature = -99.0;

    /** Records the external wind speed (m/s).*/
    private double windSpeed = 99.99;

    /** Records the external air pressure (mBar).*/
    private double pressure;

    /** Records the external humidity (fraction).*/
    private double humidity = 1.0;

    /** Records the external wind direction (degrees c/w North !!).*/
    private double windDirn;

    /** Records the Dew-point temperature. (K).*/
    private double dewPointTemperature = 0.0;

    /** Records the light level (W/m2). ##INSTRUMENT NOT YET IMPLEMENTED##*/
    private double lightLevel;

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

	/**
	 * @return the wmsStatus
	 */
	public int getWmsStatus() {
		return wmsStatus;
	}

	/**
	 * @param wmsStatus the wmsStatus to set
	 */
	public void setWmsStatus(int wmsStatus) {
		this.wmsStatus = wmsStatus;
	}

	/**
	 * @return the rainState
	 */
	public int getRainState() {
		return rainState;
	}

	/**
	 * @param rainState the rainState to set
	 */
	public void setRainState(int rainState) {
		this.rainState = rainState;
	}

	/**
	 * @return the moistureFraction
	 */
	public double getMoistureFraction() {
		return moistureFraction;
	}

	/**
	 * @param moistureFraction the moistureFraction to set
	 */
	public void setMoistureFraction(double moistureFraction) {
		this.moistureFraction = moistureFraction;
	}

	/**
	 * @return the extTemperature
	 */
	public double getExtTemperature() {
		return extTemperature;
	}

	/**
	 * @param extTemperature the extTemperature to set
	 */
	public void setExtTemperature(double extTemperature) {
		this.extTemperature = extTemperature;
	}

	/**
	 * @return the windSpeed
	 */
	public double getWindSpeed() {
		return windSpeed;
	}

	/**
	 * @param windSpeed the windSpeed to set
	 */
	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}

	/**
	 * @return the pressure
	 */
	public double getPressure() {
		return pressure;
	}

	/**
	 * @param pressure the pressure to set
	 */
	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	/**
	 * @return the humidity
	 */
	public double getHumidity() {
		return humidity;
	}

	/**
	 * @param humidity the humidity to set
	 */
	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	/**
	 * @return the windDirn
	 */
	public double getWindDirn() {
		return windDirn;
	}

	/**
	 * @param windDirn the windDirn to set
	 */
	public void setWindDirn(double windDirn) {
		this.windDirn = windDirn;
	}

	/**
	 * @return the dewPointTemperature
	 */
	public double getDewPointTemperature() {
		return dewPointTemperature;
	}

	/**
	 * @param dewPointTemperature the dewPointTemperature to set
	 */
	public void setDewPointTemperature(double dewPointTemperature) {
		this.dewPointTemperature = dewPointTemperature;
	}

	/**
	 * @return the lightLevel
	 */
	public double getLightLevel() {
		return lightLevel;
	}

	/**
	 * @param lightLevel the lightLevel to set
	 */
	public void setLightLevel(double lightLevel) {
		this.lightLevel = lightLevel;
	}
	
	public String toString() {
		// maybe a timestamp?
		return "WMS "+"Humidity: "+humidity+", Temp: "+extTemperature+", Rain: "+TcsStatusPacket.codeString(rainState);
	}

	public String getCategoryName() {
		return "WMS";
	}
    
}
