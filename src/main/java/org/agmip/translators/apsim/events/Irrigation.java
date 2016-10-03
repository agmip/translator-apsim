package org.agmip.translators.apsim.events;

import org.agmip.translators.apsim.core.Management;
import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @since Jul 13, 2012
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Irrigation extends Event {

    @JsonProperty("irop")
    private String method = "?";
    public String getMethod() { return method; }

    @JsonProperty("irval")
    private double amount = Util.missingValue;
    public double getAmount() { return amount; }

    @JsonProperty("abund")
    private double bundHeight = Util.missingValue;

    @JsonIgnore
    private boolean usePaddy = false;
    private boolean isAutoFlood = false;
    public boolean isPaddy() { return usePaddy; }
    public boolean isAutoFlood() { return isAutoFlood; }

    @Override
    public String getApsimAction() {
        if (isPaddy()) {
            return null;
        } else {
            return "irrigation apply amount = " +amount+ " (mm) " ;
        }
    }

    @Override
    public void initialise(Management management) {
        if ("?".equals(getDate()))
            log += "  * Operation irrigation ERROR: Date missing (date).\r\n";

        if (amount == Util.missingValue)
            log += "  * Operation " + getDate() + " ERROR: Irrigation amount missing (irval).\r\n";

        if (("IR008").equals(method)) {
            // KS
            usePaddy = true;
            if (management.getPercolationRate()== Util.missingValue) {
                management.setPercolationRate(amount);
                management.setPaddyInitDate(getDate());
            }
        } else if (("IR010").equals(method)) {
            // puddling, plowpan depth
            usePaddy = true;
            bundHeight = amount;
            if (bundHeight != Util.missingValue) {
                management.setPlowpanDepth(bundHeight);
                management.setPaddyInitDate(getDate());
            }
        } else if (("IR009").equals(method)) {
            // Max flood height
            usePaddy = true;
//            isPaddyEntry = true;
//            if (management.getMaxFlood() == Util.missingValue) {
//                management.setMaxFlood(amount);
//            }
        } else if (("IR011").equals(method)) {
            // Min flood height
            usePaddy = true;
            isAutoFlood = true;
//            if (management.getMinFlood() == Util.missingValue) {
//                management.setMinFlood(amount);
//            }
        }


    }

}
