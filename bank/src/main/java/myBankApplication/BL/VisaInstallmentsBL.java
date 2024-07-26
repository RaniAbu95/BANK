package myBankApplication.BL;

import myBankApplication.beans.VisaCard;
import myBankApplication.beans.VisaInstallments;
import myBankApplication.dao.VisaCardDAO;
import myBankApplication.dao.VisaInstallmentsDAO;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service

public class VisaInstallmentsBL {
    @Autowired
    private VisaCardDAO visaCardDAO ;
    @Autowired
    private VisaInstallmentsDAO visaInstallmentsDAO;

    @Autowired
    private TransactionBL transactionBL;


    public VisaInstallments createNewVisaInstallments(int numberOfInstallments, int valueOfInstallments, int visaCardInstallmentsId) throws  VisaInstallmentsNotSavedInDatabase {

        VisaInstallments visaInstallments = new VisaInstallments(numberOfInstallments,valueOfInstallments,visaCardInstallmentsId);
        checkVisaInstallments(visaInstallments);
        return visaInstallments;
    }
    private void checkVisaInstallments(VisaInstallments visaInstallments) throws VisaInstallmentsNotSavedInDatabase {



        saveVisaInstallmentsInDatebase(visaInstallments);



    }
    public boolean saveVisaInstallmentsInDatebase(VisaInstallments visaInstallments) throws VisaInstallmentsNotSavedInDatabase {
        try {
            this.visaInstallmentsDAO.save(visaInstallments);
            return true;
        } catch (Exception e) {
            throw new VisaInstallmentsNotSavedInDatabase();
        }
    }

    //@Scheduled(fixedRate = 30000)
    public void visaInstallmentsPayment() throws AccountNotFoundException, TransactionAlreadyExistException, TransactionTargetNotFoundErrorException, LoanAlreadyExistException, TransactionOperationNotFoundErrorException, LoanTypeErrorException, TransactionNotSavedInDatabase, AccountBalanceErrorException, LoanAmountErrorException, TransactionAmountNotFoundErrorException, businessLoanAmounLessThan10k, TransactionTimestampNotFoundErrorException, VisaInstallmentsNotSavedInDatabase {
        LocalDateTime currentDateTime = LocalDateTime.now();
        System.out.println("paymentOfInstallments is called");
        List<VisaCard> visaCards = this.visaCardDAO.findAll(); //change this to visaCardBL.getAllVisaCards()
        for(VisaCard visaCard : visaCards ){
            int visaCardId = visaCard.getVisaCardId();
            List<VisaInstallments> installmentsList = visaInstallmentsDAO.findAllByVisaCardInstallmentsId(visaCardId);
            for (VisaInstallments visaInstallments : installmentsList) {
                if(Boolean.FALSE.equals(visaInstallments.isInstalmentCompleted())){
                    //transactionBL.createNewTransaction(null, "cashWithdrawal", currentDateTime.toString() ,visaInstallments.getValueOfInstallments(), visaCard.getAccount().getAccountId(),"null");
                    addNewVisaPayment(null,  "cashWithdrawal",  currentDateTime.toString() ,visaInstallments.getValueOfInstallments(),  visaCard.getAccount().getAccountId(), "null", visaInstallments,visaCard);
                }
                //transactionBL.createNewTransaction(null, "cashWithdrawal", currentDateTime.toString() ,visaInstallments.getValueOfInstallments(), visaCard.getAccount().getAccountId(),"null");
            }
        }
    }

    public void addNewVisaPayment(Integer target, String operation, String timeStamp , int amount, int accountId, String foreignCurrency, VisaInstallments visaInstallments, VisaCard visaCard) throws TransactionAlreadyExistException, TransactionTargetNotFoundErrorException, LoanAlreadyExistException, TransactionOperationNotFoundErrorException, LoanTypeErrorException, TransactionNotSavedInDatabase, AccountBalanceErrorException, LoanAmountErrorException, TransactionAmountNotFoundErrorException, businessLoanAmounLessThan10k, AccountNotFoundException, TransactionTimestampNotFoundErrorException, VisaInstallmentsNotSavedInDatabase {

        if(visaInstallments.getNumberOfInstallments()> visaInstallments.getNumberOfPayments()){
            transactionBL.createNewTransaction(null, "cashWithdrawal", timeStamp.toString() ,visaInstallments.getValueOfInstallments(), visaCard.getAccount().getAccountId(),"null");
            visaInstallments.setNumberOfPayments(visaInstallments.getNumberOfPayments() + 1);
            saveVisaInstallmentsInDatebase( visaInstallments);
        }
        else {
            visaInstallments.setInstalmentCompleted(Boolean.TRUE);
            saveVisaInstallmentsInDatebase(visaInstallments);
        }
    }
}
