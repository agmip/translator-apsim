package org.agmip.translators.apsim.core;

import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Dean Holzworth, CSIRO
 * @author Ioannis N. Athanasiadis, DUTh
 * @since Jul 13, 2012
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SoilLayer {
    // bottomDepth
    @JsonProperty("sllb")
    private double bottomDepth = Util.missingValue;
	public void setBottomDepth(double bottomDepth) {
		this.bottomDepth = bottomDepth;
	}
	
    // thickness
    @JsonIgnore
    private double thickness = Util.missingValue;
    public double getThickness() { return thickness; }
	public void setThickness(double thickness) {
		this.thickness = thickness;
	}

    // bulkDensity
    @JsonProperty("slbdm")
    private double bulkDensity = Util.missingValue;
    public double getBulkDensity() { return bulkDensity; }
	public void setBulkDensity(double bulkDensity) {
		this.bulkDensity = bulkDensity;
	}
	
    @JsonProperty("sladr")
    private double airDry = Util.missingValue;
    public double getAirDry() { return airDry; }
    
    // lowerLimit
    @JsonProperty("slll")
    private double lowerLimit = Util.missingValue;
    public double getLowerLimit() { return lowerLimit; }
	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	
    // drainedUpperLimit
    @JsonProperty("sldul")
    private double drainedUpperLimit = Util.missingValue;
    public double getDrainedUpperLimit() { return drainedUpperLimit; }
	public void setDrainedUpperLimit(double drainedUpperLimit) {
		this.drainedUpperLimit = drainedUpperLimit;
	}
    // saturation
    @JsonProperty("slsat")
    private double saturation = Util.missingValue; 
    public double getSaturation() { return saturation; }
	public void setSaturation(double saturation) {
		this.saturation = saturation;
	}
	
    // organicCarbon
    @JsonProperty("sloc")
    private double organicCarbon = Util.missingValue;
    public double getOrganicCarbon() { return organicCarbon; }
	public void setOrganicCarbon(double organicCarbon) {
		this.organicCarbon = organicCarbon;
	}
	
    // ph
    @JsonProperty("slphw")
    private double ph = Util.missingValue;
    public double getPh() { return ph; }
	public void setPh(double ph) {
		this.ph = ph;
	}
	

    //kl
    @JsonProperty("apsim_kl")
    private double kl = Util.missingValue;
    public double getKl() { return kl; }
    
    // fbiom
    @JsonProperty("slacc")
    private double biomC = Util.missingValue;
    @JsonProperty("slfac")
    private double biomCFraction = Util.missingValue;
    public double getFbiom() {
        if (biomCFraction != Util.missingValue) {
            return biomCFraction;
        } else if (organicCarbon == 0) {
            return 0;
        } else {
            return biomC / organicCarbon;
        } 
     }
    
    // finert
    @JsonProperty("slic")
    private double inertC = Util.missingValue;
    @JsonProperty("slfic")
    private double inertCFraction = Util.missingValue;
    public double getFinert() {
        if (inertCFraction != Util.missingValue) {
            return inertCFraction;
        } else if (organicCarbon == 0) {
            return 0;
        } else {
            return inertC / organicCarbon; 
    	}
    }

    // ks
    @JsonProperty("sksat")
    private double ksat = Util.missingValue;
    public double getKsat() { 
    	if (ksat == Util.missingValue)
    		return Util.missingValue;
    	else
    		return ksat * 10.0 * 24.0;   // Convert from cm/h to mm/day 
    	}
    
    // swcon
    @JsonProperty("sldrl")
    private double swcon = Util.missingValue;
    public double getSwcon() { return swcon; }
    public void setSwcon(double swcon) {
		this.swcon = swcon;
	}    
    
    // xf
    @JsonProperty("slrgf")
    private double xf = Util.missingValue;
    public double getXf() { return xf; }
    public void setXf(double xf) {
		this.xf = xf;
	}
    
    @JsonIgnore
    private String log;
    public String getLog() { return log; }    
    
    

    // Constructor - Needed for Jackson
    public SoilLayer() {}
    

    
    // Initialisation routine
    public double initialise(double cumThickness, int layerNumber, int numLayers) throws Exception {
        log = "";
        
        if (bottomDepth == Util.missingValue)
            log += "  * Soil layer " + String.valueOf(layerNumber) + " ERROR: Missing thickness (sllb).\r\n";

        if (bulkDensity == Util.missingValue)
            log += "  * Soil layer " + String.valueOf(layerNumber) + " ERROR: Missing bulk density (slbdm).\r\n";

        if (airDry == Util.missingValue)
            log += "  * Soil layer " + String.valueOf(layerNumber) + " ERROR: Missing airdry.\r\n";
        
        if (lowerLimit == Util.missingValue)
            log += "  * Soil layer " + String.valueOf(layerNumber) + " ERROR: Missing lowerLimit (slll).\r\n";
        
        if (drainedUpperLimit == Util.missingValue)
            log += "  * Soil layer " + String.valueOf(layerNumber) + " ERROR: Missing drained upper limit (sldul).\r\n";

        if (saturation == Util.missingValue)
            log += "  * Soil layer " + String.valueOf(layerNumber) + " ERROR: Missing saturation (slsat).\r\n";

        if (organicCarbon == Util.missingValue)
            log += "  * Soil layer " + String.valueOf(layerNumber) + " ERROR: Missing organ carbon (sloc).\r\n";
        
        if (ph == Util.missingValue)
            log += "  * Soil layer " + String.valueOf(layerNumber) + " ERROR: Missing PH (slphw).\r\n";
        
        if (kl == Util.missingValue)
            log += "  * Soil layer " + String.valueOf(layerNumber) + " ERROR: Missing KL (apsim_kl).\r\n";
        
        if (xf == Util.missingValue)
            log += "  * Soil layer " + String.valueOf(layerNumber) + " ERROR: Missing XF (slrgf).\r\n";

        if (biomC == Util.missingValue && biomCFraction == Util.missingValue)
            log += "  * Soil layer " + String.valueOf(layerNumber) + " ERROR: Missing FBIOM (both slacc and slfac).\r\n";
        
        if (inertC == Util.missingValue && inertCFraction == Util.missingValue)
            log += "  * Soil layer " + String.valueOf(layerNumber) + " ERROR: Missing InertC (both slic and slfic).\r\n";
        
        thickness = bottomDepth * 10 - cumThickness;
        
        return bottomDepth * 10;
    }
    
}