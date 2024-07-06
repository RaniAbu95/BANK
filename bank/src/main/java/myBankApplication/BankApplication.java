
package myBankApplication;

import myBankApplication.exceptions.AccountBalanceErrorException;
import myBankApplication.exceptions.AccountCategoryErrorException;
import myBankApplication.exceptions.AccountPasswordErrorException;
import myBankApplication.exceptions.AccountsIsNotExistException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BankApplication {

	public static void main(String[] args) throws AccountsIsNotExistException, AccountBalanceErrorException, AccountPasswordErrorException, AccountCategoryErrorException {

		ApplicationContext context = SpringApplication.run(BankApplication.class, args);
		System.out.println("Hello world");
		SystemManager manager = context.getBean(SystemManager.class);
		manager.run();

	}

}
