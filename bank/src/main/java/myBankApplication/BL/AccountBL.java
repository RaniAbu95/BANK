package myBankApplication.BL;

import myBankApplication.beans.Account;
import myBankApplication.beans.Customer;
import myBankApplication.dao.AccountDAO;
import myBankApplication.exceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.security.auth.login.AccountNotFoundException;
import java.util.Optional;

@Service
public class AccountBL {

    @Autowired
    private AccountDAO accountDao;

    @Autowired
    private CustomerBL customerLB;


    public boolean checkAccount(Account account) throws AccountsAlreadyExistException, AccountBalanceErrorException, AccountCategoryErrorException, AccountPasswordErrorException {


        Optional<Account> existingAccount = this.accountDao.findById(account.getAccountId());
        if (existingAccount.isPresent()) {
            throw new AccountsAlreadyExistException();
        }

        if(account.getBalance()==null){
            throw new AccountBalanceErrorException();
        }
        if(account.getCategory() ==null){
            throw new AccountCategoryErrorException();
        }
        if(account.getPassword() == null){
            throw new AccountPasswordErrorException();
        }

        return true;
    }


    public void addNewAccount(Account account , int id) throws CustomerNotFoundException, AccountsAlreadyExistException, AccountBalanceErrorException, AccountPasswordErrorException, AccountCategoryErrorException {

        if(getCustomer(id) ==null){
            throw new CustomerNotFoundException();
        }
        if(checkAccount( account)){
            this.accountDao.save(account);
        }
    }

    public Customer getCustomer(int id) throws CustomerNotFoundException {
        Optional<Customer>customer = this.customerLB.getCustomerDoa().findById(id);
        if(customer.isPresent()){
            return customer.get();
        }
        throw new CustomerNotFoundException();
    }

    public Account getAccount(int id) throws AccountNotFoundException {
        Optional<Account>account = this.accountDao.findById(id);
        if(account.isPresent()){
            return account.get();
        }
        throw new AccountNotFoundException();
    }

}
