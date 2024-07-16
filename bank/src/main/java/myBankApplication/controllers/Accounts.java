package myBankApplication.controllers;

import myBankApplication.BL.AccountBL;
import myBankApplication.BL.BankerBL;
import myBankApplication.beans.Account;
import myBankApplication.beans.Banker;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class Accounts {

    @Autowired
    private AccountBL accountBL;


    @PostMapping("add")
    public String add(int customerId,String category,String password) throws AccountsAlreadyExistException, AccountBalanceErrorException, AccountPasswordErrorException, CustomerNotFoundException, AccountCategoryErrorException, AccountNotFoundException, AccountNotSavedInDataBaseErrorException//Params passed as query string
    {
        Account account = new Account(category, password);
        account.setCustomer(accountBL.getCustomer(customerId));
        Banker banker = accountBL.getBankerBl().getBankerWithMinAccounts();
        account.setBanker(banker);
        this.accountBL.getBankerBl().updateBankerAccounts(banker.getBankerId());
        accountBL.addNewAccount(account,customerId);
        return account.toString();
    }

    @GetMapping("get/{accountId}")
    public String getAccount(@PathVariable int accountId) throws AccountNotFoundException {
        Account account = accountBL.getAccount(accountId);
        return account.toString();
    }

    @GetMapping("getAll")
    public List<Account> getAllAccounts() {
        return accountBL.getAllAccounts();
    }


    public void updateAccountBalance(int newBalance,int accountId) throws  AccountNotFoundException
    {
        this.accountBL.updateAccountBalance(newBalance,accountId);
    }





}
