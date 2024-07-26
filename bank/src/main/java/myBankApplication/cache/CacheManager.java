package myBankApplication.cache;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import myBankApplication.beans.ExchangeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



import myBankApplication.services.ExchangeRateApiService;

@Component
public class CacheManager {

	@Autowired
	@Qualifier("map")
	private Map<String, Map<String, Double>> map;

	@Autowired
	private ExchangeRateApiService service;

	public void add(String from, Map<String, Double> values) {
		map.put(from, values);
	}

	public double getCurrency(String from, String to) {

		if (map.get(from) == null || map.get(from).get(to) == null) {
			ExchangeResult res = service.goTo3rdParty(from);
			map.put(from, res.getRates());
			System.out.println("++++++ 0.01 USD");
		} else {
			System.out.println("Free of charge, read from cache!!!!!!!");
		}

		return map.get(from).get(to);
	}


	@Scheduled(cron = "0 0 6 * * ?") // https://dzone.com/articles/running-on-time-with-springs-scheduled-tasks
	//@Scheduled(fixedRate = 60000)
	public void removeDaily() {
		map.clear();
	}

}
