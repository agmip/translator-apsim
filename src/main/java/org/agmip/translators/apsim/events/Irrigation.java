package org.agmip.translators.apsim.events;

import org.agmip.translators.apsim.core.Management;
import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @since Jul 13, 2012
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Irrigation extends Event {
	
    //@JsonProperty("irop")
    //private String method = "?";

    @JsonProperty("irval")
    private double amount = Util.missingValue;

    @JsonProperty("abund")
    private double bundHeight = Util.missingValue;
    
    //@JsonProperty("ireff")
    //private String efficiency = "?";

    @Override
    public String getApsimAction() {
        return "irrigation apply amount = " +amount+ " (mm) " ;
    }

    
    
    
    @Override
    public void initialise(Management management) {
        if ("?".equals(getDate()))
            log += "  * Operation irrigation ERROR: Date missing (date).\r\n";
        
        if (amount == Util.missingValue)
            log += "  * Operation " + getDate() + " ERROR: Irrigation amount missing (irval).\r\n";
        
        if (bundHeight != Util.missingValue)
        	management.setBundHeight(bundHeight);
    }

}
