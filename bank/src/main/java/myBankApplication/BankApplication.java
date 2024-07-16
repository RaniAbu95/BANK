
package myBankApplication;

import myBankApplication.exceptions.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.security.auth.login.AccountNotFoundException;

@SpringBootApplication
public class BankApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BankApplication.class, args);
		System.out.println("Hello world");
	}

}