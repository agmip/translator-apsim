package org.agmip.translators.apsim.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import org.agmip.translators.apsim.events.Event;
import org.agmip.translators.apsim.events.Planting;
import org.agmip.translators.apsim.events.Irrigation;
import org.agmip.translators.apsim.events.SetVariableEvent;
import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.joda.time.DateTime;
import org.joda.time.Days;

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
    private double plowpanDepth = Util.missingValue;
    public double getPlowpanDepth() { return plowpanDepth;}
    public void setPlowpanDepth(double depth) { plowpanDepth = depth; }
    
    @JsonIgnore
    private double percolationRate = Util.missingValue;
    public double getPercolationRate() { return percolationRate;}
    public void setPercolationRate(double pr) { percolationRate = pr; }
    
    @JsonIgnore
    private String paddyInitDate = "?";
    public String getPaddyInitDate() { return paddyInitDate;}
    public void setPaddyInitDate(String date) { paddyInitDate = date; }

//    @JsonIgnore
//    private double minFlood = Util.missingValue;
//    public double getMinFlood() { return minFlood;}
//    public void setMinFlood(double height) { minFlood = height; setPaddyApplied(true); }
//
//    @JsonIgnore
//    private double maxFlood = Util.missingValue;
//    public double getMaxFlood() { return maxFlood;}
//    public void setMaxFlood(double height) { maxFlood = height; setPaddyApplied(true); }

    @JsonIgnore
    private boolean isPaddyApplied = false;
    public boolean isPaddyApplied() { return isPaddyApplied;}
    public void setPaddyApplied(boolean isPaddyApplied) { this.isPaddyApplied = isPaddyApplied; }

    @JsonIgnore
    private boolean isAutoFloodApplied = false;
    public boolean isAutoFloodApplied() { return isAutoFloodApplied;}
    public void setAutoFloodApplied(boolean isAutoFloodApplied) { this.isAutoFloodApplied = isAutoFloodApplied; }
    
//    @JsonIgnore
//    public boolean isBundHeightOnly() { return getMaxFlood() != Util.missingValue && getPlowpanDepth() == Util.missingValue && getMinFlood() == Util.missingValue;}

    @JsonIgnore
    private List<BundEntry> bundEntries = new ArrayList<BundEntry>();
    public List<BundEntry> getBundEntries() { return bundEntries; }
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
    public void initialise(Soil soil) {

        // initialise all events.
        DateTime pdate = null;
        for (int i = 0; i < events.size(); i++) {
            events.get(i).initialise(this);
            log += events.get(i).getLog();
            if (events.get(i).getClass().getName().endsWith(".Planting")) {
                pdate = new DateTime(((Planting) events.get(i)).getEventDate());
            }
        }
        
        // Check if need to add variable modify event for KS
        if (plowpanDepth != Util.missingValue && percolationRate != Util.missingValue) {
            SoilLayer[] layers = soil.getLayers();
            String ksString = "";
            double plowpanDepthCm = plowpanDepth / 10.0;
            boolean findPlowLayer = false;
            for (int j = 0; j < layers.length; j++) {
                if (findPlowLayer || layers[j].getBottomDepth() < plowpanDepthCm) {
                    ksString += Double.toString(layers[j].getKsat()) + "  ";
                } else {
                    ksString += Double.toString(percolationRate) + "  ";
                    findPlowLayer = true;
                }
            }
            
            events.add(new SetVariableEvent(paddyInitDate,
                "Soil Water",
                "KS",
                ksString));
        }

        

        // sort the events into date order.
        Collections.sort(events, eventComparator);

        // initialize the wonderful bund stuff
        DateTime idate = null;
        BundEntry be = null;
        boolean runWithBund = false;
        for (Event event : events) {
            if (event instanceof Irrigation) {
//                if (runWithBund) {
                    Irrigation irr = (Irrigation) event;
                if (irr.isPaddy()) {
                    runWithBund = true;
                    if (irr.isAutoFlood()) {
                        isAutoFloodApplied = true;
                    }
                    
                    DateTime currDate = new DateTime(irr.getEventDate());
                    int dap = Days.daysBetween(pdate, currDate).getDays();
                    if (idate == null) {
                        idate = currDate;
                        be = new BundEntry(dap, irr);
                        bundEntries.add(be);
                    } else if (!idate.equals(currDate)) {
                        idate = currDate;
                        be = new BundEntry(dap, irr);
                        bundEntries.add(be);
                    } else {
                        if (be != null) be.merge(irr);
                    }
                }
//                }
            }
        }
//        if (runWithBund) {
//            bundEntries.add(be);
//        }
        this.isPaddyApplied = runWithBund;
    }

    public class BundEntry {
        private final int dap;
        private double bundHeight = Util.missingValue;
        private double minFlood = Util.missingValue;
//        private double maxFlood;

        public BundEntry(int dap, Irrigation irr) {
            this.dap = dap;
            merge(irr);
        }

        public final void merge(Irrigation irr) {
//            if("IR009".equals(irr.getMethod())) {
//                this.maxFlood = irr.getAmount();
//            }

            if("IR009".equals(irr.getMethod())) {
                this.bundHeight = irr.getAmount();
            }
            if("IR011".equals(irr.getMethod())) {
                this.minFlood = irr.getAmount();
            }
//            return this;
        }
        public int getDap() { return dap; }
        public double getBundHeight() { return bundHeight; }
        public double getMinFlood() { return minFlood; }
        public double getMaxFlood() { return bundHeight; }
        public boolean isAutoFlood() { return minFlood != Util.missingValue; }
    }
}
