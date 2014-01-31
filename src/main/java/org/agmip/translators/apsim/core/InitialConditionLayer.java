
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
public class InitialConditionLayer {
    
    // bottomDepth;
    @JsonProperty("icbl")
    private double bottomDepth;
	public void setBottomDepth(double bottomDepth) {
		this.bottomDepth = bottomDepth;
	}

    // soilWater
    @JsonProperty("ich2o")
    private double soilWater = Util.missingValue;
    public double getSoilWater() { return soilWater; }
    public void setSoilWater(double soilWater) {
		this.soilWater = soilWater;
	}

    // no3
    @JsonProperty("icno3")
    private double no3 = Util.missingValue;
    public double getNo3() { return no3; }
    public void setNo3(double no3) {
		this.no3 = no3;
	}

    // nh4
    @JsonProperty("icnh4")
    private double nh4 = Util.missingValue;
    public double getNh4() { return nh4; }
    public void setNh4(double nh4) {
		this.nh4 = nh4;
	}

    // thickness
    @JsonIgnore
    private double thickness;
    public double getThickness() { return thickness; }    
    public void setThickness(double thickness) {
		this.thickness = thickness;
	}

    @JsonIgnore
    private String log = "";
    public String getLog() { return log; }
  
    
    
    // default constructor - needed for Jackson
    public InitialConditionLayer() {}
    
    
    
    // Initialise this instance.
    public double initialise(double cumThickness, int layerNumber) {
    	
    	if (bottomDepth == Util.missingValue)
    		log += "  * Initial conditions layer " + String.valueOf(layerNumber) + " ERROR: Missing thickness (icbl).\r\n";

    	else
    		thickness = bottomDepth * 10 - cumThickness;  // convert from cm to mm
        
        if (no3 == Util.missingValue)
        	log += "  * Initial conditions layer " + String.valueOf(layerNumber) + " ERROR: Missing NO3 (icno3).\r\n";
        	
        if (nh4 == Util.missingValue)
        	log += "  * Initial conditions layer " + String.valueOf(layerNumber) + " ERROR: Missing NH4 (icnh4).\r\n";
        
        if (soilWater == Util.missingValue)
        	log += "  * Initial conditions layer " + String.valueOf(layerNumber) + " ERROR: Missing SW (ich2o).\r\n";        
                
        return bottomDepth * 10;
    }
    
    
}
