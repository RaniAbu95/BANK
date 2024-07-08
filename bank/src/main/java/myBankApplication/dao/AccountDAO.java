package myBankApplication.dao;

import myBankApplication.beans.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountDAO extends JpaRepository<Account,Integer> {





}
