package org.agmip.translators.apsim;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import junit.framework.TestCase;

import org.agmip.translators.apsim.core.ACE;
import org.agmip.translators.apsim.core.Simulation;
import org.junit.Test;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 */
public class ApsimWriterTests extends TestCase{

    File outputPath = new File("src/test/resources/gen");

    private ACE fileResourceToACE(String FileName) throws Exception{
        URL resource = this.getClass().getResource(FileName);
        String json = new Scanner(new File(resource.getPath()), "UTF-8").useDelimiter("\\A").next();
        return ApsimWriter.jsonToACE(json);
    }



    @Test
    public void testReadJSONFile() throws Exception {
    	ACE ace = fileResourceToACE("/json-samples/MaizeIncomplete.json");

        Simulation sim = ace.getExperiments().iterator().next();
        assertEquals(0.10, sim.getSoil().getSalb());
        assertEquals("18/03/1981", sim.getManagement().getEvents().get(0).getDate());
        assertEquals(0.12, sim.getSoil().getLayers()[0].getLowerLimit());
        assertEquals("maize", sim.getManagement().plantingCropName());
        assertEquals("maize", sim.getInitialCondition().getResidueType());
    }

    @Test
    public void testWriteMaizeIncomplete() throws Exception {
    	ACE ace = fileResourceToACE("/json-samples/MaizeIncomplete.json");

        ArrayList<String> files = new ArrayList<String>();
        ApsimWriter.generateAPSIMFile("MaizeIncomplete.apsim", outputPath, ace, files);
        ApsimWriter.generateMetFiles(outputPath, ace, files);
        
        assertEquals(files.size(), 2);
        assertEquals(files.get(0), "MaizeIncomplete.apsim");
        assertEquals(files.get(1), "MK10.met");
    }  
 
    @Test
    public void testWriteMaizeComplete() throws Exception {
    	ACE ace = fileResourceToACE("/json-samples/Maize.json");

        ArrayList<String> files = new ArrayList<String>();
        ApsimWriter.generateAPSIMFile("Maize.apsim", outputPath, ace, files);
        ApsimWriter.generateMetFiles(outputPath, ace, files);
        
        assertEquals(files.size(), 2);
        assertEquals(files.get(0), "Maize.apsim");
        assertEquals(files.get(1), "MK10.met");
    }  

    @Test
    public void testWriteWheatComplete() throws Exception {
    	ACE ace = fileResourceToACE("/json-samples/Wheat.json");

        ArrayList<String> files = new ArrayList<String>();
        ApsimWriter.generateAPSIMFile("Wheat.apsim", outputPath, ace, files);
        ApsimWriter.generateMetFiles(outputPath, ace, files);
        
        assertEquals(files.size(), 2);
        assertEquals(files.get(0), "Wheat.apsim");
        assertEquals(files.get(1), "MK10.met");
    }    
    
    @Test
    public void testWriteSorghumComplete() throws Exception {
    	ACE ace = fileResourceToACE("/json-samples/Sorghum.json");

        ArrayList<String> files = new ArrayList<String>();
        ApsimWriter.generateAPSIMFile("Sorghum.apsim", outputPath, ace, files);
        ApsimWriter.generateMetFiles(outputPath, ace, files);
        
        assertEquals(files.size(), 3);
        assertEquals(files.get(0), "Sorghum.apsim");
    }    
    
    @Test
    public void testWriteSugarComplete() throws Exception {
    	ACE ace = fileResourceToACE("/json-samples/Sugar.json");

        ArrayList<String> files = new ArrayList<String>();
        ApsimWriter.generateAPSIMFile("Sugar.apsim", outputPath, ace, files);
        ApsimWriter.generateMetFiles(outputPath, ace, files);
        
        assertEquals(files.size(), 2);
        assertEquals(files.get(0), "Sugar.apsim");
    }    
    
    @Test
    public void testWriteMilletComplete() throws Exception {
    	ACE ace = fileResourceToACE("/json-samples/Millet.json");

        ArrayList<String> files = new ArrayList<String>();
        ApsimWriter.generateAPSIMFile("Millet.apsim", outputPath, ace, files);
        ApsimWriter.generateMetFiles(outputPath, ace, files);
        
        assertEquals(files.size(), 2);
        assertEquals(files.get(0), "Millet.apsim");
    }    
    
    @Test
    public void testWriteRiceComplete() throws Exception {
    	ACE ace = fileResourceToACE("/json-samples/RicePaddySample.json");

        ArrayList<String> files = new ArrayList<String>();
        ApsimWriter.generateAPSIMFile("RicePaddySample.apsim", outputPath, ace, files);
        ApsimWriter.generateMetFiles(outputPath, ace, files);
        
        assertEquals(files.size(), 2);
        assertEquals(files.get(0), "RicePaddySample.apsim");
    }      
    
    @Test
    public void testWriteCottonComplete() throws Exception {
    	ACE ace = fileResourceToACE("/json-samples/Cotton.json");

        ArrayList<String> files = new ArrayList<String>();
        ApsimWriter.generateAPSIMFile("Cotton.apsim", outputPath, ace, files);
        ApsimWriter.generateMetFiles(outputPath, ace, files);
        
        assertEquals(files.size(), 2);
        assertEquals(files.get(0), "Cotton.apsim");
    }     
    
    @Test
    public void testWriteRicePaddyComplete() throws Exception {
    	ACE ace = fileResourceToACE("/json-samples/RicePaddy.json");

        ArrayList<String> files = new ArrayList<String>();
        ApsimWriter.generateAPSIMFile("RicePaddy.apsim", outputPath, ace, files);
        ApsimWriter.generateMetFiles(outputPath, ace, files);
        
        assertEquals(files.size(), 2);
        assertEquals(files.get(0), "RicePaddy.apsim");
    }      
    
}
