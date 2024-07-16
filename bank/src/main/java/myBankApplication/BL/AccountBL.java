package myBankApplication.BL;

import myBankApplication.beans.Account;
import myBankApplication.beans.Customer;
import myBankApplication.dao.AccountDAO;
import myBankApplication.exceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class AccountBL {

    @Autowired
    private AccountDAO accountDAO ;

    @Autowired
    private CustomerBL customerBL;

    @Autowired
    @Lazy
    private BankerBL bankerBL;

    public void checkAccount(Account account, int customerId) throws AccountsAlreadyExistException,  AccountCategoryErrorException, AccountPasswordErrorException, CustomerNotFoundException {

        Optional<Account> existingAccount = this.accountDAO.findById(account.getAccountId());
        if (existingAccount.isPresent()) {
            throw new AccountsAlreadyExistException();
        }
        if(getCustomer( customerId) ==null){
            throw new CustomerNotFoundException();
        }
        if(account.getCategory() ==null){
            throw new AccountCategoryErrorException();
        }
        if(account.getPassword() == null){
            throw new AccountPasswordErrorException();
        }

    }


    public void addNewAccount(Account account , int customerId) throws CustomerNotFoundException, AccountsAlreadyExistException,  AccountPasswordErrorException, AccountCategoryErrorException, AccountNotSavedInDataBaseErrorException {
        checkAccount(account,customerId);
        saveAccountInDataBase(account);
    }

    public Account getAccount(int id) throws AccountNotFoundException {
        Optional<Account>account = this.accountDAO.findById(id);
        if(account.isPresent()){
            return account.get();
        }
        throw new AccountNotFoundException();
    }

    public List<Account> getAllAccounts() {
        return this.accountDAO.findAll();
    }

    public Customer getCustomer(int id) throws CustomerNotFoundException {
        return this.customerBL.getCustomer(id);
    }

    public int getAccountBalance(int accountId) throws AccountNotFoundException {
        Optional<Account> account = this.accountDAO.findById(accountId);
        if(account.isPresent()){
            return account.get().getBalance();
        }
        else {
            throw new AccountNotFoundException();
        }
    }

    public void updateAccountBalance(int accountId,int newBalance) throws AccountNotFoundException {
        this.accountDAO.updateAccountBalance(accountId,newBalance);
    }

    public boolean saveAccountInDataBase(Account account) throws AccountNotSavedInDataBaseErrorException {
        try{
            this.accountDAO.save(account);
            return true;
        }
        catch(Exception e){
            throw new AccountNotSavedInDataBaseErrorException();
        }
    }
    public BankerBL getBankerBl() {
        return this.bankerBL;
    }
}
