package com.jb.currencyexchange.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jb.currencyexchange.beans.ExchangeResult;

@Service
public class ExchangeRateApiService {

	@Autowired
	private RestTemplate restTemplate;

	private static final String URL = "https://api.exchangerate-api.com/v4/latest/";

	public ExchangeResult goTo3rdParty(String from) {
		ExchangeResult res = restTemplate.getForObject(URL + from, ExchangeResult.class);
		return res;
	}

}
