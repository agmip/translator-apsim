package org.agmip.translators.apsim;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.agmip.core.types.TranslatorOutput;
import org.agmip.util.MapUtil;
import org.agmip.util.MapUtil.BucketEntry;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;




public class ApsimOutput implements TranslatorOutput {
    
    

    
    public void writeFile(String filePath, Map input) {
    
        // Initialise velocity
        Velocity.init();
        VelocityContext context = new VelocityContext();
        
        // Weather variable
        BucketEntry weather = MapUtil.getBucket(input, "weather").get(0);
        context.put( "wst_insi", MapUtil.getValueOr(weather.getValues(), "wst_insi", "?"));
                
        System.out.println(input.toString());
        
        Experiment experiment = new Experiment();
        try {
			experiment.readFrom(input);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        context.put( "experiment", experiment);

        // Write template.
        Template template = Velocity.getTemplate("src\\main\\resources\\AgMIPTemplate.apsim");
        FileWriter F;
        try {
            
            F = new FileWriter(new File(filePath,"Test.apsim"));
            template.merge( context, F );
            F.close();
            
        } catch (IOException ex) {
            Logger.getLogger(ApsimOutput.class.getName()).log(Level.SEVERE, null, ex);
        }
          
        
    }
}
