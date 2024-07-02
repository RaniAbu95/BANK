package com.jb.currencyexchange.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jb.currencyexchange.beans.ConvertResult;
import com.jb.currencyexchange.beans.ExchangeResult;
import com.jb.currencyexchange.cache.CacheManager;
import com.jb.currencyexchange.services.ExchangeRateApiService;

@RestController
@RequestMapping("money")
public class MyController {
	//http://localhost:8080/money/convert?from=USD&to=ILS&amount=100
	
	@Autowired
	private ExchangeRateApiService service;
	
	@Autowired
	private CacheManager cacheManager;
	
	@GetMapping("convert")
	public ResponseEntity<?> convert(@RequestParam String from,@RequestParam String to,@RequestParam double amount){
//		ExchangeResult res = service.goTo3rdParty(from);
//		double rate = res.getRates().get(to);
		double rate = cacheManager.getCurrency(from, to);
		return new ResponseEntity<>(new ConvertResult(from,to,amount,rate*amount), HttpStatus.OK);
	}
}
