package org.agmip.translators.apsim.events;

import java.text.ParseException;
import java.util.Date;
import org.agmip.translators.apsim.util.Converter;
import org.agmip.translators.apsim.util.DateDeserializer;
import org.agmip.translators.apsim.util.DateSerializer;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @since Jul 13, 2012
 */

@JsonTypeInfo(  
    use = JsonTypeInfo.Id.NAME,  
    include = JsonTypeInfo.As.PROPERTY,  
    property = "event")  
@JsonSubTypes({  
    @Type(value = Irrigation.class, name = "irrigation"),
    @Type(value = Planting.class, name = "planting"), 
    @Type(value = Fertilizer.class, name = "fertilizer") ,
    @Type(value = OrganicMatter.class, name = "organic_matter"), 
    @Type(value = Tillage.class, name = "tillage"), 
    @Type(value = Chemical.class, name = "chemical"), 
    @Type(value = Harvest.class, name = "harvest"),  
    })  
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Event {
	
    // date
    @JsonSerialize(using=DateSerializer.class)
    @JsonDeserialize(using=DateDeserializer.class)
    private String date = "?";
    public String getDate() { return date; }
    public Date getEventDate() {
        try {
            if (date == null)
                return null;
            else
                return Converter.apsim.parse(date);
        } catch (ParseException ex) {
            return null;
        }
     }

    // log
    protected String log = "";
    public String getLog() { return log; }
    
    
    // action
    abstract String getApsimAction();

    // initialise this instance.
    public abstract void initialise();
}




