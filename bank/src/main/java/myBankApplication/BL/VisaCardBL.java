package myBankApplication.BL;

import myBankApplication.beans.Account;
import myBankApplication.beans.VisaCard;
import myBankApplication.dao.VisaCardDao;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Optional;
@Service
public class VisaCardBL {

    @Autowired
    private VisaCardDao  visaCardDao;
    @Autowired
    private AccountBL accountBL;



    public VisaCard checkVisaCard(VisaCard visaCard) throws VisaCardAlreadyExistException, VisaCardNumberErrorException, ExpiredDateErrorException, CompanyErrorException, VVCErrorException, LimitErrorException, StatusErrorException {


        Optional<VisaCard> existingVisaCard = Optional.ofNullable(this.visaCardDao.findById(visaCard.getVisaCardId()));
        if (existingVisaCard.isPresent()) {
            throw new VisaCardAlreadyExistException();
        }

        if(visaCard.getVisaCardNumber()==0){
            throw new VisaCardNumberErrorException();
        }
        if(visaCard.getExpiredDate()==null){
            throw new ExpiredDateErrorException();
        }
        if(visaCard.getCompany()==null){
            throw new CompanyErrorException();
        }
        if(visaCard.getVvc()==null){
            throw new VVCErrorException();
        }
        if(visaCard.getLimit()==null){
            throw new LimitErrorException();
        }
        if(visaCard.getStatus()==null){
            throw new StatusErrorException();
        }

        return this.visaCardDao.save(visaCard);
    }


    public VisaCard createNewVisaCard(VisaCard visaCard , int id) throws AccountNotFoundException, VVCErrorException, VisaCardNumberErrorException, LimitErrorException, ExpiredDateErrorException, CompanyErrorException, StatusErrorException, VisaCardAlreadyExistException {

        if(accountBL.getAccount(id) ==null){
            throw new AccountNotFoundException();
        }


        visaCard = checkVisaCard( visaCard);
        return  visaCard;
    }

    public VisaCard visaCard(int id) throws VisaCardNotFoundException {
        Optional<VisaCard> visaCard = Optional.ofNullable(this.visaCardDao.findById(id));
        if(visaCard.isPresent()){
            return visaCard.get();
        }
        throw new VisaCardNotFoundException();
    }
}
