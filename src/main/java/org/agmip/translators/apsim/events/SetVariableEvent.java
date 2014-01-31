package org.agmip.translators.apsim.events;

import org.agmip.translators.apsim.core.Management;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @since Jul 13, 2012
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SetVariableEvent extends Event {

    // default constructor
    public SetVariableEvent() { }
    
    private String moduleName;
    private String variableName;
    private String variableValue;
    
    // special constructor used by management
    public SetVariableEvent(String date, String moduleName, String variableName, String variableValue) {
    	setDate(date);
    	this.moduleName = moduleName;
    	this.variableName = variableName;
    	this.variableValue = variableValue;
    }
    
    
    // initialise this instance.
    @Override
    public void initialise(Management management) {
    }

    
    // apsimAction
    @Override
    public String getApsimAction() {
        return "'" + moduleName + "' set " + variableName + " = " + variableValue;
    }    

}
