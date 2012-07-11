/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.agmip.translators.apsim;

import java.text.ParseException;
import java.util.Map;

import org.agmip.translators.apsim.weather.Weather;

/**
 *
 * @author Dean Holzworth
 */
public class Experiment {

	
    Weather weather = new Weather();
    Soil soil = new Soil();
    Management treatment = new Management();
    private InitialConditions initialConditions = new InitialConditions();
    
    
    public void readFrom(Map input) throws ParseException {
        soil.readFrom(input);
        initialConditions.readFrom(input);
    }


    public Soil getSoil() {
        return soil;
    }

    public void setSoil(Soil soil) {
        this.soil = soil;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
}
