package myBankApplication.controllers;

import myBankApplication.BL.AccountBL;
import myBankApplication.BL.TransactionBL;
import myBankApplication.beans.Account;
import myBankApplication.beans.Banker;
import myBankApplication.beans.OperationRequest;
import myBankApplication.beans.Transaction;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.time.Instant;
import java.time.LocalDate;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionBL transactionBL;
    private AccountBL accountBL;
    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody OperationRequest request, @RequestParam int accountId) throws
            AccountsAlreadyExistException,
            AccountBalanceErrorException,
            AccountPasswordErrorException,
            CustomerNotFoundException,
            AccountCategoryErrorException,
            AccountNotFoundException,
            TransactionAlreadyExistException,
            TransactionTargetNotFoundErrorException,
            TransactionOperationNotFoundErrorException,
            TransactionNotSavedInDatabase,
            TransactionTimestampNotFoundErrorException,
            TransactionAmountNotFoundErrorException,
            LoanAlreadyExistException,
            LoanTypeErrorException,
            LoanAmountErrorException,
            businessLoanAmounLessThan10k
    {
        // Use request.getOperation() and other methods to access the properties
        Account account = transactionBL.getTransactionAccount(accountId);
        Integer target = request.getTarget();
        String operation = request.getOperation();
        String timeStamp = request.getTimeStamp();
        String foreignCurrency = request.getForeigCurrencyToExchange();
        int amount = request.getAmount();
        transactionBL.createNewTransaction(target, operation, timeStamp ,amount, accountId,foreignCurrency);

        return ResponseEntity.ok("Transaction completed successfully");

        // Your business logic here
    }





}
