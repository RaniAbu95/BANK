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

import myBankApplication.beans.Customer;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class SystemManager {

    @Autowired
    private AccountBL accountBL;
    @Autowired
    private CustomerBL customerBL;


    public void run() throws AccountsAlreadyExistException, AccountBalanceErrorException, AccountPasswordErrorException, AccountCategoryErrorException, CustomerEmailErrorException, CustomerLocationErrorException, EmailErrorException, CustomerIdErrorException, CustomerIsNotExistException {

        System.out.println("Adding account to database");
//        Account account = new Account("1000", 1, "Savings", "password123");
//
//        accountBL.createAccount(account);

        Customer customer = new Customer("New York", "john_doe", 12345, "john.doe@example.com");

        customerBL.createCustomer(customer);
    }

}
