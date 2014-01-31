package org.agmip.translators.apsim.events;

import org.agmip.translators.apsim.core.Management;
import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @since Jul 13, 2012
 */

public class Tillage extends Event {

    // depth
    @JsonProperty("tidep")
    double depth = Util.missingValue;
    
    // implementCode
    @JsonProperty("tiimp")
    String implementCode = "?";
    
    // implementName
    private String getImplementName() {
        if ("?".equals(implementCode))
            return "?";
        else
            return Util.tillageCodeToName(implementCode); 
    }
    
    // apsimAction
    @Override
    public String getApsimAction() {
        return "SurfaceOrganicMatter tillage type=" + getImplementName() + ", f_incorp=0, tillage_depth=" + depth;
    }
    
    
    

    // Initialise this instance.
    @Override
    public void initialise(Management management) {
        if ("?".equals(getDate()))
            log += "  * Operation tillage ERROR: Date missing (date).r\n";
        
        if (depth == Util.missingValue)
            log += "  * Operation " + getDate() + " ERROR: Tillage depth missing (tidep).\r\n";
        
        if ("?".equals(implementCode))
            log += "  * Operation " + getDate() + " ERROR: Tillage implement missing (tiimp).\r\n";
        else
            log += "  * Operation " + getDate() + " ASSUMPTION: Tillage type: " + getImplementName() + " may not match an APSIM tillage type\r\n";
    }
    
}
