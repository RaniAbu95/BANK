package myBankApplication.BL;

import myBankApplication.beans.*;
import myBankApplication.dao.AccountDAO;
import myBankApplication.exceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;


import org.springframework.stereotype.Service;


import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountBL {

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


    public Account addNewAccount(Account account , int customerId) throws CustomerNotFoundException, AccountsAlreadyExistException, AccountPasswordErrorException, AccountCategoryErrorException, AccountNotSavedInDataBaseErrorException, BankerNotSavedInDataBaseErrorException, CustomerEmailUnVerfiyedErrorException {

        checkAccount(account,customerId);
        setRestrictionAmount(account);
        Banker responsibleBanker = bankerBL.getBankerWithMinAccounts();
        bankerBL.incrementBankerAccountsByOne(responsibleBanker.getBankerId());
        responsibleBanker.getAccounts().add(account);
        Customer customer = customerBL.getCustomer(customerId);
        account.setCustomer(customer);
        saveAccountInDataBase(account);
        return account;
    }

    public Account getAccount(int id) throws AccountNotFoundException {
        Optional<Account>account = this.accountDAO.findById(id);
        if(account.isPresent()){
            return account.get();
        }
        throw new AccountNotFoundException();
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
    }

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
            Transaction transaction =  transactionBL.createNewTransaction(null, "cashWithdrawal", timeStamp, amount,  accountId, "null");
            loan.setCompletedPayments(loan.getCompletedPayments() + 1);
            loanBL.saveLoanInDataBase(loan);
            double accountBalance = loan.getAccount().getBalance();
            updateAccountBalance(accountId, accountBalance - transaction.getAmount());
        }

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
