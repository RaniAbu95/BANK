package myBankApplication.BL;

import myBankApplication.BL.CustomerBL;
import myBankApplication.beans.Account;
import myBankApplication.dao.AccountDao;
import myBankApplication.exceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class AccountBL {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private CustomerBL customerLB;


    public Account createAccount(Account accounts) throws AccountsIsNotExistException, AccountBalanceErrorException, AccountCategoryErrorException, AccountPasswordErrorException {


        Optional<Account> existingAccount = Optional.ofNullable(this.accountDao.findById(accounts.getAccountId()));
        if (existingAccount.isPresent()) {
            throw new AccountsIsNotExistException();
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


    public Account createNewAccount(Account accounts , int id) throws CustomerNotFoundException, AccountsIsNotExistException, AccountBalanceErrorException, AccountPasswordErrorException, AccountCategoryErrorException {

        if(customerLB.getCustomer(id) ==null){
            throw new CustomerNotFoundException();
        }


        accounts = createAccount( accounts);
        return  accounts;
    }
}
