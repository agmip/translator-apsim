package org.agmip.translators.apsim.events;

import org.agmip.translators.apsim.core.Management;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @since Jul 13, 2012
 */

public class Chemical extends Event {

    @Override
    public String getApsimAction() {
        return null;
    }

    @Override
    public void initialise(Management management) {
        
    }

}
