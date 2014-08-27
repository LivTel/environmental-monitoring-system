package ngat.ems;

public class SkyModelSeeingUpdate extends SkyModelUpdate {

	/** Raw seeing value.*/
    private double rawSeeing;

    /** Corrected seeing value.*/
    private double correctedSeeing;

    /** Predicted seeing at zenith in red (700nm).*/
    private double predictedSeeing;
    
    /** Source of the seeing update - eg an instrument. may add more to this eg target/group/execid etc*/
    private String source;
    
    /** Name of the target observed from which the seeing data was derived.*/
    private String targetName;
    
    /** True if this update was a standard.*/
	private boolean standard;

	/** Wavelength of observation.*/
	private double wavelength;

	/** Target elevation at time of observation.*/
	private double elevation;
	
	/** Target azimuth at time of observation.*/
	private double azimuth;
	
	/**
	 * @param statusTimeStamp
	 * @param rawSeeing
	 * @param correctedSeeing
	 * @param predictedSeeing
	 * @param source
	 */
	public SkyModelSeeingUpdate(long statusTimeStamp, double rawSeeing, double correctedSeeing, double predictedSeeing,
			boolean standard, String source) {
		super(statusTimeStamp);
		this.rawSeeing = rawSeeing;
		this.correctedSeeing = correctedSeeing;
		this.predictedSeeing = predictedSeeing;
		this.standard = standard;
		this.source = source;
	}

	/**
	 * @return the predictedSeeing
	 */
	public double getPredictedSeeing() {
		return predictedSeeing;
	}

	/**
	 * @param predictedSeeing the predictedSeeing to set
	 */
	public void setPredictedSeeing(double predictedSeeing) {
		this.predictedSeeing = predictedSeeing;
	}

	/**
	 * @return the standard
	 */
	public boolean isStandard() {
		return standard;
	}

	/**
	 * @param standard the standard to set
	 */
	public void setStandard(boolean standard) {
		this.standard = standard;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the targetName
	 */
	public String getTargetName() {
		return targetName;
	}

	/**
	 * @param targetName the targetName to set
	 */
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	/**
	 * @return the rawSeeing
	 */
	public double getRawSeeing() {
		return rawSeeing;
	}

	/**
	 * @param rawSeeing the rawSeeing to set
	 */
	public void setRawSeeing(double rawSeeing) {
		this.rawSeeing = rawSeeing;
	}

	/**
	 * @return the correctedSeeing
	 */
	public double getCorrectedSeeing() {
		return correctedSeeing;
	}

	/**
	 * @param correctedSeeing the correctedSeeing to set
	 */
	public void setCorrectedSeeing(double correctedSeeing) {
		this.correctedSeeing = correctedSeeing;
	}

	
	
	/**
	 * @return the wavelength
	 */
	public double getWavelength() {
		return wavelength;
	}

	/**
	 * @param wavelength the wavelength to set
	 */
	public void setWavelength(double wavelength) {
		this.wavelength = wavelength;
	}

	/**
	 * @return the elevation
	 */
	public double getElevation() {
		return elevation;
	}

	/**
	 * @param elevation the elevation to set
	 */
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

	/**
	 * @return the azimuth
	 */
	public double getAzimuth() {
		return azimuth;
	}

	/**
	 * @param azimuth the azimuth to set
	 */
	public void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SkyModelSeeingUpdate [alt="+elevation+", azm="+azimuth+", rs=" + rawSeeing + ", cs=" + correctedSeeing
				+ ", ps=" + predictedSeeing + ", src=" + source + ", tgt="+targetName+"]";
	}
	
	

}