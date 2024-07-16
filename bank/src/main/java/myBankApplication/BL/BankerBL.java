package myBankApplication.BL;


import myBankApplication.beans.Account;
import myBankApplication.beans.Banker;
import myBankApplication.dao.AccountDAO;
import myBankApplication.dao.BankerDAO;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class BankerBL {
    @Autowired
    private BankerDAO bankerDAO;

    @Autowired
    private AccountBL accountBL;

    public void checkBanker(Banker banker) throws BankerEmailErrorException, BankerNameErrorException {
        if(banker.getName()==null){
            throw new BankerNameErrorException();
        }
        if(banker.getEmail() ==null){
            throw new BankerEmailErrorException();
        }
    }
    public void addNewBanker(Banker banker) throws BankerEmailErrorException, BankerNameErrorException, BankerNotSavedInDataBaseErrorException {
        checkBanker(banker);
        saveBankerInDataBase(banker);

    }

    public Banker getBankerWithMinAccounts(){
        return bankerDAO.findBankerWithMinAccounts();
    }

    public List<Banker> getAllBankers(){
        return this.bankerDAO.findAll();

    }

    public Banker getBanker(int id) throws AccountNotFoundException {

        Optional<Banker>banker = this.bankerDAO.findById(id);
        if(banker.isPresent()){
            return banker.get();
        }
        throw new AccountNotFoundException();
    }

    public Banker updateBankerAccounts(int bankerId){
        this.bankerDAO.incrementNumberOfAccounts(bankerId);
        return this.bankerDAO.findById(bankerId).get();
    }

    public boolean saveBankerInDataBase(Banker banker) throws  BankerNotSavedInDataBaseErrorException {
        try{
            this.bankerDAO.save(banker);
            return true;
        }
        catch(Exception e){
            throw new BankerNotSavedInDataBaseErrorException();
        }
    }


}
