package org.agmip.translators.apsim.events;

import java.util.Calendar;
import java.util.Date;

import org.agmip.ace.LookupCodes;
import org.agmip.translators.apsim.core.Management;
import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonProperty;

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
    @JsonProperty("cul_id")
    private String cultivarDef = "?";

    // cultivar
    @JsonProperty("plftn")
    private double ftn = Util.missingValue;
    
    // planting depth. units=mm
    @JsonProperty("pldp")
    private double depth = Util.missingValue;
    public double depth() { return depth; }
    
    // plants per hill
    @JsonProperty("plph")
    private double plantsPerHill = Util.missingValue;
    public double plantsPerHill() { return plantsPerHill; }

    // plants per hill
    @JsonProperty("page")
    private int ageOfTransplant = Integer.MAX_VALUE;
    public int ageOfTransplant() { return ageOfTransplant; }
    
    // row spacing. units=cm
    @JsonProperty("plrs")
    double rowSpacing;
    public double rowSpacingAsMM() {
        if (rowSpacing == Util.missingValue)
            return Util.missingValue;
        else
            return rowSpacing * 10.0;
    }
    
    // Plant population at planting. units=number/m2
    @JsonProperty("plpop")
    private double population = Util.missingValue;

    // Plant population at planting. units=number/m2
    @JsonProperty("plpoe")
    private double population2 = Util.missingValue;

    // Number of plants in seedbed. units=number
    @JsonProperty("nplsb")
    private double numPlantsInSeedbed = Util.missingValue;
    public double numPlantsInSeedbed() { return numPlantsInSeedbed; }
    
    
    // Plant material 
    // 	  S=Dry seed,
    //    T=Transplant,
    //    N=Nursery,
    //    P=Pregerminated seed,
    //    R=Ratoon,
    @JsonProperty("plma")
    private String plantMaterial = "S";
    
    // Crop Name
    public String getCropName() {
        return LookupCodes.lookupCode("crid", cropID, "apsim");
    }
    
    // Calculate number of hills
    private double numberOfHills() { 
    	return population / plantsPerHill;
    }
    
    @Override
    public String getApsimAction() {
    	String actionLine = getSowLine(getCropName());
    	actionLine = actionLine.replace("$cropName", getCropName());
 		actionLine = actionLine.replace("$population", String.valueOf(population));
    	actionLine = actionLine.replace("$depth", String.valueOf(depth));
        if ("?".equals(cultivar)) {
            actionLine = actionLine.replace("$cultivar",cultivarDef);
        } else {
            actionLine = actionLine.replace("$cultivar",cultivar);
        }
    	
    	actionLine = actionLine.replace("$row_spacing_m", String.valueOf(rowSpacingAsMM()/1000.0));
    	actionLine = actionLine.replace("$row_spacing", String.valueOf(rowSpacingAsMM()));
    	actionLine = actionLine.replace("$ftn", String.valueOf(ftn));
    	actionLine = actionLine.replace("$plantsPerHill", String.valueOf(plantsPerHill));
    	actionLine = actionLine.replace("$ageOfTransplant", String.valueOf(ageOfTransplant));
    	actionLine = actionLine.replace("$numberOfHills", String.valueOf(numberOfHills()));
    	actionLine = actionLine.replace("$numPlantsInSeedbed", String.valueOf(numPlantsInSeedbed()));
    	return actionLine;
    }

    @Override
    public void initialise(Management management) {
        if ("?".equals(getDate()))
            log += "  * Operation planting ERROR: Date missing. '?' has been inserted.\r\n";
        
        if ("?".equals(cropID))
            log += "  * Operation " + getDate() + " ERROR: Planting crop missing (crid).\r\n";

        if (population == Util.missingValue) {
        	if (population2 == Util.missingValue)
        		log += "  * Operation " + getDate() + " ERROR: Planting population missing (plpop, plpoe).\r\n";
        	else
        		population = population2;
        }
        
        if (depth == Util.missingValue)
            log += "  * Operation " + getDate() + " ERROR: Planting depth missing (pldp).\r\n";
      
        if ("?".equals(cultivar) && "?".equals(cultivarDef))
        	log += "  * Operation " + getDate() + " ERROR: Missing planting cultivar (apsim_cul_id and cul_id).\r\n";

        if (rowSpacing == Util.missingValue)
            log += "  * Operation " + getDate() + " ERROR: Planting row spacing missing (plrs).\r\n";

        if (numPlantsInSeedbed == Util.missingValue && getCropName().equals("rice") && !plantMaterial.equals("S"))
            log += "  * Operation " + getDate() + " ERROR: Number of plants in seedbed missing (nplsb).\r\n";
        
        if (getCropName().equals("sorghum") && ftn == Util.missingValue)
        	log += "  * Operation " + getDate() + " ERROR: Planting fertile tiller number not specified for sorghum (plftn). \r\n";
        
        if (getCropName().equals("rice") && plantMaterial.equals("T")) {
        	if (plantsPerHill == Util.missingValue)
        		log += "  * Operation " + getDate() + " ERROR: Rice planting variable 'plants per hill (plph) missing. \r\n";
        	if (ageOfTransplant == Integer.MAX_VALUE)
        		log += "  * Operation " + getDate() + " ERROR: Rice planting variable 'age of transplant (page) missing. \r\n";
        	else {
        		Calendar c = Calendar.getInstance(); 
        		c.setTime(getEventDate()); 
        		c.add(Calendar.DATE, -ageOfTransplant);
        		setDate(Util.apsim.format(c.getTime()));
        		
        	}
        		
        }
    }
    
    // Return a specific crop sow line.    
    private String getSowLine(String cropName) {
    	if (cropName.equals("maize"))
    		return "$cropName sow plants = $population, sowing_depth = $depth (mm), cultivar = $cultivar, row_spacing = $row_spacing (mm)";
    	else if (cropName.equals("sorghum"))
    		return "$cropName sow plants = $population, sowing_depth = $depth (mm), cultivar = $cultivar, row_spacing = $row_spacing (mm), skip = solid, tiller_no_fertile = $ftn";
    	else if (cropName.equals("sugar"))
    		return "$cropName sow plants = $population, cultivar = $cultivar, sowing_depth = $depth";
    	else if (cropName.equals("millet"))
    		return "$cropName sow plants = $population, sowing_depth = $depth (mm), cultivar = $cultivar, row_spacing = $row_spacing_m (m)";
    	else if (cropName.equals("rice")) {
    		if (plantMaterial.equals("S"))
    			return "$cropName sow cultivar = $cultivar, establishment = direct-seed, nplds = $population";
    		else
    			return "$cropName sow cultivar = $cultivar, establishment = transplant, nplsb = $numPlantsInSeedbed, nplh  = $plantsPerHill, sbdur = $ageOfTransplant, nh = $numberOfHills";
    	}
    	else if (cropName.equals("cotton"))
    		return "$cropName sow plants_pm = $population, cultivar = $cultivar, sowing_depth = $depth (mm), row_spacing = $row_spacing (mm), skiprow = 1";
    	else
    		// Plant based crops
    		return "$cropName sow plants = $population, sowing_depth = $depth (mm), cultivar = $cultivar, row_spacing = $row_spacing (mm), crop_class = plant";
    }
    
  
        
}
