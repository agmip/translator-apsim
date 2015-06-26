package org.agmip.translators.apsim.core;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.codehaus.jackson.annotate.JsonProperty;

public class ACE {
	// soils
	@JsonProperty("soils")
	Collection<Soil> soils = new Vector<Soil>();
	public Collection<Soil> getSoils() { return soils; }
	public void setSoils(Collection<Soil> soils) {
		this.soils = soils;
	}
	
	// weathers
	@JsonProperty("weathers")
	Collection<Weather> weathers = new Vector<Weather>();
	public Collection<Weather> getWeathers() { return weathers; }
	public void setWeathers(Collection<Weather> weathers) {
		this.weathers = weathers;
	}

	// experiments
	@JsonProperty("experiments")
	List<Simulation> experiments = new Vector<Simulation>();
	public List<Simulation> getExperiments() { return experiments; }
	public void setExperiments(List<Simulation> experiments) {
		this.experiments = experiments;
	}

	// initialise this object
	public void initialise() throws Exception{

		// Initialise each soil.
		for(Soil soil:soils)
			soil.initialise();
		
		// For each experiment, give it the corresponding soil, climate and any adjustments.
		if (experiments != null) {
			for(Simulation sim:experiments){
				String mySoil = sim.getSoilID();
				for(Soil soil:soils){
					if(mySoil.equals(soil.getId())){
						soil.setCropName(sim.getManagement().plantingCropName());
						sim.setSoil(soil);
						break;
					}
				}


				String myWeather = sim.getWeatherID();
				for (Weather w : weathers){
					if(myWeather.equals(w.getId())){
						sim.setWeather(w); break;
					}
				}
				
				sim.initialise();
			}
		}
		
		// For now assume a single soil/crop parameterisation. Will have to change maybe.
                // Commented at 01/23/2014, in order to avoid the exception there is no experiment in the data set
//		for(Soil soil:soils)
//			soil.setCropName(experiments.get(0).getManagement().plantingCropName());
	}
 

}
