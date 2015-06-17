package org.agmip.translators.apsim.core;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @since May 28, 2015
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Adjustment {

	// id
	@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
	@JsonProperty("variable")
	private String variable = "";

	public String getVariable() {
		return variable;
	}

	public void setVariable(String value) {
		variable = value;
	}

	@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
	@JsonProperty("method")
	private String method = "";

	public String getMethod() {
		return method;
	}

	public void setMethod(String value) {
		method = value;
	}

	@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
	@JsonProperty("startdate")
	private String startdate = "";

	public String getStartDate() {
		return startdate;
	}

	public void setStartDate(String value) {
		startdate = value;
	}

	@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
	@JsonProperty("enddate")
	private String enddate = "";

	public String getEndDate() {
		return enddate;
	}

	public void setEndDate(String value) {
		enddate = value;
	}

	@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
	@JsonProperty("value")
	private String value = "";

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getManagerScript() {
    	// convert AgMIP variable names to APSIM.
    	if (variable.equals("tmax")) variable = "met.maxt";
    	if (variable.equals("tmin")) variable = "met.mint";
    	if (variable.equals("rain")) variable = "met.rain";
    	if (variable.equals("co2y")) variable = "co2";

    	// Write some APSIM manager script.		
		String script = "";
		String indent = "      ";
    	if (!startdate.equals("0000-01-01") && !enddate.equals("0000-01-01")) {
    		script = "      if (DateUtility.WithinDates(DateTime.ParseExact(\"" + startdate + "\", \"yyyy-M-d\", CultureInfo.InvariantCulture),\n"
     			   + "                                  Today,\n"
    			   + "                                  DateTime.ParseExact(\"" + enddate + "\", \"yyyy-M-d\", CultureInfo.InvariantCulture)))\n";
    		indent = "         ";
    	}
    	
    	String operator = "?";
    	if (method.equals("delta")) operator = " += ";
    	else if (method.equals("multiply")) operator = " *= ";
    	else if (method.equals("substitute")) operator = " = ";
    	
    	script += indent + variable + operator + value + "F;\n";
    	
    	return script;
    }

	// default constructor - Needed for Jackson
	public Adjustment() {
	}

}
