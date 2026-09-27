
package myBankApplication;

import jakarta.annotation.PostConstruct;
import myBankApplication.beans.User;
import myBankApplication.dao.UserDAO;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@EnableScheduling
public class BankApplication {

//	@Autowired
//	private UserDAO userDAO;
//
//	@PostConstruct
//	public void initUsers() {
//		List<User> users = Stream.of(
//				new User(101, "javatechie", "password", "javatechie@gmail.com"),
//				new User(102, "user1", "pwd1", "user1@gmail.com"),
//				new User(103, "user2", "pwd2", "user2@gmail.com"),
//				new User(104, "user3", "pwd3", "user3@gmail.com")
//		).collect(Collectors.toList());
//		userDAO.saveAll(users);
//	}

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BankApplication.class, args);
		System.out.println("Hello world");
	}

}