package org.agmip.translators.apsim.core;

import org.agmip.ace.LookupCodes;
import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Dean Holzworth, CSIRO
 * @author Ioannis N. Athanasiadis, DUTh
 * @since Jul 13, 2012
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitialCondition {
    
    // date
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
    @JsonProperty("icdat")
    private String date = "?";
    public String getDate() { return date; }
	public void setDate(String date) {
		this.date = date;
	}

    // residueWeight
    @JsonProperty("icrag")
    private double residueWeight = Util.missingValue;
    public double getResidueWeight() { return residueWeight; }
    public void setResidueWeight(double residueWeight) {
		this.residueWeight = residueWeight;
	}

    // residueNConc
    @JsonProperty("icrn")
    private double residueNConc = Util.missingValue;
    public double getResidueNConc() { return residueNConc; }
    public void setResidueNConc(double residueNConc) {
		this.residueNConc = residueNConc;
	}

    // cropCode
    @JsonProperty("icpcr")
    private String cropCode = "?";
    public void setCropCode(String cropCode) {
		this.cropCode = cropCode;
	}

    // residueType
    public String getResidueType() { 
        return LookupCodes.lookupCode("crid", cropCode, "apsim");
    }
    
    // soilLayers
    @JsonProperty("soilLayer")
    private InitialConditionLayer[] soilLayers = new InitialConditionLayer[0];
    public InitialConditionLayer[] getSoilLayers() { return soilLayers; }
    public void setSoilLayers(InitialConditionLayer[] soilLayers) {
		this.soilLayers = soilLayers;
	}

    // cnr
    private double cnr = Util.missingValue;
    public double getCnr() {return cnr;}
    public void setCnr(double cnr) {
		this.cnr = cnr;
	}

    @JsonIgnore
    private String log = "";
    public String getLog() { return log; }
    

    
    
    // Needed for Jackson
    public InitialCondition() {}
    
    
    
    
    // Initialise this instance.
    public void initialise(SoilLayer[] layersFromSoil) {

    	if (soilLayers.length == 0)
            log = "  * Initial conditions ERROR: No layered information in initial conditions.\r\n";
        
        else {
            double cumThickness = 0.0;
            for (int i = 0; i < soilLayers.length; i++)
                cumThickness = soilLayers[i].initialise(cumThickness, i+1);
            
            for (int i = 0; i < soilLayers.length; i++)
                log += soilLayers[i].getLog();
        }
        
    	
        if ("?".equals(getResidueType()))
            log += "  * SurfaceOrganicMatter ERROR: Missing residue type (icpcr).\r\n";
        
        if (residueWeight == Util.missingValue)
            log += "  * SurfaceOrganicMatter ERROR: Missing residue weight (icrag).\r\n";
        else {
            if (residueNConc == Util.missingValue)
                log += "  * SurfaceOrganicMatter ERROR: Missing residue nitrogen concentration (icrn). Cannot calculate CNR.\r\n";
            
            else {
                double carbon = 0.4 * residueWeight;
                double nitrogen = residueNConc / 100.0 * residueWeight;
                if (nitrogen == 0)
                    log += "  * SurfaceOrganicMatter ERROR: Residue nitrogen concentration = 0.\r\n";
                else
                    cnr = carbon / nitrogen;
                }
                
            }
        }


	
	
	
	
	
	         
    
    
    }


