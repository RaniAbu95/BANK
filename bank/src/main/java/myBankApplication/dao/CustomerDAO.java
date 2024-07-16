package myBankApplication.dao;

import jakarta.transaction.Transactional;
import myBankApplication.beans.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerDAO extends JpaRepository<Customer, Integer> {
//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE customers SET email =:newEmail WHERE customer_id = :customerId", nativeQuery = true)
//    void updateCustomerEmail(int customerId, String newEmail);
//
//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE customers SET location =:newLocation WHERE customer_id = :customerId", nativeQuery = true)
//    void updateCustomerLocation(int customerId, String newLocation);
}
