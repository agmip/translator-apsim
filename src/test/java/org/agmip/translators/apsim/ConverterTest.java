package org.agmip.translators.apsim;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import junit.framework.TestCase;

import org.agmip.translators.apsim.core.SimulationCollection;
import org.agmip.translators.apsim.core.SimulationRun;
import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 */
public class ConverterTest extends TestCase{

	SimulationCollection collection;
    File outputPath = new File("src/test/resources/gen");

    private void loadJSONFromResource(String FileName) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        URL resource = this.getClass().getResource(FileName);
        String json = new Scanner(new File(resource.getPath()), "UTF-8").useDelimiter("\\A").next();
        collection = mapper.readValue(json, SimulationCollection.class);
        collection.initialise();
    }



    @Test
    public void testReadJSONFile() throws Exception {
        loadJSONFromResource("/json-samples/mach_fast.json");

        SimulationRun sim = collection.getExperiments().iterator().next();
        assertEquals("0.10", sim.getSoil().getSalb());
        assertEquals("18/03/1981", sim.getManagement().getEvents().get(0).getDate());
        assertEquals("0.12", sim.getSoil().getLayers()[0].getLowerLimit());
        assertEquals("Maize", sim.getManagement().plantingCropName());
        assertEquals("Maize", sim.getInitialCondition().getResidueType());
    }

    @Test
    public void testWriteAPSIMFile() throws Exception {
        loadJSONFromResource("/json-samples/mach_fast.json");
        long ping = System.currentTimeMillis();
        try {
            ArrayList<String> files = new ArrayList<String>();
            ApsimOutput.generateAPSIMFile(outputPath, collection, files);
            ApsimOutput.generateMetFiles(outputPath, collection, files);
        } catch (Exception e) {
                assertTrue(e.toString(),false);
        }
        assertTrue("APSIM file generated in " +(System.currentTimeMillis()-ping) +" ms", true);

    }  

 

}
