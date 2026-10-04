package myBankApplication.BL;

import myBankApplication.beans.*;
import myBankApplication.services.EmailService;
import myBankApplication.dao.CustomerDAO;

import myBankApplication.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class CustomerBL {

    private static final Logger log = LoggerFactory.getLogger(CustomerBL.class);

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private AccountBL accountBL;

    @Autowired
    private BankerBL bankerBL;

    @Autowired
    private UserBL userBL;


    @Autowired
    private EmailService emailService;

    public void checkCustomer(Customer customer) throws CustomerIsNotExistException, CustomerEmailErrorException, CustomerIdErrorException, CustomerLocationErrorException {
        Optional<Customer> existingCustomer = this.customerDAO.findById(customer.getCustomerId());

        if(existingCustomer.isPresent()){
            throw new CustomerIsNotExistException();
        }
        if(customer.getEmail()==null){
            throw new CustomerEmailErrorException();
        }

        if(customer.getLocation() == null){
            throw new CustomerLocationErrorException();
        }
        if(customer.getCustomerId() == null){
            throw new CustomerIdErrorException();
        }

    }

    public Customer getCustomerByUserName(String userName) {
        return this.customerDAO.findFirstByUsername(userName);
    }

    public Customer getCustomer(int id) throws CustomerNotFoundException {
        Optional<Customer> customer = this.customerDAO.findById(id);
        if(customer.isPresent()){
            return customer.get();
        }
        throw new CustomerNotFoundException();
    }


    // הרשמה (/signup): יוצרים משתמש, לקוח מקושר (לפי שם משתמש) וחשבון בנק עם מספר חשבון.
    // הכול בטרנזקציה אחת — אם פתיחת החשבון נכשלת לא נשאר משתמש בלי חשבון
    @Transactional(rollbackFor = Exception.class)
    public Account registerUser(User user) throws UseerNotSavedInDataBaseErrorException, UserUserNameErrorException, UserPasswordErrorException, CustomerEmailErrorException, CustomerLocationErrorException, CustomerIdErrorException, CustomerIsNotExistException, CustomerNotSavedInDataBaseErrorException, AccountNotSavedInDataBaseErrorException, BankerNotSavedInDataBaseErrorException, NoBankerAvailableException {
        userBL.addNewUser(user);
        Customer customer = new Customer(user.getLocation(), user.getUserName(), user.getEmail(), user.getPassword());
        checkCustomer(customer);
        saveCustomerInDataBase(customer);
        return accountBL.openDefaultAccount(customer, user.getPassword());
    }

    // התחברות ראשונה עם Google: פותחים משתמש, לקוח וחשבון כמו בהרשמה רגילה.
    // שם המשתמש נגזר מהדוא"ל, והסיסמה אקראית — המשתמש נכנס דרך Google ולא עם סיסמה.
    // Google כבר אימתה את הדוא"ל, ולכן אין צורך בקוד אימות
    @Transactional(rollbackFor = Exception.class)
    public User registerGoogleUser(String email) throws UseerNotSavedInDataBaseErrorException, UserUserNameErrorException, UserPasswordErrorException, CustomerEmailErrorException, CustomerLocationErrorException, CustomerIdErrorException, CustomerIsNotExistException, CustomerNotSavedInDataBaseErrorException, AccountNotSavedInDataBaseErrorException, BankerNotSavedInDataBaseErrorException, NoBankerAvailableException {
        String userName = userBL.uniqueUserName(email.substring(0, email.indexOf('@')));
        User user = new User(userName, UUID.randomUUID().toString(), "ROLE_USER", "", email);
        Account account = registerUser(user);
        user.setEmailVerify("EmailVerfiyed");
        userBL.saveUserInDataBase(user);
        account.getCustomer().setEmailVerify("EmailVerfiyed");
        saveCustomerInDataBase(account.getCustomer());
        return user;
    }

    // משתמשים שנרשמו לפני שההרשמה פתחה חשבון אוטומטית מקבלים לקוח וחשבון בעליית השרת
    @EventListener(ApplicationReadyEvent.class)
    public void openAccountsForUsersWithoutAccount() {
        for (User user : userBL.getAllUsers()) {
            // ADMIN הוא משתמש הניהול (ראו CustomUserDetailsService) — אין לו חשבון בנק
            if (user.getUserName() == null || user.getUserName().equals("ADMIN")) {
                continue;
            }
            try {
                Customer customer = getCustomerByUserName(user.getUserName());
                if (customer == null) {
                    customer = new Customer(user.getLocation(), user.getUserName(), user.getEmail(), user.getPassword());
                    saveCustomerInDataBase(customer);
                } else if (customer.getAccounts() != null && !customer.getAccounts().isEmpty()) {
                    continue;
                }
                Account account = accountBL.openDefaultAccount(customer, user.getPassword());
                log.info("Opened account {} for existing user {}", account.getAccountNumber(), user.getUserName());
            } catch (Exception e) {
                log.error("Could not open an account for existing user {}", user.getUserName(), e);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Account addNewCustomer(Customer customer) throws CustomerEmailErrorException, CustomerLocationErrorException, CustomerIdErrorException, CustomerIsNotExistException, CustomerNotSavedInDataBaseErrorException, UseerNotSavedInDataBaseErrorException, UserUserNameErrorException, UserPasswordErrorException, AccountNotSavedInDataBaseErrorException, BankerNotSavedInDataBaseErrorException, NoBankerAvailableException {
        checkCustomer(customer);
        saveCustomerInDataBase(customer);
        Account account = accountBL.openDefaultAccount(customer, customer.getPassword());
        User user = new User();
        user.setUserName(customer.getUsername());
        user.setPassword(customer.getPassword());
        //should add create or add
        emailService.sendAccountEmail(customer.getEmail(), "Welcome", "Thank you for creating an account with us.");

        userBL.addNewUser(user);
        return account;
    }

    public void deleteCustomer(int customerId) throws CustomerIsNotExistException, AccountNotSavedInDataBaseErrorException, AccountNotFoundException, BankerNotFoundException, BankerNotSavedInDataBaseErrorException {
        Optional<Customer> existingCustomer = this.customerDAO.findById(customerId);
        if(!existingCustomer.isPresent()){
            throw new CustomerIsNotExistException();
        }

        List <Account> accountToSuspend = existingCustomer.get().getAccounts();
        for(Account account : accountToSuspend){

            //find bankerBy account id
            Banker responsibleBanker  = bankerBL.getBankerByAccountId(account.getAccountId());
            bankerBL.decrementBankerAccountsByOne(responsibleBanker.getBankerId());
            account.setStatus("Suspended");
            accountBL.saveAccountInDataBase(account);
        }
        existingCustomer.get().setStatus("Suspended");
        this.customerDAO.save(existingCustomer.get());
    }

    public CustomerDAO getCustomerDao() {
        return customerDAO;
    }

    public void setCustomerDoa(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers() throws CustomerNotFoundException {
        return this.customerDAO.findAll();
    }

    public Customer updateCustomerEmail(int customerId, String newEmail) throws CustomerNotFoundException, CustomerNotSavedInDataBaseErrorException {
        //check the email authintication
        Optional<Customer> customerToUpdate = this.customerDAO.findById(customerId);
        if(customerToUpdate.isPresent()){
            customerToUpdate.get().setEmail(newEmail);
            saveCustomerInDataBase(customerToUpdate.get());
            Optional<Customer> updatedCustomer = this.customerDAO.findById(customerId);
            return updatedCustomer.get();
        }
        else {
            throw new CustomerNotFoundException();

        }
    }

    public Customer updateCustomerLocation(int customerId, String newLocation) throws CustomerNotFoundException, CustomerNotSavedInDataBaseErrorException {
        //check the  authintication
        Optional<Customer> customerToUpdate = this.customerDAO.findById(customerId);
        if (customerToUpdate.isPresent()) {
            customerToUpdate.get().setLocation(newLocation);
            saveCustomerInDataBase(customerToUpdate.get());
            Optional<Customer> updatedCustomer = this.customerDAO.findById(customerId);
            return updatedCustomer.get();
        } else {
            throw new CustomerNotFoundException();
        }
    }

        public boolean saveCustomerInDataBase(Customer customer) throws CustomerNotSavedInDataBaseErrorException {
            try{
                this.customerDAO.save(customer);
                return true;
            }
            catch(Exception e){
                throw new CustomerNotSavedInDataBaseErrorException();
            }
        }


    public void emailVerfiyed(int customerId) throws CustomerNotSavedInDataBaseErrorException, CustomerNotFoundException {
        Optional<Customer> customerToUpdate = this.customerDAO.findById(customerId);
        if (customerToUpdate.isPresent()) {
            customerToUpdate.get().setEmailVerify("EmailVerfiyed");
            saveCustomerInDataBase(customerToUpdate.get());

        } else {
            throw new CustomerNotFoundException();
        }

    }


    public String getCustomerEmail(int customerId) throws CustomerNotFoundException {
        Optional<Customer> customer = this.customerDAO.findById(customerId);
        return customer.get().getEmail();
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void sendBalanceOutOfRangeEmail()  {
        List <Customer> customersList = customerDAO.findAll();
        for (Customer customer : customersList) {
            List<Account> accountList = customer.getAccounts();
            for (Account account : accountList) {
                if(account.getBalance()<0 ){
                    String email = customer.getEmail();
                    emailService.sendAccountEmail(email, "you're balance out of the range", "Sorry, Please check your account.");
                }
            }
        }
    }
}

