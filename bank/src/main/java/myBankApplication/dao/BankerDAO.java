package myBankApplication.dao;

import jakarta.transaction.Transactional;
import myBankApplication.beans.Banker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BankerDAO  extends JpaRepository<Banker,Integer> {
    @Query(value = "SELECT * FROM bankers WHERE number_of_accounts = (SELECT MIN(number_of_accounts) FROM bankers) LIMIT 1", nativeQuery = true)
    Banker findBankerWithMinAccounts();

    @Transactional
    @Modifying
    @Query(value = "UPDATE bankers SET number_of_accounts = number_of_accounts + 1 WHERE banker_id = :bankerId", nativeQuery = true)
    void incrementNumberOfAccounts(int bankerId);
}
