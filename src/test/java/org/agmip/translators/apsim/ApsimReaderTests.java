package org.agmip.translators.apsim;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.agmip.translators.apsim.core.ACE;
import org.agmip.translators.apsim.core.Soil;
import org.agmip.translators.apsim.core.Weather;
import org.agmip.translators.apsim.readers.ApsimFileReader;
import org.agmip.translators.apsim.readers.SoilReader;
import org.agmip.translators.apsim.readers.WeatherReader;
import org.junit.Test;

public class ApsimReaderTests {
	
	@Test
	public void testWeatherReader() throws Exception{
		WeatherReader	weatherReader = new WeatherReader();
		Weather weather = weatherReader.read("src/test/resources/apsim/Yucheng.met");
		assertEquals(36.68, weather.getLatitude(), 0.01);
		assertEquals("Yucheng",weather.getName());
		assertEquals(1189, weather.getRecords().size());
		assertEquals("02/11/2002", weather.getRecords().get(1).getDate());
		assertEquals(14.7, weather.getRecords().get(1).getMinTemperature(), 0.01);
		assertEquals(23.1, weather.getRecords().get(1).getMaxTemperature(), 0.01);
		
	}
	
	@Test
	public void testSimulationCollectionReader() throws Exception{
		ApsimFileReader	sr = new ApsimFileReader("src/test/resources/apsim/SmallWheatValidation.apsim");
		ACE c = sr.readSim();
		assertEquals(3,c.getExperiments().size());
		assertEquals(1,c.getSoils().size());
		assertEquals(1,c.getWeathers().size());
	}

	@Test
	public void testSoilReader() throws Exception{
		SoilReader	sr = new SoilReader("src/test/resources/apsim/SmallWheatValidation.apsim");
		List<Soil> soils = sr.read();
		assertEquals(2,soils.size());
		assertEquals("Yucheng",soils.get(0).getId());
		assertEquals(10,soils.get(1).getLayers().length);
	}	
	
}
