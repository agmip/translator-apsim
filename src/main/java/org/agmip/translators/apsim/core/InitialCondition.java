package org.agmip.translators.apsim.core;

import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.agmip.ace.LookupCodes;

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
    
    // residueWeight
    @JsonProperty("icrag")
    private String residueWeight = "?";
    public String getResidueWeight() { return residueWeight; }
    
    // residueNConc
    @JsonProperty("icrn")
    private String residueNConc = "?";
    public String getResidueNConc() { return residueNConc; }
    
 
    
    // cropCode
    @JsonProperty("icpcr")
    private String cropCode = "?";

    // residueType
    public String getResidueType() { 
        return LookupCodes.lookupCode("crid", cropCode, "common");
        //return cropCode;
    } //Util.cropCodeToName(cropCode); }
    
    // soilLayers
    @JsonProperty("soilLayer")
    private InitialConditionLayer[] soilLayers = new InitialConditionLayer[0];
    public InitialConditionLayer[] getSoilLayers() { return soilLayers; }

    @JsonIgnore
    private String log = "";
    public String getLog() { return log; }
    
    // cnr
    private String cnr = "0";
    public String getCnr() {return cnr;}
    
    // Needed for Jackson
    public InitialCondition() {}
    
    
    
    // Initialise this instance.
    public void initialise(SoilLayer[] layersFromSoil) {
        double cumThickness = 0.0;
        if (soilLayers.length == 0) {
            log = "  * Initial conditions ERROR: Missing NO3, NH4 and SW. An empty table has been set up under soil with '-99' entries \r\n";
            soilLayers = new InitialConditionLayer[layersFromSoil.length];
            for (int i = 0; i < soilLayers.length; i++) {
                soilLayers[i] = new InitialConditionLayer(layersFromSoil[i].getThickness());
            }
        }
        else {
            boolean missingFound = false;
            for (int i = 0; i < soilLayers.length; i++) {
                cumThickness = soilLayers[i].initialise(cumThickness);
                if ("-99".equals(soilLayers[i].getNo3()) ||
                    "-99".equals(soilLayers[i].getNh4()) ||
                    "-99".equals(soilLayers[i].getSoilWater()))
                    missingFound = true;
            } 
            if (missingFound)
                log = "  * Initial conditions ERROR: Some missing values were found. Please check the table initial conditions table under the soil\r\n";
        }
        
        if ("?".equals(getResidueType()))
            log += "  * SurfaceOrganicMatter ERROR: Missing residue type.\r\n";
        
        if ("?".equals(residueWeight))
            log += "  * SurfaceOrganicMatter ERROR: Missing residue weight.\r\n";
        else {
            if ("?".equals(residueNConc))
                log += "  * SurfaceOrganicMatter ERROR: Missing residue nitrogen concentration. Cannot calculate CNR.\r\n";
            
            else {
                double carbon = 0.4 * Double.valueOf(residueWeight);
                double nitrogen = Double.valueOf(residueNConc) / 100.0 * Double.valueOf(residueWeight);
                if (nitrogen == 0)
                    log += "  * SurfaceOrganicMatter ERROR: Residue nitrogen concentration = 0.\r\n";
                else
                    cnr = String.valueOf(carbon / nitrogen);
                }
                
            }
        }

	public void setDate(String date) {
		this.date = date;
	}

	public void setResidueWeight(String residueWeight) {
		this.residueWeight = residueWeight;
	}

	public void setResidueNConc(String residueNConc) {
		this.residueNConc = residueNConc;
	}

	public void setCropCode(String cropCode) {
		this.cropCode = cropCode;
	}

	public void setSoilLayers(InitialConditionLayer[] soilLayers) {
		this.soilLayers = soilLayers;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public void setCnr(String cnr) {
		this.cnr = cnr;
	}
         
    
    
    }


