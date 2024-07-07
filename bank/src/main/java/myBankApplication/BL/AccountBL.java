package myBankApplication.BL;

import myBankApplication.beans.Account;
import myBankApplication.beans.Customer;
import myBankApplication.dao.AccountDao;
import myBankApplication.exceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.security.auth.login.AccountNotFoundException;
import java.util.Optional;

@Service
public class AccountBL {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private CustomerBL customerLB;


    public Account checkAccount(Account accounts) throws AccountsAlreadyExistException, AccountBalanceErrorException, AccountCategoryErrorException, AccountPasswordErrorException {


        Optional<Account> existingAccount = this.accountDao.findById(accounts.getAccountId());
        if (existingAccount.isPresent()) {
            throw new AccountsAlreadyExistException();
        }

        if(accounts.getBalance()==null){
            throw new AccountBalanceErrorException();
        }
        if(accounts.getCategory() ==null){
            throw new AccountCategoryErrorException();
        }
        if(accounts.getPassword() == null){
            throw new AccountPasswordErrorException();
        }

        return this.accountDao.save(accounts);
    }


    public Account createNewAccount(Account accounts , int id) throws CustomerNotFoundException, AccountsAlreadyExistException, AccountBalanceErrorException, AccountPasswordErrorException, AccountCategoryErrorException {

        if(customerLB.getCustomer(id) ==null){
            throw new CustomerNotFoundException();
        }


        accounts = checkAccount( accounts);
        return  accounts;
    }

    public Account getAccount(int id) throws AccountNotFoundException {
        Optional<Account>account = this.accountDao.findById(id);
        if(account.isPresent()){
            return account.get();
        }
        throw new AccountNotFoundException();
    }

}
