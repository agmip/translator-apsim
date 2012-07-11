package org.agmip.translators.apsim;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public class Management {

	/**
	 * Experiment identifier
	 * Unit or Type: text
	 */
	@JsonProperty("eid")
	String experimentID ;
	
	/**
	 * Name of experiment
	 * Unit or Type: text
	 */
	@JsonProperty("exname")
	String experimentName ;


	/**
	 * Duration of experiment in years
	 * Unit or Type: number
	 */
	@JsonProperty("exp_dur")
	Integer experimentDuration;


	/**
	 * Cultivar name
	 * Unit or Type: text
	 */
	@JsonProperty("cul_name")
	String cultivarName;


	/**
	 * Field slope (from horizontal)
	 * Unit or Type: degree angle
	 */
	@JsonProperty("flsl")
	Double fieldSlope;

	
	/**
	 * Field slope length
	 * Unit or Type: m
	 */
	@JsonProperty("flsll")
	Double fieldSlopeLength;

	/**
	 * Drainage, type in field
	 * Unit or Type: code
	 */
	@JsonProperty("fl_drntype")
	String fieldDrainageCode ;


	/**
	 * Water table depth
	 * Unit or Type: cm
	 */
	@JsonProperty("dtwt")
	String waterTableDepth;
	
	
	/**
	 * Growth stage date, emergence
	 * Unit or Type: date
	 */
	@JsonProperty("pldae")
	Date emergenceDate;

	/**
	 * Growth stage of planting, as date
	 * Unit or Type: date
	 */
	@JsonProperty("pldae")
	Date plantingDate;

	/**
	 * Plant population at planting
	 * Unit or Type: number/m2
	 */
	@JsonProperty("plpop")
	Double populationAtPlanting;
	
	
	/**
	 * Plant population at emergence
	 * Unit or Type: number/m2
	 */
	@JsonProperty("plpoe")
	Double populationAtEmergence;


	/**
	 * Planting depth
	 * Unit or Type: mm
	 */
	@JsonProperty("pldp")
	Double plantingDepth;

	/**
	 * Row spacing
	 * Unit or Type: cm
	 */
	@JsonProperty("plrs")
	String plantingRowSpacing;





}
