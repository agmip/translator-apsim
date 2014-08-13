package org.agmip.translators.apsim.events;

import org.agmip.translators.apsim.core.Management;

/**
 * @author Meng Zhang
 * @since Aug 13, 2014
 */
public class AutoIrrigation extends Event {

    @Override
    String getApsimAction() {
        return null;
    }

    @Override
    public void initialise(Management management) {
        
    }
    
}
