package myBankApplication.controllers;

import myBankApplication.BL.AccountBL;
import myBankApplication.beans.Account;
import myBankApplication.beans.Banker;
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
    public Account add(int customerId,String category,String password) throws AccountsAlreadyExistException, AccountBalanceErrorException, AccountPasswordErrorException, CustomerNotFoundException, AccountCategoryErrorException, AccountNotFoundException, AccountNotSavedInDataBaseErrorException//Params passed as query string
    {

        Account account = new Account(category, password);
        account.setCustomer(accountBL.getCustomer(customerId));
        Banker banker = accountBL.getBankerBl().getBankerWithMinAccounts();
        account.setBanker(banker);
        this.accountBL.getBankerBl().updateBankerAccounts(banker.getBankerId());
        Account newAccount = accountBL.addNewAccount(account,customerId);
        return newAccount;
    }

    @GetMapping("get/{accountId}")
    public Account getAccount(@PathVariable int accountId) throws AccountNotFoundException {
        return  accountBL.getAccount(accountId);
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
