package myBankApplication.dao;

import myBankApplication.beans.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDAO extends JpaRepository<Transaction,Integer> {

    Transaction findById(int transactionId);
}
