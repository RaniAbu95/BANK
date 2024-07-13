package myBankApplication.dao;

import jakarta.transaction.Transactional;
import myBankApplication.beans.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AccountDAO extends JpaRepository<Account,Integer> {

    //@Query(value = "select * from accounts", nativeQuery = true)
    //List<Account> getAllAccounts();

    @Transactional
    @Modifying
    @Query(value = "UPDATE accounts SET balance =:newBalance WHERE account_id = :accountId", nativeQuery = true)
    void updateAccountBalance(int accountId, int newBalance);


}
