package org.agmip.translators.apsim.events;

import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonProperty;
import org.agmip.ace.LookupCodes;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @since Jul 13, 2012
 */

public class Planting extends Event{

    // cropID
    @JsonProperty("crid")
    private String cropID = "?";

    // cultivar
    @JsonProperty("apsim_cul_id")
    private String cultivar = "?";

    // cultivar
    @JsonProperty("cul_id")
    private String culId = "?";
    
    // planting depth. units=cm.
    @JsonProperty("pldp")
    private String depth = "?";
    public String depthAsMM() {
        if ("?".equals(depth))
            return "?";
        else
            return String.valueOf(Double.valueOf(depth));
    }

    // row spacing. units=cm
    @JsonProperty("plrs")
    String rowSpacing;
    public String rowSpacingAsMM() {
        if ("?".equals(rowSpacing))
            return "?";
        else
            return String.valueOf(Double.valueOf(rowSpacing) * 10.0);
    }
    
    // Plant population at planting. units=number/m2
    @JsonProperty("plpop")
    private String population = "?";

    // Crop Name
    public String getCropName() {
        return LookupCodes.lookupCode("crid", cropID, "common");
        //return Util.cropCodeToName(cropID);
    }
    
    @Override
    public String getApsimAction() {
        return getCropName()+" sow plants = " + population + ", sowing_depth = " + depthAsMM() + ", cultivar = " + cultivar + ", row_spacing = " + rowSpacingAsMM() + ", crop_class = plant";
    }

    @Override
    public void initialise() {
        if ("?".equals(getDate()))
            log += "  * Operation planting ERROR: Date missing. '?' has been inserted\r\n";
        
        if ("?".equals(cropID))
            log += "  * Operation " + getDate() + " ERROR: Planting crop missing. A '?' has been inserted\r\n";
        
        if ("?".equals(depth)) {
            depth = "50";
            log += "  * Operation " + getDate() + " ASSUMPTION: Planting depth missing. A depth of 50mm has been assumed.\r\n";
        }
      
        if ("?".equals(cultivar)) {
        	log += "  * Operation " + getDate() + " ERROR: AgMIP planting cultivars don't match the APSIM cultivars. Please check the sowing operation. AgMIP cultivar is: " + cultivar + "\r\n";
            if ("?".equals(culId))
                log += "  * Operation " + getDate() + " ERROR: Missing planting cultivar.\r\n";
            else
                cultivar = culId;
        }
            

        if ("?".equals(rowSpacing)) {
            rowSpacing = "500";
            log += "  * Operation " + getDate() + " ASSUMPTION: Planting row spacing missing. A row spacing of 500 has been assumed\r\n";
        }

    }
        
}
