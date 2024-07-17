package myBankApplication.dao;


import myBankApplication.beans.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AccountDAO extends JpaRepository<Account,Integer> {


}
