package myBankApplication.BL;

import myBankApplication.beans.Account;
import myBankApplication.beans.Customer;
import myBankApplication.beans.Loan;
import myBankApplication.beans.Transaction;
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
    private TransactionBL transactionBL;

    public void loanManagement(int loanamount,Account account) throws businessLoanAmounLessThan10k, LoanAlreadyExistException, LoanTypeErrorException, LoanAmountErrorException, AccountNotFoundException {

        if(loanamount>10000)
        {
            //Account account=accountbl.getAccount(accountId);
            int intersetRate= 10;
            String loanType ="business";
            Loan loan = new Loan(account,intersetRate,loanamount,loanType);
            createNewLoan(loan);
        }
    }

    public void createNewLoan(Loan loan ) throws AccountNotFoundException, LoanAlreadyExistException, LoanTypeErrorException, LoanAmountErrorException {

        if(loan.getAccount() ==null){
            throw new AccountNotFoundException();
        }


        checkLoan(loan);
    }
    public Loan checkLoan(Loan loan) throws LoanAlreadyExistException, LoanAmountErrorException, LoanTypeErrorException {


        Optional<Loan> existingLoan = Optional.ofNullable(this.loanDAO.findById(loan.getLoanId()));
        if (existingLoan.isPresent()) {
            throw new LoanAlreadyExistException();
        }
        System.out.println(loan.getAmount());
        if(loan.getAmount()<=0){
            throw new LoanAmountErrorException();
        }
        if(loan.getLoanType()== null){
            throw new LoanTypeErrorException();
        }
        if(loan.getIntersetRate()<=0){
            throw new LoanTypeErrorException();
        }

        return this.loanDAO.save(loan);
    }
    private void loanIntersetClaculation(Loan loan){
        int loanAmount= (int) loan.getAmount();
        int loanIntersetRate = (int)loan.getIntersetRate();
        transferringTheLoanToTheAccount(loanIntersetRate);
    }
    private boolean transferringTheLoanToTheAccount(int loanIntersetRate){
//        try{
//            transactionBL.cashDeposit()
//        }
        return false;
    }




}



