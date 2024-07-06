package myBankApplication.BL;

import myBankApplication.beans.Account;
import myBankApplication.beans.Loan;
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


    public Loan createLoan(Loan loan) throws AccountsAlreadyExistException, AccountBalanceErrorException, AccountCategoryErrorException, AccountPasswordErrorException, LoanAlreadyExist {

        // account can't have two loans
        Optional<Loan> exsitingLoan = Optional.ofNullable(this.loanDAO.findByAccount_AccountId(loan.getAccount().getAccountId()));
        if (exsitingLoan.isPresent()) {
            throw new LoanAlreadyExist();
        }

        if(loan.getLoanType()==null){
            throw new AccountBalanceErrorException();
        }
        if(loan.getAmount() ==0){
            throw new AccountCategoryErrorException();
        }
        if(loan.getIntersetRate() == 0){
            throw new AccountPasswordErrorException();
        }

        return this.loanDAO.save(loan);
    }

}
