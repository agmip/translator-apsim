package org.agmip.translators.apsim.events;

import org.agmip.translators.apsim.core.Management;
import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @since Jul 13, 2012
 */

public class OrganicMatter extends Event {

    @JsonProperty("omamt")
    private double amount = Util.missingValue;
    
    @JsonProperty("omdep")
    private double depth = Util.missingValue;
    
    @JsonProperty("omc%")
    private double carbon = Util.missingValue;

    @JsonProperty("omn%")
    private double nitrogen = Util.missingValue;

    @JsonProperty("omp%")
    private double phosphorus = Util.missingValue;

    @JsonProperty("omc2n")
    private double cnr = Util.missingValue;
    
    @Override
    public String getApsimAction() {
        double cpr = Util.missingValue;
        
        String Action = "SurfaceOrganicMatter add_surfaceom " +
                        "type = manure, " +
        		        "name = manure, " +
                        "mass = " + amount + " (kg/ha), " +
                        "depth = " + depth + " (mm)";
               
        if (cnr != Util.missingValue)
        	Action += ", cnr = " + String.valueOf(cnr);
        
        else if (amount != Util.missingValue) {
            if (carbon != Util.missingValue) {
                if (nitrogen != Util.missingValue) {
                    double amountCarbon = Double.valueOf(carbon) / 100.0 * Double.valueOf(amount);
                    double amountNitrogen = Double.valueOf(nitrogen) / 100.0 * Double.valueOf(amount);
                    if (amountNitrogen == 0.0)
                        cnr = 0;
                    else
                        cnr = amountCarbon / amountNitrogen * 100.0;
                }
                Action += ", cnr = " + cnr;
                if (phosphorus != Util.missingValue) {
                    double amountCarbon = Double.valueOf(carbon) / 100.0 * Double.valueOf(amount);
                    double amountPhosphorus = Double.valueOf(phosphorus) / 100.0 * Double.valueOf(amount);
                    if (amountPhosphorus == 0.0)
                        cpr = 0;
                    else
                        cpr = amountCarbon / amountPhosphorus * 100.0;
                    Action += ", cpr = " + cpr;
                }
            }
        }
        return Action;
    }

    @Override
    public void initialise(Management management) {
        if ("?".equals(getDate()))
            log += "  * Operation fertiliser ERROR: Date missing. '?' has been inserted\r\n";
        
        if (amount == Util.missingValue)
            log += "  * Operation " + getDate() + " ERROR: Organic matter application missing amount (omamt).\r\n";
        if (depth == Util.missingValue)
            log += "  * Operation " + getDate() + " ERROR: Organic matter application missing depth (omdep).\r\n";
        if (cnr == Util.missingValue) {
        	if (carbon == Util.missingValue)
	            log += "  * Operation " + getDate() + " ERROR: Organic matter application missing carbon percent (omc%).\r\n";
	        if (nitrogen == Util.missingValue)
	            log += "  * Operation " + getDate() + " ERROR: Organic matter application missing nitrogen percent (omn%).\r\n";
        }
    }

}
