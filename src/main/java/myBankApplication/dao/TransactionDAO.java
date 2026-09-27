package myBankApplication.dao;

import myBankApplication.beans.Account;
import myBankApplication.beans.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionDAO extends JpaRepository<Transaction,Integer> {

    Transaction findById(int transactionId);
    List<Transaction> findAllByAccount(Account account);

}
