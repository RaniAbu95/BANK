package myBankApplication.dao;

import myBankApplication.beans.Account;
import myBankApplication.beans.Customer;
import myBankApplication.beans.Loan;
import myBankApplication.beans.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanDAO  extends JpaRepository<Loan, Integer> {
    Loan findById(int loanId);
    List<Loan> findAllByAccount(Account account);

}
