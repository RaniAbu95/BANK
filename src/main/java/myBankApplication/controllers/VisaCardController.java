package myBankApplication.controllers;

import myBankApplication.BL.AccountBL;
import myBankApplication.BL.TransactionBL;
import myBankApplication.BL.VisaCardBL;
import myBankApplication.BL.VisaInstallmentsBL;
import myBankApplication.beans.Account;
import myBankApplication.beans.Transaction;
import myBankApplication.beans.VisaCard;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;
@RestController
@RequestMapping("/visaCards")
public class VisaCardController {
    @Autowired
    private VisaCardBL visaCardBL;

    @Autowired
    private VisaInstallmentsBL VisaInstallments;

    @PostMapping("add")
    public String add(@RequestParam int accountId, @RequestParam String status, @RequestParam String company, @RequestParam int cvv, @RequestParam String expiredDate, @RequestParam long visaCardNumber,@RequestParam int limit) throws AccountsAlreadyExistException, AccountBalanceErrorException, AccountPasswordErrorException, CustomerNotFoundException, AccountCategoryErrorException, AccountNotFoundException, TransactionAlreadyExistException, TransactionTargetNotFoundErrorException, TransactionOperationNotFoundErrorException, TransactionNotSavedInDatabase, TransactionTimestampNotFoundErrorException, TransactionAmountNotFoundErrorException, LoanAlreadyExistException, LoanTypeErrorException, LoanAmountErrorException, businessLoanAmounLessThan10k, VVCErrorException, VisaCardNumberErrorException, LimitErrorException, ExpiredDateErrorException, CompanyErrorException, StatusErrorException, VisaCardAlreadyExistException//Params passed as query string
    {
        Account account = visaCardBL.getVisaCardAccount(accountId);
        VisaCard visaCard = new VisaCard(account,status,company,cvv,expiredDate,visaCardNumber,limit);
        visaCardBL.createNewVisaCard(visaCard);

        return visaCard.toString();
    }


    @PostMapping("addInstallments")
    public String addInstallments(int numberOfInstallments,int valueOfInstallments,int visaCardInstallmentsId) throws VisaInstallmentsNotSavedInDatabase {
        visaCardBL.createVisaInstallments(numberOfInstallments,valueOfInstallments,visaCardInstallmentsId);
        return VisaInstallments.toString();
    }


}
