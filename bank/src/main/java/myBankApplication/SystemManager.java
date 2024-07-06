//package myBankApplication;
//
//import myBankApplication.BL.AccountBL;
//import myBankApplication.BL.CustomerBL;
//
//import myBankApplication.beans.Account;
//
//
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;

package myBankApplication;

import myBankApplication.BL.AccountBL;
import myBankApplication.BL.CustomerBL;
import myBankApplication.beans.Account;

import myBankApplication.exceptions.AccountBalanceErrorException;
import myBankApplication.exceptions.AccountCategoryErrorException;
import myBankApplication.exceptions.AccountPasswordErrorException;
import myBankApplication.exceptions.AccountsIsNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class SystemManager {

    @Autowired
    private AccountBL accountBL;
    @Autowired
    private CustomerBL customerBL;


    public void run() throws AccountsIsNotExistException, AccountBalanceErrorException, AccountPasswordErrorException, AccountCategoryErrorException {

        System.out.println("Adding account to database");
        Account account = new Account("1000", 1, "Savings", "password123");

        accountBL.createAccount(account);

    }
}
