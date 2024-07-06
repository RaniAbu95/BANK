package myBankApplication.dao;

import myBankApplication.beans.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountDao extends JpaRepository<Account,Integer> {

    Account findById(int AccountId);





}
