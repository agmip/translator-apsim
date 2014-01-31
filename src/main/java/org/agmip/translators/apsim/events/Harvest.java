package org.agmip.translators.apsim.events;

import org.agmip.translators.apsim.core.Management;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @since Jul 13, 2012
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Harvest extends Event {

    // APSIM currently does auto harvesting - this is not used.
    @Override
    public String getApsimAction() {
            return null;
    }

    
    
    // initialise this instance.
    @Override
    public void initialise(Management management) {
    }


}
