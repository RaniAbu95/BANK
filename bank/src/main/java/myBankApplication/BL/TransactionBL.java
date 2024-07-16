package myBankApplication.BL;

import myBankApplication.beans.Account;
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



    public Account getTransactionAccount(int accountId) throws AccountNotFoundException {

        return accountBL.getAccount(accountId);
    }

    public Transaction getTransaction(int id) throws TransactionNotFoundException {
        Optional<Transaction> transaction = Optional.ofNullable(this.transactionDao.findById(id));
        if(transaction.isPresent()){
            return transaction.get();
        }
        throw new TransactionNotFoundException();
    }


    public Transaction createNewTransaction(Transaction transaction ) throws AccountNotFoundException, TransactionAlreadyExistException, TransactionTargetNotFoundErrorException, TransactionOperationNotFoundErrorException, TransactionTimestampNotFoundErrorException, TransactionNotSavedInDatabase, TransactionAmountNotFoundErrorException {

        checkTransaction( transaction);
        return  transaction;
    }
    private void checkTransaction(Transaction transaction) throws TransactionAlreadyExistException, TransactionTimestampNotFoundErrorException, TransactionOperationNotFoundErrorException, TransactionTargetNotFoundErrorException, TransactionNotSavedInDatabase, TransactionAmountNotFoundErrorException {


        Optional<Transaction> existingTransaction = Optional.ofNullable(this.transactionDao.findById(transaction.getTransactionId()));
        if (existingTransaction.isPresent()) {
            throw new TransactionAlreadyExistException();
        }

        if(transaction.getTimeStamp() ==null){
            throw new TransactionTimestampNotFoundErrorException();
        }
        if(transaction.getOperation() ==null){
            throw new TransactionOperationNotFoundErrorException();
        }
        if(transaction.getAmount() ==null){
            throw new TransactionAmountNotFoundErrorException();
        }
        if(transaction.getOperation() =="Transformation"){
            TransformationCheck(transaction);
        }
        //checkTransActionTybe(transaction);
        saveTransactionInDatebase(transaction);

    }
    private void TransformationCheck(Transaction transaction) throws TransactionTargetNotFoundErrorException, TransactionNotSavedInDatabase {
        if(transaction.getTarget() ==null){
            throw new TransactionTargetNotFoundErrorException();
        }
        //checkTransActionTybe(transaction);
        saveTransactionInDatebase(transaction);

    }
    private void checkTransActionTybe(Transaction transaction){
        if(transaction.getOperation()=="cashDeposit"){}
        if(transaction.getOperation()=="cashWithdrawal"){}
        //if(transaction.getOperation()=="cashDeposit"){}
        //if(transaction.getOperation()=="cashDeposit"){}
        //if(transaction.getOperation()=="cashDeposit"){}
        //if(transaction.getOperation()=="cashDeposit"){}
        //if(transaction.getOperation()=="cashDeposit"){}


        //  إيداع العملات الأجنبية في الحساب
      //  سحب العملات الأجنبية من الحساب
      //          القروض
      //  وديعة في حساب العميل
      //   سحب من حساب العميل
      // سداد القرض
      //  صرف العملات الأجنبية
    }








    public int cashDeposit(Transaction transaction){

        return 0;
    }
    //public cashWithdrawal()
    public boolean saveTransactionInDatebase(Transaction transaction) throws TransactionNotSavedInDatabase {
        try {
            this.transactionDao.save(transaction);
            return true;
        } catch (Exception e) {
            throw new TransactionNotSavedInDatabase();
        }
    }
}
