package myBankApplication.controllers;

import myBankApplication.BL.AccountBL;
import myBankApplication.BL.LoanBL;
import myBankApplication.beans.Account;
import myBankApplication.beans.Loan;
import myBankApplication.beans.Transaction;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("/loans")
public class LoanController {
    @Autowired
    private LoanBL loanBL;
    @Autowired
    private AccountBL   accountBL;
    @PostMapping("add")
    public void add(String loanType,int loanamount,int intersetRate,int accountId) throws AccountsAlreadyExistException, AccountBalanceErrorException, AccountPasswordErrorException, CustomerNotFoundException, AccountCategoryErrorException, AccountNotFoundException, TransactionAlreadyExistException, TransactionTargetNotFoundErrorException, TransactionOperationNotFoundErrorException, TransactionNotSavedInDatabase, TransactionTimestampNotFoundErrorException, TransactionAmountNotFoundErrorException, LoanAlreadyExistException, LoanTypeErrorException, LoanAmountErrorException, businessLoanAmounLessThan10k//Params passed as query string
    {
        Account account = accountBL.getAccount(accountId);
        Loan loan = new Loan(account,intersetRate,loanamount,loanType);
        loanBL.createNewLoan(loan);


    }
}
