package org.agmip.translators.apsim.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.agmip.translators.apsim.events.Event;
import org.agmip.translators.apsim.events.Irrigation;
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

    // scripts
    private ArrayList<String> scripts;
    public ArrayList<String> getScripts() { return scripts; }
    
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
    public Management() {
    	scripts = new ArrayList<String>();
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
    
    // initialise this instance
    public void initialise(Soil soil) {
    	
        // Special handling for irrigation with operation code of IR008, IR009 and IR010
        int size = events.size();
        for (int i = 0; i < size; i++) {
            if (events.get(i) instanceof Irrigation) {
                Irrigation ir = (Irrigation) events.get(i);
                if (("IR008").equals(ir.getMethod())) {
                	addSetKSEvent(soil, i, ir);
                } else if (("IR009").equals(ir.getMethod())) {
                    addSetMaxPondEvent(ir);
                } else if (("IR011").equals(ir.getMethod())) {
                	addMinimumIrrigationEvent(ir);                    
                } else if (("IR010").equals(ir.getMethod())) {
                    // Might add another SetVariableEvent for plow-pan depth
                }
            }
        }

        // skip meaningless events
        for (int i = events.size() - 1; i > -1; i--) {
            if (events.get(i) instanceof Irrigation) {
                Irrigation ir = (Irrigation) events.get(i);
                if (("IR008").equals(ir.getMethod())
                        || ("IR009").equals(ir.getMethod())
                        || ("IR010").equals(ir.getMethod())) {
                    events.remove(i);
                }
            }
        }

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

	private void addSetMaxPondEvent(Irrigation ir) {
		events.add(new SetVariableEvent(ir.getDate(),
		        "Soil Water",
		        "max_pond",
		        String.valueOf(ir.getAmount())));
		bundHeight = ir.getAmount();
	}

	private void addSetKSEvent(Soil soil, int i, Irrigation ir) {
		// Get an array of KS values.
		double[] KS = soil.getKS();
		
		if (KS.length == 0)
			log += events.get(i).getLog();
		else {
			// Set the KS in layer 2.
			KS[1] = ir.getAmount();
			
			// Convert the KS array into a string.
			String ksString = Double.toString(KS[0]);
			for (int j = 1; j < KS.length; j++) {
				ksString += "  " + Double.toString(KS[j]);
			}
			
		    events.add(new SetVariableEvent(ir.getDate(),
		            "Soil Water",
		            "KS",
		            ksString));
		}
	}
	
	private void addMinimumIrrigationEvent(Irrigation ir) {
		// minimum irrigation level.
	    events.add(new SetVariableEvent(ir.getDate(),
	            "AutoIrrigator",
	            "MinPond",
	            String.valueOf(ir.getAmount())));
	}
	
}
