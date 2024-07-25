package myBankApplication.BL;

//import myBankApplication.EmailService;
import myBankApplication.beans.Account;
import myBankApplication.beans.Loan;
import myBankApplication.beans.Transaction;
import myBankApplication.dao.TransactionDAO;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Optional;
@Service
public class TransactionBL {


    @Autowired
    private TransactionDAO transactionDao;


    @Autowired
    private AccountBL accountBL;
    @Autowired
    private LoanBL loanBL;



    public Account getTransactionAccount(int accountId) throws AccountNotFoundException {

        return accountBL.getAccount(accountId);
    }

    public Transaction getTransaction(int id) throws TransactionNotFoundException {
        Optional<Transaction> transaction = Optional.ofNullable(this.transactionDao.findById(id));
        if (transaction.isPresent()) {
            return transaction.get();
        }
        throw new TransactionNotFoundException();
    }


    public Transaction createNewTransaction(Transaction transaction) throws AccountNotFoundException, TransactionAlreadyExistException, TransactionTargetNotFoundErrorException, TransactionOperationNotFoundErrorException, TransactionTimestampNotFoundErrorException, TransactionNotSavedInDatabase, TransactionAmountNotFoundErrorException, AccountBalanceErrorException, LoanAlreadyExistException, LoanTypeErrorException, LoanAmountErrorException, businessLoanAmounLessThan10k {

        checkTransaction(transaction);
        return transaction;
    }

    private void checkTransaction(Transaction transaction) throws TransactionAlreadyExistException, TransactionTimestampNotFoundErrorException, TransactionOperationNotFoundErrorException, TransactionTargetNotFoundErrorException, TransactionNotSavedInDatabase, TransactionAmountNotFoundErrorException, AccountBalanceErrorException, AccountNotFoundException, LoanAlreadyExistException, LoanTypeErrorException, LoanAmountErrorException, businessLoanAmounLessThan10k {


        Optional<Transaction> existingTransaction = Optional.ofNullable(this.transactionDao.findById(transaction.getTransactionId()));
        if (existingTransaction.isPresent()) {
            throw new TransactionAlreadyExistException();
        }

        if (transaction.getTimeStamp() == null) {
            throw new TransactionTimestampNotFoundErrorException();
        }
        if (transaction.getOperation() == null) {
            throw new TransactionOperationNotFoundErrorException();
        }
        if (transaction.getAmount() == null) {
            throw new TransactionAmountNotFoundErrorException();
        }
        if (transaction.getOperation() == "CashTransfare") {
            TransformationCheck(transaction);
        }
        saveTransactionInDatebase(transaction);
        checkTransActionTybe(transaction);


    }

    private void TransformationCheck(Transaction transaction) throws TransactionTargetNotFoundErrorException, TransactionNotSavedInDatabase {
        if (transaction.getTarget() == null) {
            throw new TransactionTargetNotFoundErrorException();
        }
        //checkTransActionTybe(transaction);
        saveTransactionInDatebase(transaction);

    }

    private void checkTransActionTybe(Transaction transaction) throws AccountNotFoundException, AccountBalanceErrorException, TransactionNotSavedInDatabase, TransactionAlreadyExistException, TransactionTargetNotFoundErrorException, LoanAlreadyExistException, TransactionOperationNotFoundErrorException, LoanTypeErrorException, LoanAmountErrorException, TransactionAmountNotFoundErrorException, businessLoanAmounLessThan10k, TransactionTimestampNotFoundErrorException {
        if (transaction.getOperation().equals("cashDeposit")) {

            cashDeposit(transaction);
        }
        if (transaction.getOperation().equals("cashWithdrawal")) {
            cashWithdrawal(transaction);
        }
        if (transaction.getOperation().equals("CashTransfare")) {
            CashTransfare(transaction);
        }
        if(transaction.getOperation().equals("Loan")){
            cashDeposit(transaction);
            createLoanTransaction(transaction);

        }
        //if(transaction.getOperation()=="cashDeposit"){}
        //if(transaction.getOperation()=="cashDeposit"){}
        //if(transaction.getOperation()=="cashDeposit"){}


        //  إيداع العملات الأجنبية في الحساب
        //  سحب العملات الأجنبية من الحساب
        //          القروض   <<<<
        //  وديعة في حساب العميل
        //   سحب من حساب العميل
        // سداد القرض
        //  صرف العملات الأجنبية
    }


    public boolean cashDeposit(Transaction transaction) throws TransactionNotSavedInDatabase {
        try {
            int accountId = accountBL.getAccountId(transaction.getAccount());
            int accountBalanceFromDatabase = accountBL.getAccountBalance(accountBL.getAccountId(transaction.getAccount()));
            int newBalance = accountBalanceFromDatabase + transaction.getAmount();
            accountBL.updateAccountBalance(accountId, newBalance);


            return true;
        } catch (Exception e) {
            throw new TransactionNotSavedInDatabase();
        }

    }

    private boolean cashWithdrawal(Transaction transaction) throws TransactionNotSavedInDatabase {
        try {
            int accountId = accountBL.getAccountId(transaction.getAccount());
            int accountBalanceFromDatabase = accountBL.getAccountBalance(accountBL.getAccountId(transaction.getAccount()));
            int newBalance = accountBalanceFromDatabase - transaction.getAmount();
            accountBL.updateAccountBalance(accountId, newBalance);
            return true;
        } catch (Exception e) {
            throw new TransactionNotSavedInDatabase();
        }
    }

    private boolean CashTransfare(Transaction transaction) throws TransactionNotSavedInDatabase {
        try {
            if (cashWithdrawal(transaction) == true) {
                int accountId = accountBL.getAccountId(transaction.getAccount());
                Integer targetAccountId = transaction.getTarget();


                int accountBalanceFromDatabase = accountBL.getAccountBalance(targetAccountId);

                int newBalance = accountBalanceFromDatabase + transaction.getAmount();
                accountBL.updateAccountBalance(targetAccountId, newBalance);

                return true;
            }
            return false;
        } catch (Exception e) {
            throw new TransactionNotSavedInDatabase();
        }


        //public cashWithdrawal()

    }

    public boolean saveTransactionInDatebase(Transaction transaction) throws TransactionNotSavedInDatabase {
        try {
            this.transactionDao.save(transaction);
            return true;
        } catch (Exception e) {
            throw new TransactionNotSavedInDatabase();
        }
    }
    public void checkLoanTransaction(Transaction transaction) throws TransactionAlreadyExistException, TransactionTargetNotFoundErrorException, TransactionOperationNotFoundErrorException, TransactionNotSavedInDatabase, AccountBalanceErrorException, TransactionAmountNotFoundErrorException, TransactionTimestampNotFoundErrorException, AccountNotFoundException, LoanAlreadyExistException, LoanTypeErrorException, LoanAmountErrorException, businessLoanAmounLessThan10k {
        checkTransaction(transaction);
    }
    public void createLoanTransaction(Transaction transaction) throws AccountNotFoundException, TransactionAlreadyExistException, TransactionTargetNotFoundErrorException, TransactionOperationNotFoundErrorException, TransactionTimestampNotFoundErrorException, TransactionNotSavedInDatabase, TransactionAmountNotFoundErrorException, AccountBalanceErrorException, LoanAlreadyExistException, LoanTypeErrorException, LoanAmountErrorException, businessLoanAmounLessThan10k {


        int accountId = transaction.getAccount().getAccountId();
        Account account=transaction.getAccount();
        Integer amount =transaction.getAmount();
        loanBL.loanManagement(amount,account);

    }


}
