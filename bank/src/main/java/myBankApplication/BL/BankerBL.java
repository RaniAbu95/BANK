package myBankApplication.BL;


import myBankApplication.beans.Account;
import myBankApplication.dao.AccountDAO;
import myBankApplication.dao.BankerDAO;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Optional;

@Service
public class BankerBL {
    @Autowired
    private BankerDAO bankerDAO;

    @Autowired
    private AccountBL accountBL;

    public boolean checkBanker(Account account) throws AccountsAlreadyExistException, AccountBalanceErrorException, AccountCategoryErrorException, AccountPasswordErrorException {

        return false;
    }
    public void addNewBanker(Account account , int id) throws CustomerNotFoundException, AccountsAlreadyExistException, AccountBalanceErrorException, AccountPasswordErrorException, AccountCategoryErrorException, AccountNotFoundException {
        if(this.accountBL.getAccount(id) ==null){
            throw new CustomerNotFoundException();
        }

    }

//    public Account getAccount(int id) throws AccountNotFoundException {
//        Optional<Account> account = this.accountDAO.findById(id);
//        if(account.isPresent()){
//            return account.get();
//        }
//        throw new AccountNotFoundException();
//    }

    }
