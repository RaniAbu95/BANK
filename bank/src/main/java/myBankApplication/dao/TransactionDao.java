package myBankApplication.dao;

import myBankApplication.beans.Account;
import myBankApplication.beans.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDao extends JpaRepository<Transaction,Integer> {

    Transaction findById(int transactionId);
}
