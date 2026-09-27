package myBankApplication.beans;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	public ExchangeResult() {
	}

	public ExchangeResult(String provider, String warningUpgradeToV6, String terms, String base,
			LocalDate date, Integer timeLastUpdated, Map<String, Double> rates) {
		this.provider = provider;
		this.warningUpgradeToV6 = warningUpgradeToV6;
		this.terms = terms;
		this.base = base;
		this.date = date;
		this.timeLastUpdated = timeLastUpdated;
		this.rates = rates;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getWarningUpgradeToV6() {
		return warningUpgradeToV6;
	}

	public void setWarningUpgradeToV6(String warningUpgradeToV6) {
		this.warningUpgradeToV6 = warningUpgradeToV6;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Integer getTimeLastUpdated() {
		return timeLastUpdated;
	}

	public void setTimeLastUpdated(Integer timeLastUpdated) {
		this.timeLastUpdated = timeLastUpdated;
	}

	public Map<String, Double> getRates() {
		return rates;
	}

	public void setRates(Map<String, Double> rates) {
		this.rates = rates;
	}
}