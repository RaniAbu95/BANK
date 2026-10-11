package myBankApplication;

import myBankApplication.BL.TransactionBL;
import myBankApplication.beans.Account;
import myBankApplication.dao.AccountDAO;
import myBankApplication.dao.TransactionDAO;
import myBankApplication.exceptions.TransactionExceedsLimitException;
import myBankApplication.exceptions.TransactionTargetNotFoundErrorException;
import myBankApplication.exceptions.TransferToSameAccountException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// העברה בין חשבונות: היעד מזוהה לפי מספר החשבון בן 7 הספרות, לא לפי accountId
@SpringBootTest(classes = BankApplication.class)
@ActiveProfiles("test")
class TransferTests {

    private static final String NOW = "2026-10-11T00:00:00Z";

    @Autowired
    private TransactionBL transactionBL;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private TransactionDAO transactionDAO;

    private Account newAccount(int accountNumber, double balance) {
        Account account = new Account("Regular", "pw");
        account.setAccountNumber(accountNumber);
        account.setBalance(balance);
        account.setRestriction(-10000);
        return accountDAO.save(account);
    }

    private double balanceOf(Account account) {
        return accountDAO.findById(account.getAccountId()).orElseThrow().getBalance();
    }

    private int transactionCount(Account account) {
        return transactionDAO.findAllByAccount(account).size();
    }

    @Test
    void transferByAccountNumberMovesMoneyFromSourceToTarget() throws Exception {
        Account from = newAccount(1100001, 0);
        Account to = newAccount(1100002, 0);

        transactionBL.createNewTransaction(to.getAccountNumber(), "CashTransfare", NOW, 5000, from.getAccountId(), null);

        assertEquals(-5000, balanceOf(from));
        assertEquals(5000, balanceOf(to));
        assertEquals(1, transactionCount(from));
    }

    @Test
    void unknownTargetIsRejectedWithoutChangingAnything() throws Exception {
        Account from = newAccount(1100003, 100);

        assertThrows(TransactionTargetNotFoundErrorException.class, () ->
                transactionBL.createNewTransaction(9999999, "CashTransfare", NOW, 50, from.getAccountId(), null));

        assertEquals(100, balanceOf(from));
        assertEquals(0, transactionCount(from));
    }

    @Test
    void transferToTheSameAccountIsRejected() throws Exception {
        Account from = newAccount(1100004, 100);

        assertThrows(TransferToSameAccountException.class, () ->
                transactionBL.createNewTransaction(from.getAccountNumber(), "CashTransfare", NOW, 50, from.getAccountId(), null));

        assertEquals(100, balanceOf(from));
    }

    @Test
    void transferOverTheLimitChangesNeitherAccount() throws Exception {
        Account from = newAccount(1100005, 0);
        Account to = newAccount(1100006, 0);

        assertThrows(TransactionExceedsLimitException.class, () ->
                transactionBL.createNewTransaction(to.getAccountNumber(), "CashTransfare", NOW, 10001, from.getAccountId(), null));

        assertEquals(0, balanceOf(from));
        assertEquals(0, balanceOf(to));
        assertEquals(0, transactionCount(from));
    }
}
