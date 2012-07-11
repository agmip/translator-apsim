package org.agmip.translators.apsim;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.agmip.util.JSONAdapter;

/**
 * Unit test for simple App.
 */
public class TranslationTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TranslationTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TranslationTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testTranslation()
    {
        try {
            ApsimOutput Translator = new ApsimOutput();
        
            JSONAdapter j = new JSONAdapter();
            LinkedHashMap<String, Object> result;

            File file = new File( "src/test/resources/UFGA8201_mzx.json");

            Scanner scanner = new Scanner(new FileReader(file));
    		String json ="";
            while(scanner.hasNext())
    			json += scanner.nextLine();
            
            
            result = (LinkedHashMap<String, Object>) JSONAdapter.fromJSON(json);

            Translator.writeFile("src/test/resources/", result);
            //File file = new File("test.apsim");
            
            //assertTrue(file.exists());
            //assertTrue(file.getName().equals("UFGA7801_SBX.SOL"));
            //assertTrue(file.delete());
        } catch (IOException ex) {
            Logger.getLogger(TranslationTest.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue(false);
        }
    }
    
}
