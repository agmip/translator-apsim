package org.agmip.translators.apsim.core;

import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Dean Holzworth, CSIRO
 * @author Ioannis N. Athanasiadis, DUTh
 * @since Jul 13, 2012
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Soil {
    
    // classification
    @JsonProperty("classification")
    private String classification = "?" ;
    public String getClassification() { return classification; }
	public void setClassification(String classification) {
		this.classification = classification;
	}
	
    // site
    @JsonProperty("soil_site")
    private String site = "?";
    public String getSite() { return site; }
	public void setSite(String site) {
		this.site = site;
	}
	
    // name
    @JsonProperty("soil_name")
    private String name = "?";
    public String getName() {
        if ("?".equals(name))
            return id;
        else
            return name;
    }
	public void setName(String name) {
		this.name = name;
	}
	
    // id
    @JsonProperty("soil_id")
    private String id = "?";    
    public String getId() { return id; }
	public void setId(String id) {
		this.id = id;
	}
	
    // source
    @JsonProperty("sl_source")
    private String source = "?";
    public String getSource() { return source; }
	public void setSource(String source) {
		this.source = source;
	}
	
    // latitude
    @JsonProperty("soil_lat")
    private double latitude = Util.missingValue;
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

    // longitude
    @JsonProperty("soil_long")
    private double longitude = Util.missingValue;
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

    // u
    @JsonProperty("slu1")
    private double u = Util.missingValue;
    public double getU() { return u; }
	public void setU(double u) {
		this.u = u;
	}

    // SummerU
    @JsonProperty("summeru")
    private double sumU = Util.missingValue;
    public double getSumU() {
        if (u == Util.missingValue) {
            return sumU;
        } else {
            return u;
        }
    }
    public void setSumU(double u) {
            this.sumU = u;
    }

    // WinterU
    @JsonProperty("winteru")
    private double winU = Util.missingValue;
    public double getWinU() {
        if (u == Util.missingValue) {
            return winU;
        } else {
            return u;
        }
    }
    public void setWinU(double u) {
            this.winU = u;
    }

    // CONA
    @JsonProperty("cona")
    private double cona = Util.missingValue;
    public double getCona() {
        if (cona == Util.missingValue) {
            return 3.5;
        } else {
            return cona;
        }
    }
    public void setCona(double cona) {
            this.cona = cona;
    }

    // salb
    @JsonProperty("salb")
    private double salb = Util.missingValue;
    public double getSalb() { return salb; }
    public void setSalb(double salb) {
		this.salb = salb;
	}

    // cn2bare
    @JsonProperty("slro")
    private double cn2bare = Util.missingValue;
    public double getCn2bare() { return cn2bare; }
    public void setCn2bare(double cn2bare) {
		this.cn2bare = cn2bare;
	}
    
    // swcon - is a whole of profile number in AgMIP - layered in APSIM.
    @JsonProperty("sldr")
    private double swcon = Util.missingValue;
    public double getSwcon() { return swcon; }
    public void setSwcon(double swcon) {
		this.swcon = swcon;
	}    

    // diffusConst
    @JsonProperty("diffusconst")
    public double diffusConst = Util.missingValue;
    public double getDiffusConst() { return diffusConst; }
    public void setDiffusConst(double diffusConst) {
		this.diffusConst = diffusConst;
	}

    // diffusSlope
    @JsonProperty("diffusslope")
    public double diffusSlope = Util.missingValue;
    public double getDiffusSlope() { return diffusSlope; }
    public void setDiffusSlope(double diffusSlope) {
		this.diffusSlope = diffusSlope;
	}

    // cropName
    public String cropName = "";
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) {
		this.cropName = cropName;
	}
    
    // layers
    @JsonProperty("soilLayer")
    private SoilLayer[] layers;
    public SoilLayer[] getLayers() { return layers; }
    public void setLayers(SoilLayer[] layers) {
		this.layers = layers;
	}

    @JsonIgnore
    private String log;
    public String getLog() { return log; }
    
    
    
    // Needed for Jackson
    public Soil() {}
    
    
    
    // Initialise the instance.
    public void initialise() throws Exception {
        log = "";
        
        if (layers.length == 0)
           log += "  * Soil ERROR: No soil layers found\r\n";
        else {
            // calculate a thickness for each layer.
            double cumThickness = 0.0;
            for (int i = 0; i < layers.length; i++) {
                cumThickness = layers[i].initialise(cumThickness, i+1, layers.length);
            } 
            
           
            for (int i = 0; i < layers.length; i++) {
                log += layers[i].getLog();
            }   
        }

        if (id.equals("?"))
        	log += "  * Soil ERROR: Missing soil ID (soil_id).\r\n";
        
        if (u == Util.missingValue)
            log += "  * Soil ERROR: Missing U (slu1).\r\n";
        
        if (salb == Util.missingValue)
            log += "  * Soil ERROR: Missing SALB (salb).\r\n";

        if (cn2bare == Util.missingValue)
            log += "  * Soil ERROR: Missing CN2Bare (slro).\r\n";
               
        if (diffusConst == Util.missingValue)
            log += "  * Soil ERROR: Missing diffusConst (diffusConst).\r\n";        

        if (diffusSlope == Util.missingValue)
            log += "  * Soil ERROR: Missing diffusSlope (diffusSlope).\r\n";        

        if (swcon == Util.missingValue)
            log += "  * Soil ERROR: Missing SWCON (sldr).\r\n";
        
    }
   
      
}
