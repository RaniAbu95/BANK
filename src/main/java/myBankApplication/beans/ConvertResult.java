package myBankApplication.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

public class ConvertResult {

	@JsonProperty("fromCurrency")
	private String from;
	@JsonProperty("toCurrency")
	private String to;
	@JsonProperty("amount")
	private double amount;
	@JsonProperty("result")
	private double result;

	public ConvertResult() {
	}

	public ConvertResult(String from, String to, double amount, double result) {
		this.from = from;
		this.to = to;
		this.amount = amount;
		this.result = result;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}
}