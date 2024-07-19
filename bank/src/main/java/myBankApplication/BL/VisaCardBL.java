package myBankApplication.BL;

import myBankApplication.beans.Account;
import myBankApplication.beans.Transaction;
import myBankApplication.beans.VisaCard;
import myBankApplication.beans.VisaInstallments;
import myBankApplication.dao.VisaCardDAO;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Optional;
@Service
public class VisaCardBL {

    @Autowired
    private VisaCardDAO visaCardDao;
    @Autowired
    private AccountBL accountBL;
    @Autowired
    private VisaInstallmentsBL visaInstallmentsBL;

    public void checkVisaCard(VisaCard visaCard) throws VisaCardAlreadyExistException, VisaCardNumberErrorException, ExpiredDateErrorException, CompanyErrorException, VVCErrorException, LimitErrorException, StatusErrorException, TransactionNotSavedInDatabase {


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
        if(visaCard.getCvv()==null){
            throw new VVCErrorException();
        }
        if(visaCard.getLimit()==null){
            throw new LimitErrorException();
        }
        if(visaCard.getStatus()==null){
            throw new StatusErrorException();
        }
        saveVisaCardInDatebase(visaCard);

    }

    public boolean saveVisaCardInDatebase(VisaCard visaCard) throws TransactionNotSavedInDatabase {
        try {
            this.visaCardDao.save(visaCard);
            return true;
        } catch (Exception e) {
            throw new TransactionNotSavedInDatabase();
        }
    }
    public VisaCard createNewVisaCard(VisaCard visaCard ) throws AccountNotFoundException, VVCErrorException, VisaCardNumberErrorException, LimitErrorException, ExpiredDateErrorException, CompanyErrorException, StatusErrorException, VisaCardAlreadyExistException, TransactionNotSavedInDatabase {

        if(visaCard.getAccount() ==null){
            throw new AccountNotFoundException();
        }


        checkVisaCard( visaCard);
        return  visaCard;
    }

    public VisaCard visaCard(int id) throws VisaCardNotFoundException {
        Optional<VisaCard> visaCard = Optional.ofNullable(this.visaCardDao.findById(id));
        if(visaCard.isPresent()){
            return visaCard.get();
        }
        throw new VisaCardNotFoundException();
    }
    public Account getVisaCardAccount(int accountId) throws AccountNotFoundException {

        return accountBL.getAccount(accountId);
    }
    public void createVisaInstallments(int numberOfInstallments,int valueOfInstallments,int visaCardInstallmentsId) throws VisaInstallmentsNotSavedInDatabase {
        visaInstallmentsBL.createNewVisaInstallments(numberOfInstallments,valueOfInstallments,visaCardInstallmentsId);
    }
}
