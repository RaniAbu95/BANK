package myBankApplication.BL;

import myBankApplication.beans.*;
import myBankApplication.dao.LoanDAO;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LoanBL {

    @Autowired
    private LoanDAO loanDAO;

    @Autowired
    private AccountBL accountBl;

    private TransactionBL transactionBL;

    public void saveLoanInDataBase(Loan loan) throws businessLoanAmounLessThan10k, LoanAlreadyExistException, LoanTypeErrorException, LoanAmountErrorException, AccountNotFoundException {

        double loanamount = loan.getAmount();
        int intersetRate;
        if(loanamount<100000 && loanamount>=50000)
        {
            intersetRate = 30;
            loan.setIntersetRate(intersetRate);
            String loanType ="Gold Loan";
            loan.setLoanType(loanType);
            loan.setNumberOfPayments(36);

        }

        else if(loanamount<50000 && loanamount>=10000)
        {
            intersetRate = 20;
            loan.setIntersetRate(intersetRate);
            String loanType ="Platinum Loan";
            loan.setLoanType(loanType);
            loan.setNumberOfPayments(24);

        }
        else if(loanamount<10000)
        {
            intersetRate = 10;
            loan.setIntersetRate(intersetRate);
            String loanType ="Basic Loan";
            loan.setLoanType(loanType);
            loan.setNumberOfPayments(12);
        }
        else {
            throw new LoanAmountErrorException();
        }
        checkLoan(loan);
        loanDAO.save(loan);

    }

    public void createNewLoan(double amount, int accountId) throws AccountNotFoundException, LoanAlreadyExistException, LoanTypeErrorException, LoanAmountErrorException, businessLoanAmounLessThan10k {
        Account account = accountBl.getAccount(accountId);
        if(account ==null){
            throw new AccountNotFoundException();
        }

        Loan loan = new Loan(account,amount);
        saveLoanInDataBase(loan);
    }
    public void checkLoan(Loan loan) throws LoanAlreadyExistException, LoanAmountErrorException, LoanTypeErrorException {

        Optional<Loan> existingLoan = Optional.ofNullable(this.loanDAO.findById(loan.getLoanId()));
        if (existingLoan.isPresent() && existingLoan.get().getCompletedPayments()==loan.getCompletedPayments()) {
            throw new LoanAlreadyExistException();
        }

        if (loan.getAmount() <= 0) {
            throw new LoanAmountErrorException();
        }
        if (loan.getLoanType() == null) {
            throw new LoanTypeErrorException();
        }
        if (loan.getIntersetRate() <= 0) {
            throw new LoanTypeErrorException();
        }
    }

    public List<Loan> getAllLoans(int accountId) throws AccountNotFoundException {
        Account account = accountBl.getAccount(accountId);
        return this.loanDAO.findAllByAccount(account);
    }

    @Scheduled(cron = "0 0 12 15 * ?")
    //@Scheduled(fixedRate = 60000)
    public void loanPayment() throws TransactionAlreadyExistException, LoanTypeErrorException, TransactionAmountNotFoundErrorException, VisaInstallmentsNotSavedInDatabase, TransactionTargetNotFoundErrorException, LoanAlreadyExistException, TransactionOperationNotFoundErrorException, TransactionNotSavedInDatabase, AccountBalanceErrorException, LoanAmountErrorException, businessLoanAmounLessThan10k, AccountNotFoundException, TransactionTimestampNotFoundErrorException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        System.out.println("paymentOfInstallments is called");
        List<Loan> loans = this.loanDAO.findAll();
        for(Loan loan : loans ){
            if(loan.getNumberOfPayments()-loan.getCompletedPayments()>0){
                double totalAmountToPay = (1+loan.getIntersetRate()/100.0)*(loan.getAmount()/loan.getNumberOfPayments());
                int accountId = loan.getAccount().getAccountId();
                accountBl.addNewPayment("LoanInstallment",currentDateTime.toString(),totalAmountToPay,accountId,null,loan);
            }
        }
    }


}



