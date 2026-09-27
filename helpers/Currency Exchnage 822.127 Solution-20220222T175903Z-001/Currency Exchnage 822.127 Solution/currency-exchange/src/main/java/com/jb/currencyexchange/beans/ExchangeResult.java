package com.jb.currencyexchange.beans;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeResult {

	@JsonProperty("provider")
	private String provider;
	
	@JsonProperty("WARNING_UPGRADE_TO_V6")
	private String warningUpgradeToV6;
	
	@JsonProperty("terms")
	private String terms;
	
	@JsonProperty("base")
	private String base;
	
	@JsonProperty("date")
	private LocalDate date;
	
	@JsonProperty("time_last_updated")
	private Integer timeLastUpdated;
	
	@JsonProperty("rates")
	private Map<String, Double> rates = new HashMap<>();/*Rates rates;*/

}