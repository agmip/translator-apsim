package org.agmip.translators.apsim.core;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.agmip.translators.apsim.util.Util;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;


/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @since Jul 13, 2012
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyWeather {
	
    public static final SimpleDateFormat agmip = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat apsimWeather = new SimpleDateFormat("yyyy D");

    // date
    @JsonProperty("w_date")
    @JsonDeserialize(using=DailyWeatherDeserializer.class)
    @JsonSerialize(using=DailyWeatherSerializer.class,include=JsonSerialize.Inclusion.NON_DEFAULT)
    private String date ="1000/01/01";
    public String getDate() { return date; }
    public void setDate(String value) {date = value;}

    // solarRadiation
    @JsonProperty("srad")
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
    private double solarRadiation = Util.missingValue;
    public double getSolarRadiation() { return solarRadiation; }
    public void setSolarRadiation(double value) {solarRadiation = value;}
    
    // maxTemperature
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
    @JsonProperty("tmax")
    private double maxTemperature = Util.missingValue;
    public double getMaxTemperature() { return maxTemperature; }
    public void setMaxTemperature(double value) {maxTemperature = value;}
    
    // minTemperature
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
    @JsonProperty("tmin")
    private double minTemperature = Util.missingValue;
    public double getMinTemperature() { return minTemperature; }
    public void setMinTemperature(double value) {minTemperature = value;}
    
    // rainfall
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
    @JsonProperty("rain")
    private double rainfall = Util.missingValue;
    public double getRainfall() { return rainfall; }
    public void setRainfall(double value) {rainfall = value;}
    
    // windSpeed
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
    @JsonProperty("wind")
    private double windSpeed = Util.missingValue;
    public double getWindSpeed() { return windSpeed; }
    
    // dewPoint
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
    @JsonProperty("dewp")
    private double dewPoint = Util.missingValue;
    public double getDewPoint() { return dewPoint; }
    
    // vaporPressure
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
    @JsonProperty("vprs")
    private double vaporPressure = Util.missingValue;
    public double getVaporPressure() { return vaporPressure; }

    // relativeHumidity
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
    @JsonProperty("rhum")
    private double relativeHumidity = Util.missingValue;
    public double getRelativeHumidity() { return relativeHumidity; }

    // Needed for Jackson
    public DailyWeather() {}
    
    
    
    
    

    public static class DailyWeatherDeserializer extends JsonDeserializer<String> {			 

        @Override
        public String deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
            
            try {
                Date date = agmip.parse(jp.getText());
                String outputDate = apsimWeather.format(date);
                if(outputDate.length() == 8) {
                    return outputDate;
                } else {
                    StringBuffer buf = new StringBuffer(outputDate);
                    buf.insert(5, " ");
                    if (buf.length() == 7) {
                        buf.insert(5, " ");
                    }
                    return buf.toString();
                }
            } catch (ParseException e) {
                throw new IOException(e);
            }
        }
    }
    
    
    
    public static class DailyWeatherSerializer extends JsonSerializer<String> {

        @Override
        public void serialize(String date, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
            
            String formattedDate="";
            try {
                date = date.replaceAll("[ ]+", " ");
                formattedDate = agmip.format(apsimWeather.parse(date));
            } catch (ParseException e) {
                throw new IOException(e);
            }

            gen.writeString(formattedDate);
        }
    }
    
    
    
}
