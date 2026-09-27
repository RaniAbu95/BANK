package myBankApplication.BL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import myBankApplication.beans.ConvertResult;
import myBankApplication.cache.CacheManager;
import myBankApplication.services.ExchangeRateApiService;

@Service
public class CurrencyExchangeRateBL {
    //http://localhost:8080/money/convert?from=USD&to=ILS&amount=100

    @Autowired
    private ExchangeRateApiService service;

    @Autowired
    private CacheManager cacheManager;



    public double convert(String from, double amount){
//		ExchangeResult res = service.goTo3rdParty(from);
//		double rate = res.getRates().get(to);
        String to="ILS";
        double rate = cacheManager.getCurrency(from, to);
        ResponseEntity<ConvertResult> convertedAmount= new ResponseEntity<>(new ConvertResult(from,to,amount,rate*amount), HttpStatus.OK);
        return convertedAmount.getBody().getResult();
        //return new ResponseEntity<>(new ConvertResult(from,to,amount,rate*amount), HttpStatus.OK);
    }


}
