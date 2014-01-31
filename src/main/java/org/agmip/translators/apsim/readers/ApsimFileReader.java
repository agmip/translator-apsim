package org.agmip.translators.apsim.readers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.agmip.translators.apsim.core.ACE;
import org.agmip.translators.apsim.core.Simulation;
import org.agmip.translators.apsim.core.Soil;
import org.agmip.translators.apsim.core.Weather;

import com.ximpleware.AutoPilot;

/**
 * Helper class to parse an APSIM XML file and extract all unique simulations.
 * 
 * @author Ioannis N. Athanasiadis
 */
public class ApsimFileReader extends VTDReader{
	HashMap<String, Soil> knownSoils  = new HashMap<String, Soil>();
	HashMap<String, Weather> knownWeathers  = new HashMap<String, Weather>();
	
	public ApsimFileReader(String file) {
		super(file,"(//simulation)");
	}


	public ACE readSim() throws Exception{
		AutoPilot a = xpath("");
		int count = 1;
		
		List<Simulation> sims = new ArrayList<Simulation>();

		
		while (a.evalXPath() != -1) {
			Simulation s = read("["+count+"]");
			
			sims.add(s);
		    count++;
		}
				
		ACE coll = new ACE();
		coll.setSoils(knownSoils.values());
		coll.setWeathers(knownWeathers.values());
		coll.setExperiments(sims);
		return coll;
	}
	
	
	@SuppressWarnings("unchecked")
	public Simulation read(String path) throws Exception{
		Simulation sim = new Simulation();
		sim.setExperimentName(xPathText(path+"/@name"));
	    
//		soil.setCn2bare(xPathText(path+"/SoilWat/Cn2Bare"));
{
		// SOIL stuff.
		String shortcut = xPathText(path+"/*/soil/@shortcut");
		String name = xPathText(path+"/*/soil/@name");
		String q="", soilID="";
		if(shortcut.isEmpty()){
			q =prefix+path+"/*/soil";
			soilID = name+path;
		} else{
			soilID = shortcut.substring(1);
			String[] names = soilID.split("/");
			q = "/";
			for(String part:names)	q = q+"/*[@name='"+part+"']";
		}
			

		if(!knownSoils.containsKey(soilID)){
			SoilReader sr = new SoilReader(file,q);
			Soil soil = sr.read("");
			soil.setId(soilID);
			knownSoils.put(soilID, soil);
		}
		sim.setSoilID(soilID);
	}
	{
		//Weather
		String shortcut = xPathText(path+"/metfile/filename/@shortcut");
		if(shortcut.isEmpty()) shortcut = xPathText(path+"/metfile/@shortcut");
		
		WeatherReader w = new WeatherReader();
		String filename;
		if(shortcut.isEmpty()){
			filename = xPathText(path+"/metfile/filename/text()");
		}
		else{
			String[] names = shortcut.substring(1).split("/");
			String q = "";
			for(String part:names)	q = q+"/*[@name='"+part+"']";
			q = q+"/filename/text()";
			
			filename = xAbsPathText(q);
		}
		File f = new File(filename);
		if(!f.isAbsolute()){
			File f1 = new File(super.file);
			filename = f1.getParent().concat("/"+filename);
			f = new File(filename);
		} 
		
		String shortname = f.getName();
		shortname = shortname.substring(0, shortname.lastIndexOf("."));
			
		
		if(!knownWeathers.containsKey(shortname)){
			Weather weather = w.read(filename);
			weather.setId(shortname);
			knownWeathers.put(shortname, weather);
		}
		sim.setWeatherID(shortname);
		return sim;
	}

	}


	@Override
	@Deprecated
	public <T> List<T> read() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}
	

