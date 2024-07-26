package myBankApplication.beans;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name="Transactions")
public class Transaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;
    @Column(name="TimeStamp")
    private String timeStamp;
    @Column(name="Operation")
    private String operation;
    @Column(name="Target")
    private Integer target;
    @Column(name="Amount")
    private double amount;
    @Column (name="foreigCurrencyToExchange")
    private String foreigCurrencyToExchange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "transaction")
    private CurrencyExchangeRate currencyExchangeRate;

    public Transaction() {
    }

    public Transaction( Integer target, String operation, String timeStamp,double amount,Account account) {
        this.target = target;
        this.operation = operation;
        this.timeStamp = timeStamp;
        this.amount=amount;
        this.account=account;
        this.foreigCurrencyToExchange=null;
    }


    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", timeStamp=" + timeStamp +
                ", operation='" + operation + '\'' +
                ", target='" + target + '\'' +
                ", account=" + account +
                '}';
    }

    public String getForeigCurrencyToExchange() {
        return foreigCurrencyToExchange;
    }

    public void setForeigCurrencyToExchange(String foreigCurrencyToExchange) {
        this.foreigCurrencyToExchange = foreigCurrencyToExchange;
    }

    public CurrencyExchangeRate getCurrencyExchangeRate() {
        return currencyExchangeRate;
    }

    public void setCurrencyExchangeRate(CurrencyExchangeRate currencyExchangeRate) {
        this.currencyExchangeRate = currencyExchangeRate;
    }
}