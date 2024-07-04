package BL;

import beans.Account;
import dao.AccountDao;
import exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class AccountBL {

    @Autowired
    private AccountDao accountDao;
    private dao.AccountDao AccountDao;
    private CustomerBL customerLB;

    public Account createAccount(Account accounts) throws AccountsIsNotExistException, AccountBalanceErrorException, AccountCategoryErrorException, AccountPasswordErrorException {

        Optional<Account>existingAccount= Optional.ofNullable(this.accountDao.findById(accounts.getAccountId()));

        if(existingAccount != null){
            throw new AccountsIsNotExistException();
        }
        if(accounts.getBalance()==null){
            throw new AccountBalanceErrorException();
        }
        if(accounts.getCategory() !=null){
            throw new AccountCategoryErrorException();
        }
        if(accounts.getPassword() != null){
            throw new AccountPasswordErrorException();
        }


        return this.AccountDao.save(accounts);
    }


    public Account createNewAccount(Account accounts , int id) throws CustomerNotFoundException, AccountsIsNotExistException, AccountBalanceErrorException, AccountPasswordErrorException, AccountCategoryErrorException {

        if(customerLB.getCustomer(id) ==null){
            throw new CustomerNotFoundException();
        }


        accounts = createAccount( accounts);
        return  accounts;
    }
}
