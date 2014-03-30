package org.agmip.translators.apsim.core;

import org.agmip.translators.apsim.util.DateDeserializer;
import org.agmip.translators.apsim.util.DateSerializer;
import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Dean Holzworth, CSIRO
 * @author Ioannis N. Athanasiadis, DUTh
 * @since Jul 13, 2012
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Simulation {
    
	// Initial condition
    @JsonProperty("initial_conditions")
    private InitialCondition initialCondition;
    public InitialCondition getInitialCondition() { return initialCondition; }
    
    // linked weather
    @JsonIgnore
    private Weather weather;
    public Weather getWeather() { return weather; }
    public void setWeather(Weather w) { weather = w; }
    
    // id of linked weather
    @JsonProperty("wst_id")
    private String weatherID;
    public String getWeatherID(){return weatherID;}
    public void setWeatherID(String weatherID) {
  		this.weatherID = weatherID;
  	}
  	
    // management.
    private Management management;
    public Management getManagement() { return management; }
    public void setManagement(Management management) {
  		this.management = management;
  	}
  	
    // linked soil.
    @JsonIgnore
    private Soil soil;
    public Soil getSoil() { return soil; }
    public void setSoil(Soil s) { soil = s;}
    
    // id of linked soil
    @JsonProperty("soil_id")
    private String soilID;
    public String getSoilID(){return soilID;}
    public void setSoilID(String soilID) {
  		this.soilID = soilID;
  	}
  	
    // experimentName
    @JsonProperty("exname")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    private String experimentName = "default";
    public String getExperimentName() { return experimentName; }
    public void setExperimentName(String experimentName) {
  		this.experimentName = experimentName;
  	}
  	
    // treatmentName
    @JsonProperty("trt_name")
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    private String treatmentName = "";
    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) {
  		this.treatmentName = treatmentName;
  	}
  	
    // startDate
    @JsonSerialize(using=DateSerializer.class)
    @JsonDeserialize(using=DateDeserializer.class)
    @JsonProperty("sdat")
    private String startDate = "?";
    public String getStartDate() { return startDate; }

    // endDate
    @JsonSerialize(using=DateSerializer.class)
    @JsonDeserialize(using=DateDeserializer.class)
    @JsonProperty("endat")
    private String endDate = "?";
    public String getEndDate() { return endDate; }
    
    // return a unique simulation name.
    public String getUniqueName() {
    	String Name;
        if ("".equals(treatmentName))
            Name = experimentName;
        else
            Name = experimentName + "-" + treatmentName;
//        Name = Name.replace("/", "_");
//        Name = Name.replace("\\", "_");
        Name = Name.replaceAll("[/\\\\*|?:<>\"]", "_");
        return Name;
    }
    
    // latitude
    @JsonProperty("fl_lat")
    private double latitude = Util.missingValue;
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) {
  		this.latitude = latitude;
  	}
  	
    // longitude
    @JsonProperty("fl_long")
    private double longitude = Util.missingValue;
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) {
  		this.longitude = longitude;
  	}
  	
    // log
    @JsonIgnore
    private String log;
    public String getLog() { return log; }
    


   
    
    // Default constructor - Needed for Jackson
    public Simulation() {}
    
    
    
    // initialise the SimulationRun instance.
    public void initialise() throws Exception {
        log = "";
        
        // Check the start and end date.
        if ("?".equals(startDate))
           log += "  * Clock ERROR: Missing a simulation start date (sdat).\r\n";
        if ("?".equals(endDate))
            log += "  * Clock ERROR: Missing a simulation end date (endat).\r\n";

        // Check the weather.
        if (weather == null)
            log += "  * Met ERROR: Missing weather data.\r\n";
        else {
        	if (weather.getLatitude() == Util.missingValue)
        		log += "  * Met ERROR: No latitude found in weather data (wst_lat).\r\n";
        	if (weather.getTav() == Util.missingValue)
        		log += "  * Met ERROR: No TAV found in weather data (tav).\r\n";
        	if (weather.getAmp() == Util.missingValue)
        		log += "  * Met ERROR: No AMP found in weather data (tamp).\r\n";
        	if (weather.getCo2() == Util.missingValue)
        		log += "  * Met WARNING: No CO2 found in weather data (aco2 or co2y).\r\n";
        }
            
        // Check the soil.
        if (soil == null)
            log += "  * Soil: No soil found in AgMIP dataset.\r\n";
        else
            log += soil.getLog();
        
        // Check for initial conditions.
        if (initialCondition == null)
            log += "  * Initial conditions ERROR: Missing all initial conditions (NO3, NH4, SW, SurfaceOM.residueWeight)\r\n";
        else if (soil != null) {
            initialCondition.initialise(soil.getLayers());
            log += initialCondition.getLog();
        }
        
        // Check for management events.
        if (management == null)
            log += "  * Operations ERROR: Missing all management events.\r\n";
        else {
            management.initialise();
            log += management.getLog();
        }
    }
    
    
  
}
