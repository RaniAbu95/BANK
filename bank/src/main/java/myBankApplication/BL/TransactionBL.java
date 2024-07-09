package myBankApplication.BL;

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


    public Transaction checkTransaction(Transaction transaction) throws TransactionAlreadyExistException {


        Optional<Transaction> existingTransaction = Optional.ofNullable(this.transactionDao.findById(transaction.getTransactionId()));
        if (existingTransaction.isPresent()) {
            throw new TransactionAlreadyExistException();
        }

        //timeStamp
        //operation
        //target



        return this.transactionDao.save(transaction);
    }


    public Transaction createNewTransaction(Transaction transaction , int id) throws AccountNotFoundException, TransactionAlreadyExistException {

        if(accountBL.getAccount(id) ==null){
            throw new AccountNotFoundException();
        }


        transaction = checkTransaction( transaction);
        return  transaction;
    }

    public Transaction getTransaction(int id) throws TransactionNotFoundException {
        Optional<Transaction> transaction = Optional.ofNullable(this.transactionDao.findById(id));
        if(transaction.isPresent()){
            return transaction.get();
        }
        throw new TransactionNotFoundException();
    }
}
