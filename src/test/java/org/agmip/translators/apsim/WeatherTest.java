package org.agmip.translators.apsim;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;
import junit.framework.TestCase;
import org.agmip.util.JSONAdapter;
import org.agmip.translators.apsim.core.DailyWeather;
import org.agmip.translators.apsim.core.Weather;
import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.Ignore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.FileUtils;

public class WeatherTest extends TestCase {
    Weather w;
    private static final Logger log = LoggerFactory.getLogger(WeatherTest.class);

    public void setUp() throws Exception {
        /* This is never used so no need to slow down the tests
        ObjectMapper mapper = new ObjectMapper();
        w = mapper.readValue(new File("src/test/resources/json-samples/weather.json"), Weather.class);
        */
    }

    @Test
    public void testDate() throws Exception{
        assertEquals("20120130",DailyWeather.agmip.format(DailyWeather.apsimWeather.parse("2012  30")));
    }


    @Test
    public void testGetYear() throws Exception {
        assertEquals("2012", Util.GetYear("12/12/2012"));
        assertEquals("2000", Util.GetYear("21/09/2000"));
    }

    @Test
    public void testGetDay() throws Exception{
        assertEquals("1", Util.GetDay("01/01/2012"));
        assertEquals("32", Util.GetDay("01/02/2012"));
    }

    @Test
    @Ignore
    public void testWeatherOnly() throws Exception {
        // Load JSON as a map
        URL resource = this.getClass().getResource("/json-samples/mk10.json");
        String json = new Scanner(new File(resource.getPath()), "UTF-8").useDelimiter("\\A").next();
        HashMap<String, Object> input = JSONAdapter.fromJSON(json);
        json = null;

        // Send the map and a temp directory to the translator
        StringBuilder tempDir = new StringBuilder(System.getProperty("java.io.tmpdir"));
        //tempDir.append(File.separator);
        tempDir.append(UUID.randomUUID().toString().replaceAll("-", ""));
        log.debug("Temp directory: {}", tempDir.toString());
        ApsimOutput apsim = new ApsimOutput();
        apsim.writeFile(tempDir.toString(), input);
        File test = new File(tempDir.toString());
        File[] files = test.listFiles();
        if (files.length == 1) {
            assertEquals("MK10.met", files[0].getName());
        }
        // Now clean up
        FileUtils.deleteDirectory(test);
    }

}
