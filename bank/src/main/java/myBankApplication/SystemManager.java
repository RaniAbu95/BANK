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
import myBankApplication.BL.LoanBL;
import myBankApplication.beans.Account;

import myBankApplication.beans.Customer;
import myBankApplication.beans.Loan;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.login.AccountNotFoundException;


@Component
public class SystemManager {

    @Autowired
    private AccountBL accountBL;
    @Autowired
    private CustomerBL customerBL;


    @Autowired
    private LoanBL loanBL;


    public void run() throws CustomerEmailErrorException, CustomerLocationErrorException, EmailErrorException, CustomerIdErrorException, CustomerIsNotExistException, LoanAlreadyExistException, LoanTypeErrorException, LoanAmountErrorException, AccountNotFoundException, AccountsAlreadyExistException, AccountBalanceErrorException, AccountPasswordErrorException, CustomerNotFoundException, AccountCategoryErrorException {

        System.out.println("Adding customer to database");
        Customer customer = new Customer("New York", "john_doe", 123456, "john.doe@example.com");



        //System.out.println("Adding account to database");
        Account account = new Account("1000", 1, "Savings", "password123456");

        customerBL.createNewCustomer(customer);

        //accountBL.createNewAccount(account,customer.getCustomerId());








//        accountBL.createAccount(account);




//        System.out.println("Adding loan to database");
//
//        Loan loan = new Loan(1, "Personal Loan", 5000.0f, 5.0f, account);
//        loanBL.createNewLoan(loan,account.getAccountId());




    }

}
