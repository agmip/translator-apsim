package org.agmip.translators.apsim.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.agmip.translators.apsim.events.Event;
import org.agmip.translators.apsim.events.Planting;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author Dean Holzworth, CSIRO
 * @author Ioannis N. Athanasiadis, DUTh
 * @since Jul 13, 2012
 */
 
@JsonIgnoreProperties(ignoreUnknown = true)
public class Management {

    // log
    private String log = "";
    public String getLog() { return log; }
    
    // events
    private List<Event> events;
    public List<Event> getEvents() { return events; }

    // return the crop being planted
    public String plantingCropName() {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getClass().getName().endsWith(".Planting")) {
                Planting planting = (Planting) events.get(i);
                return planting.getCropName();
            }
        }
        return null;
    }
    // Needed for Jackson
    public Management() {}
    // initialise this instance
    public void initialise() {
        log = "";
        // initialise all events.
        for (int i = 0; i < events.size(); i++) {
            events.get(i).initialise();
            log += events.get(i).getLog();
        }
        Comparator<Event> eventComparator = new Comparator<Event>() {
            @Override
            public int compare(Event eventA, Event eventB)  {
                if (eventA.getEventDate() == null)
                    return -1;
                if (eventB.getEventDate() == null)
                    return 1;
                return eventA.getEventDate().compareTo(eventB.getEventDate());
            }
        };
        // sort the events into date order.
        Collections.sort(events, eventComparator);
    }
}
