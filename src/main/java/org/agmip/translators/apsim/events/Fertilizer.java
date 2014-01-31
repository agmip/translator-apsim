package org.agmip.translators.apsim.events;

import org.agmip.translators.apsim.core.Management;
import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @since Jul 13, 2012
 */

public class Fertilizer extends Event {
	
    // method
    //@JsonProperty("feacd")
    //private String method;

    // material
    //@JsonProperty("fecd")
    //private String material;
    
    //@JsonProperty("feamp")
    //private String phosphorus;

    //@JsonProperty("feamk")
    //private String potasium;

    @JsonProperty("fedep")
    private double depth = Util.missingValue;

    @JsonProperty("feamn")
    private double nitrogen = Util.missingValue;

    @Override
    public String getApsimAction() {
        return "fertiliser apply amount = " + nitrogen + " (kg/ha), type = no3_n (), depth = " + depth + " (mm)";
    }

    
    
    
    
    
    @Override
    public void initialise(Management management) {
        if ("?".equals(getDate()))
            log += "  * Operation fertiliser ERROR: Date missing (date).\r\n";
        
        if (depth == Util.missingValue)
        	log += "  * Operation " + getDate() + " ERROR: Fertiliser depth missing (fedep).\r\n";
        
        if (nitrogen == Util.missingValue)
            log += "  * Operation " + getDate() + " ERROR: Fertiliser nitrogen amount missing (feamn).\r\n";
    }
	
	
}
