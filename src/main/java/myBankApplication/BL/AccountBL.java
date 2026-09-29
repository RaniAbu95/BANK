package myBankApplication.BL;

import myBankApplication.beans.*;
import myBankApplication.dao.AccountDAO;
import myBankApplication.exceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.security.auth.login.AccountNotFoundException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountBL {

    // מספר חשבון בנק: 7 ספרות בדיוק (לא מתחיל ב-0)
    private static final int ACCOUNT_NUMBER_MIN = 1_000_000;
    private static final int ACCOUNT_NUMBER_MAX = 9_999_999;
    private static final int ACCOUNT_NUMBER_MAX_ATTEMPTS = 100;
    private static final SecureRandom random = new SecureRandom();

    // החשבון שנפתח אוטומטית למשתמש חדש: חשבון רגיל, יתרה 0 ומסגרת 10,000
    public static final String DEFAULT_CATEGORY = "Regular";

    @Autowired
    private AccountDAO accountDAO ;

    @Autowired
    @Lazy
    private TransactionBL transactionBL ;

    @Autowired
    @Lazy
    private CustomerBL customerBL;

    @Autowired
    @Lazy
    private VisaInstallmentsBL visaInstallmentsBL ;

    @Autowired
    @Lazy
    private BankerBL bankerBL;

    @Autowired
    @Lazy
    private LoanBL loanBL;

    public void checkAccount(Account account, int customerId) throws AccountsAlreadyExistException, AccountCategoryErrorException, AccountPasswordErrorException, CustomerNotFoundException, CustomerEmailUnVerfiyedErrorException {
        Optional<Account> existingAccount = this.accountDAO.findById(account.getAccountId());
        if (existingAccount.isPresent()) {
            throw new AccountsAlreadyExistException();
        }
        if(getCustomer( customerId) ==null){
            throw new CustomerNotFoundException();
        }
        if(account.getCategory() ==null){
            throw new AccountCategoryErrorException();
        }
        if(account.getPassword() == null){
            throw new AccountPasswordErrorException();
        }
        if(!customerBL.getCustomer(customerId).getEmailVerify().equals("EmailVerfiyed")){
            throw new CustomerEmailUnVerfiyedErrorException();
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public Account addNewAccount(Account account , int customerId) throws CustomerNotFoundException, AccountsAlreadyExistException, AccountPasswordErrorException, AccountCategoryErrorException, AccountNotSavedInDataBaseErrorException, BankerNotSavedInDataBaseErrorException, CustomerEmailUnVerfiyedErrorException, NoBankerAvailableException {

        checkAccount(account,customerId);
        return openAccount(account, customerBL.getCustomer(customerId));
    }

    // פתיחת חשבון ברירת מחדל ללקוח חדש בזמן ההרשמה (לפני אימות המייל, ולכן בלי checkAccount).
    // בטרנזקציה: גם מחוץ לבקשת HTTP (בעליית השרת) רשימת החשבונות של הבנקאי נטענת,
    // ואם השמירה נכשלת — גם הגדלת מונה החשבונות של הבנקאי מתבטלת
    @Transactional(rollbackFor = Exception.class)
    public Account openDefaultAccount(Customer customer, String password) throws AccountNotSavedInDataBaseErrorException, BankerNotSavedInDataBaseErrorException, NoBankerAvailableException {
        return openAccount(new Account(DEFAULT_CATEGORY, password), customer);
    }

    private Account openAccount(Account account, Customer customer) throws AccountNotSavedInDataBaseErrorException, BankerNotSavedInDataBaseErrorException, NoBankerAvailableException {
        setRestrictionAmount(account);
        account.setAccountNumber(generateAccountNumber());
        Banker responsibleBanker = bankerBL.getBankerWithMinAccounts();
        if (responsibleBanker == null) {
            throw new NoBankerAvailableException();
        }
        bankerBL.incrementBankerAccountsByOne(responsibleBanker.getBankerId());
        responsibleBanker.getAccounts().add(account);
        account.setBanker(responsibleBanker);
        account.setCustomer(customer);
        saveAccountInDataBase(account);
        return account;
    }

    public int generateAccountNumber() throws AccountNotSavedInDataBaseErrorException {
        for (int attempt = 0; attempt < ACCOUNT_NUMBER_MAX_ATTEMPTS; attempt++) {
            int candidate = ACCOUNT_NUMBER_MIN + random.nextInt(ACCOUNT_NUMBER_MAX - ACCOUNT_NUMBER_MIN + 1);
            if (!this.accountDAO.existsByAccountNumber(candidate)) {
                return candidate;
            }
        }
        throw new AccountNotSavedInDataBaseErrorException();
    }

    // חשבונות שנפתחו לפני שנוסף מספר החשבון מקבלים מספר בעליית השרת
    @EventListener(ApplicationReadyEvent.class)
    public void assignMissingAccountNumbers() throws AccountNotSavedInDataBaseErrorException {
        for (Account account : this.accountDAO.findByAccountNumberIsNull()) {
            account.setAccountNumber(generateAccountNumber());
            saveAccountInDataBase(account);
        }
    }

    public Account getAccount(int id) throws AccountNotFoundException {
        Optional<Account>account = this.accountDAO.findById(id);
        if(account.isPresent()){
            return account.get();
        }
        throw new AccountNotFoundException();
    }

    // משתמש רגיל רשאי לגשת רק לחשבונות של הלקוח שלו (מקושר לפי שם משתמש); מנהל — לכל חשבון
    public void checkAccountOwner(int accountId, Authentication authentication) throws AccountNotFoundException, AccountAccessDeniedException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return;
        }
        Customer owner = getAccount(accountId).getCustomer();
        if (owner == null || !authentication.getName().equals(owner.getUsername())) {
            throw new AccountAccessDeniedException();
        }
    }

    public int getAccountId(Account account) throws AccountNotFoundException {
        return account.getAccountId();
    }

    public List<Account> getAllAccounts() {
        return this.accountDAO.findAll();
    }

    public Customer getCustomer(int id) throws CustomerNotFoundException {
        return this.customerBL.getCustomer(id);
    }

    public double getAccountBalance(int accountId) throws AccountNotFoundException {
        Optional<Account> account = this.accountDAO.findById(accountId);
        if(account.isPresent()){
            return account.get().getBalance();
        }
        else {
            throw new AccountNotFoundException();
        }
    }


    public void updateAccountBalance(int accountId,double newBalance) throws AccountBalanceErrorException, AccountNotFoundException {
        Account accountToUpdate =getAccount(accountId);
//        if(accountToUpdate.getRestriction() > newBalance){
//            throw new AccountBalanceErrorException();
//        }
        accountToUpdate.setBalance(newBalance);
        this.accountDAO.save(accountToUpdate);
    }

    public boolean saveAccountInDataBase(Account account) throws AccountNotSavedInDataBaseErrorException {
        try{
            this.accountDAO.save(account);
            return true;
        }
        catch(Exception e){
            throw new AccountNotSavedInDataBaseErrorException();
        }
    }
    public BankerBL getBankerBl() {
        return this.bankerBL;
    }


    public Account updateStatusToSuspend(int accountId) throws AccountNotFoundException {
        Optional<Account> account = this.accountDAO.findById(accountId);
        if(account.isPresent()){
            Account accountToUpdate =account.get();
            accountToUpdate.setStatus("Suspend");
            this.accountDAO.save(accountToUpdate);

            return accountToUpdate;
        }
        throw new AccountNotFoundException();
    }


    public void setRestrictionAmount(Account account) {
        if(account.getCategory().equals("Saving")){
            account.setRestriction(-40000);
        }
        if(account.getCategory().equals("Buisness")){
            account.setRestriction(-60000);
        }
        if(account.getCategory().equals("Student")){
            account.setRestriction(-10000);
        }
        if(account.getCategory().equals(DEFAULT_CATEGORY)){
            account.setRestriction(-10000);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addNewPayment(String paymentType, String timeStamp , double amount, int accountId, VisaInstallments visaInstallments ,Loan loan) throws TransactionAlreadyExistException, TransactionTargetNotFoundErrorException, LoanAlreadyExistException, TransactionOperationNotFoundErrorException, LoanTypeErrorException, TransactionNotSavedInDatabase, AccountBalanceErrorException, LoanAmountErrorException, TransactionAmountNotFoundErrorException, businessLoanAmounLessThan10k, AccountNotFoundException, TransactionTimestampNotFoundErrorException, VisaInstallmentsNotSavedInDatabase {

        if(paymentType.equals("VisaInstallment")) {
            transactionBL.createNewTransaction(null, "cashWithdrawal", timeStamp, amount,  accountId, "null");
            visaInstallments.setNumberOfPayments(visaInstallments.getNumberOfPayments() + 1);
            if(visaInstallments.getNumberOfInstallments()-visaInstallments.getNumberOfPayments()==0 ) {
                visaInstallments.setInstalmentCompleted(Boolean.TRUE);
            }
            visaInstallmentsBL.saveVisaInstallmentsInDatebase(visaInstallments);
        }

        else if(paymentType.equals("LoanInstallment")){
            // פעולת המשיכה כבר מורידה את הסכום מהיתרה (ובודקת את המסגרת) — אין לעדכן את היתרה שוב
            transactionBL.createNewTransaction(null, "cashWithdrawal", timeStamp, amount,  accountId, "null");
            loan.setCompletedPayments(loan.getCompletedPayments() + 1);
            loanBL.saveLoanInDataBase(loan);
        }

    }

    // User and Customer are linked by user name (see CustomerBL.addNewCustomer)
    public List<Account> getAccountsByUserName(String userName) {
        Customer customer = this.customerBL.getCustomerByUserName(userName);
        if (customer == null || customer.getAccounts() == null) {
            return new ArrayList<>();
        }
        return customer.getAccounts();
    }

    public List<Transaction> getAllTransactions(int accountId) throws AccountNotFoundException {
        return this.transactionBL.getAllTransactions(accountId);
    }


    public List<Loan> getAllLoanTransactions(int accountId) throws AccountNotFoundException {
        return loanBL.getAllLoans(accountId);
    }

    public int getCustomerId(int accountId) throws AccountNotFoundException {
        return accountDAO.findCustomerIdByAccountId(accountId);
    }



}
