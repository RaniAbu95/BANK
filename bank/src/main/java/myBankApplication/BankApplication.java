
package myBankApplication;

import myBankApplication.exceptions.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BankApplication {

	public static void main(String[] args) throws AccountsAlreadyExistException, AccountBalanceErrorException, AccountPasswordErrorException, AccountCategoryErrorException, CustomerEmailErrorException, CustomerLocationErrorException, EmailErrorException, CustomerIdErrorException, CustomerIsNotExistException {

		ApplicationContext context = SpringApplication.run(BankApplication.class, args);
		System.out.println("Hello world");
		SystemManager manager = context.getBean(SystemManager.class);
		manager.run();

	}

}
