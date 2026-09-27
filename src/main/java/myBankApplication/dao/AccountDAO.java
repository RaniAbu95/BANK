package myBankApplication.dao;


import myBankApplication.beans.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountDAO extends JpaRepository<Account,Integer> {


    @Query(value = "SELECT customer_id FROM accounts WHERE account_id = :accountId", nativeQuery = true)
    Integer findCustomerIdByAccountId(@Param("accountId") Integer accountId);
}
