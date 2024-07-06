package myBankApplication.dao;

import myBankApplication.beans.Customer;
import myBankApplication.beans.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanDAO  extends JpaRepository<Loan, Integer> {
    Customer findById(int customerId);

}
