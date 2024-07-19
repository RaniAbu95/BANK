package myBankApplication.controllers;

import myBankApplication.BL.AccountBL;
import myBankApplication.BL.TransactionBL;
import myBankApplication.beans.Account;
import myBankApplication.beans.Banker;
import myBankApplication.beans.Transaction;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;
import java.time.Instant;
import java.time.LocalDate;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionBL transactionBL;
    private AccountBL accountBL;
    @PostMapping("add")
    public String add(Integer target, String operation, String timeStamp ,int amount, int accountId) throws AccountsAlreadyExistException, AccountBalanceErrorException, AccountPasswordErrorException, CustomerNotFoundException, AccountCategoryErrorException, AccountNotFoundException, TransactionAlreadyExistException, TransactionTargetNotFoundErrorException, TransactionOperationNotFoundErrorException, TransactionNotSavedInDatabase, TransactionTimestampNotFoundErrorException, TransactionAmountNotFoundErrorException, LoanAlreadyExistException, LoanTypeErrorException, LoanAmountErrorException, businessLoanAmounLessThan10k//Params passed as query string
    {
        Account account = transactionBL.getTransactionAccount(accountId);
        Transaction transaction = new Transaction(target,operation,timeStamp,amount,account);
        transactionBL.createNewTransaction(transaction);

        return transaction.toString();
    }

}
