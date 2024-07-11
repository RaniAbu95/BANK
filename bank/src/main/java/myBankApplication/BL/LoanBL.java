package myBankApplication.BL;

import myBankApplication.beans.Account;
import myBankApplication.beans.Customer;
import myBankApplication.beans.Loan;
import myBankApplication.dao.LoanDAO;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Optional;

@Service
public class LoanBL {

    @Autowired
    private LoanDAO loanDAO;
    private AccountBL accountbl;




    public Loan checkLoan(Loan loan) throws LoanAlreadyExistException, LoanAmountErrorException, LoanTypeErrorException {


        Optional<Loan> existingLoan = Optional.ofNullable(this.loanDAO.findById(loan.getLoanId()));
        if (existingLoan.isPresent()) {
            throw new LoanAlreadyExistException();
        }

        if(loan.getAmount() ==0){
            throw new LoanAmountErrorException();
        }
        if(loan.getLoanType() !=null){
            throw new LoanTypeErrorException();
        }

        return this.loanDAO.save(loan);
    }

    public Loan createNewLoan(Loan loan , int id) throws AccountNotFoundException, LoanAlreadyExistException, LoanTypeErrorException, LoanAmountErrorException {

    if(accountbl.getAccount(id) ==null){
        throw new AccountNotFoundException();
    }
    loan = checkLoan(loan);
    return  loan;
}

}



