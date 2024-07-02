package com.jb.currencyexchange.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConvertResult {

	@JsonProperty("fromCurrency")
	private String from;
	@JsonProperty("toCurrency")
	private String to;
	@JsonProperty("amount")
	private double amount;
	@JsonProperty("result")
	private double result;
	
}