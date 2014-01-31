package org.agmip.translators.apsim.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.agmip.translators.apsim.events.Event;
import org.agmip.translators.apsim.events.Planting;
import org.agmip.translators.apsim.events.SetVariableEvent;
import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonIgnore;
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
    
    // bund height - I think this property should be in management not in
    // individual irrigation applications. Units: mm
    @JsonIgnore
    private double bundHeight = Util.missingValue;
    public double getBundHeight() { return bundHeight;}
    public void setBundHeight(double height) { bundHeight = height; }
    
    
    
    
    // default constructor - needed for Jackson
    public Management() {}
    
    
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
    
    // initialise this instance
    public void initialise() {
    	
        // initialise all events.
        for (int i = 0; i < events.size(); i++) {
            events.get(i).initialise(this);
            log += events.get(i).getLog();
        }
        
        // Look for bund height. If found then add it as an event.
        if (events.size() >= 1 && bundHeight != Util.missingValue) {
        	Collections.sort(events, eventComparator);
        	events.add(new SetVariableEvent(events.get(0).getDate(), 
        			                        "Soil Water",
        			                        "max_pond",
        			                        String.valueOf(bundHeight)));
        }
        
        

        
        // sort the events into date order.
        Collections.sort(events, eventComparator);
    }
}
