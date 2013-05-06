package org.agmip.translators.apsim.core;

import java.util.Collection;
import java.util.Vector;

import org.codehaus.jackson.annotate.JsonProperty;

public class SimulationCollection {
	@JsonProperty("soils")
	Collection<Soil> soils = new Vector<Soil>();
	public Collection<Soil> getSoils() { return soils; }
	@JsonProperty("weathers")
	Collection<Weather> weathers = new Vector<Weather>();
	public Collection<Weather> getWeathers() { return weathers; }

	@JsonProperty("experiments")
	Collection<SimulationRun> experiments = new Vector<SimulationRun>();
	public Collection<SimulationRun> getExperiments() { return experiments; }

	public void initialise() throws Exception{

		if (experiments != null) {
			for(SimulationRun sim:experiments){
				String mySoil = sim.getSoilID();
				for(Soil soil:soils){
					if(mySoil.equals(soil.getID())){
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
	}



	public void setSoils(Collection<Soil> soils) {
		this.soils = soils;
	}

	public void setWeathers(Collection<Weather> weathers) {
		this.weathers = weathers;
	}

	public void setExperiments(Collection<SimulationRun> experiments) {
		this.experiments = experiments;
	}
}
