package myBankApplication.dao;

import myBankApplication.beans.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User,Integer> {
    User findByUserName(String username);
}
