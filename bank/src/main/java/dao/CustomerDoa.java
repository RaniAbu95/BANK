package dao;

import beans.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerDoa extends JpaRepository<Customer, Integer> {
    Customer findById(int customerId);
}
