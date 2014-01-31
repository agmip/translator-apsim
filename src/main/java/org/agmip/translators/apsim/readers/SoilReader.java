package org.agmip.translators.apsim.readers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.agmip.translators.apsim.core.Soil;
import org.agmip.translators.apsim.core.SoilLayer;

import com.ximpleware.AutoPilot;

/**
 * Helper class to parse an APSIM XML file and extract all unique soils.
 * 
 * @author Ioannis N. Athanasiadis
 */
public class SoilReader extends VTDReader{
	public SoilReader(String file) {
		super(file,"(//soil[not(@shortcut)])");
	}
	
	public SoilReader(String file, String path){
		super(file, path);
	}


	@SuppressWarnings("unchecked")
	public List<Soil> read() throws Exception{
		AutoPilot a = xpath("");
		int count = 1;
		List<Soil> soils = new ArrayList<Soil>();
		Set<String> names = new HashSet<String>();

		while (a.evalXPath() != -1) {
			Soil s = read("["+count+"]");
			String name = s.getName();
			name =  names.contains(name)? name+count : name;
			names.add(name);
			s.setName(name);			
			soils.add(s);
		    count++;
		}
		return soils;
	}
	
	
	@SuppressWarnings("unchecked")
	public Soil read(String path) throws Exception{
		Soil soil = new Soil();
	    
		soil.setCn2bare(xPathDouble(path+"/SoilWat/Cn2Bare"));
		soil.setDiffusConst(xPathDouble(path+"/SoilWat/DiffusConst"));
		soil.setDiffusSlope(xPathDouble(path+"/SoilWat/DiffusSlope"));
		soil.setId(xPathText(path+"/@name"));
		soil.setLatitude(xPathDouble(path+"/Latitude"));
		soil.setLongitude(xPathDouble(path+"/Longitude"));
		soil.setName(xPathText(path+"/@name"));
		soil.setSalb(xPathDouble(path+"/SoilWat/Salb")); 
		soil.setSite(xPathText(path+"/Site"));
		soil.setSource(xPathText(path+"/DataSource"));
		soil.setU(Double.parseDouble(xPathText(path+"/SoilWat/SummerU")));
		
//		TODO: Deal with soil classification.
//		String[] soilClass = get(prefix+"/Comment[1]").split(":");			
//		soil.setClassification(soilClass.length==2?soilClass[1].trim():soilClass[0].trim());

		int soilLayers = count(path+"/Water/Layer");
		 

		SoilLayer[] layers = new SoilLayer[soilLayers]; 
		soil.setLayers(layers);
		for(int l=1; l<=soilLayers; l++) {
			SoilLayer layer = new SoilLayer();
			layer.setBulkDensity(xPathDouble(path+"/Water/Layer["+l+"]/BD"));
			layer.setDrainedUpperLimit(xPathDouble(path+"/Water/Layer["+l+"]/DUL"));
			layer.setLowerLimit(xPathDouble(path+"/Water/Layer["+l+"]/LL15"));
			layer.setOrganicCarbon(xPathDouble(path+"/SoilOrganicMatter/Layer["+l+"]/OC"));
			layer.setPh(xPathDouble(path+"/Analysis/Layer["+l+"]/PH"));
			layer.setSaturation(xPathDouble(path+"/Water/Layer["+l+"]/SAT"));
			soil.setSwcon(xPathDouble(path+"/SoilWat/Layer["+l+"]/SWCON"));
			layer.setThickness(xPathDouble(path+"/Water/Layer["+l+"]/Thickness"));
			
			layers[l-1]=layer;
		}


		Double sum = 0.0;
		for(int k=0;k<layers.length;k++){
			sum = sum + layers[k].getThickness()/10;
			layers[k].setBottomDepth(sum);
		}
		return soil;
	}

}
