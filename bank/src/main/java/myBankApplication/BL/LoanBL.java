package myBankApplication.BL;

import myBankApplication.beans.Account;
import myBankApplication.beans.Customer;
import myBankApplication.beans.Loan;
import myBankApplication.dao.AccountDao;
import myBankApplication.dao.CustomerDoa;
import myBankApplication.dao.LoanDAO;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoanBL {

    @Autowired
    private LoanDAO loanDAO;

    @Autowired
    private AccountDao accountDAO;


    public Loan createLoan(Account account) throws AccountsAlreadyExistException, AccountBalanceErrorException, AccountCategoryErrorException, AccountPasswordErrorException, LoanAlreadyExist {


        Optional<Customer> exsitingLoan = Optional.ofNullable(this.loanDAO.findById(account.getAccountId()));
        if (exsitingLoan.isPresent()) {
            throw new LoanAlreadyExist();
        }

        if(account.getAccountId()==0){
            throw new AccountBalanceErrorException();
        }
        if(account.getAmount() ==0){
            throw new AccountCategoryErrorException();
        }
        if(account.getCategory() !=null){
            throw new AccountPasswordErrorException();
        }

        return this.loanDAO.save(loan);
    }

    public Account getAccount(int id) throws CustomerNotFoundException {
        Optional<Account>account = Optional.ofNullable(this.accountDAO.findById(id));
        if(account.isPresent()){
            return account.get();
        }
        throw new CustomerNotFoundException();
    }
}
