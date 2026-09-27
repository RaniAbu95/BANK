package myBankApplication.controllers;

import myBankApplication.BL.AccountBL;
import myBankApplication.beans.Account;
import myBankApplication.beans.Banker;
import myBankApplication.beans.Loan;
import myBankApplication.beans.Transaction;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountBL accountBL;


    @PostMapping("add")
    public Account add(@RequestParam int customerId,@RequestParam String  category,@RequestParam String password) throws AccountsAlreadyExistException, AccountBalanceErrorException, AccountPasswordErrorException, CustomerNotFoundException, AccountCategoryErrorException, AccountNotFoundException, AccountNotSavedInDataBaseErrorException, BankerNotSavedInDataBaseErrorException, CustomerEmailUnVerfiyedErrorException//Params passed as query string
    {
        Account account = new Account(category, password);
        account.setCustomer(accountBL.getCustomer(customerId));
        Banker banker = accountBL.getBankerBl().getBankerWithMinAccounts();
        account.setBanker(banker);
        //this.accountBL.getBankerBl().updateBankerAccounts(banker.getBankerId());
        return accountBL.addNewAccount(account,customerId);
    }

    @GetMapping("getId/{accountId}")
    public Account getAccount(@PathVariable int accountId) throws AccountNotFoundException {
        return  accountBL.getAccount(accountId);
    }

    @GetMapping("getBalance/{accountId}")
    public double getBalance(@PathVariable int accountId) throws AccountNotFoundException {
        return  accountBL.getAccountBalance(accountId);
    }

    @GetMapping("getAllTransactions/{accountId}")
    public List<Transaction> getLastTransactions(@PathVariable int accountId) throws AccountNotFoundException {
        return this.accountBL.getAllTransactions(accountId);
    }

    @GetMapping("getAllLoans/{accountId}")
    public List<Loan> getAllLoans(@PathVariable int accountId) throws AccountNotFoundException {
        return this.accountBL.getAllLoanTransactions(accountId);
    }


    @GetMapping("getAll")
    public List<Account> getAllAccounts() {
        return accountBL.getAllAccounts();
    }


    public void updateAccountBalance(int newBalance,int accountId) throws AccountNotFoundException, AccountBalanceErrorException {
        this.accountBL.updateAccountBalance(newBalance,accountId);
    }

    @PutMapping("suspend/{accountId}")
    public Account updateStatusToSuspend(@PathVariable int accountId) throws AccountNotFoundException, AccountNotSavedInDataBaseErrorException {
        return  accountBL.updateStatusToSuspend(accountId);
    }

}
