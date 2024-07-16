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
    private String target;
    @Column(name="Amount")
    private int amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "transaction")
    private CurrencyExchangeRate currencyExchangeRate;

    public Transaction() {
    }

    public Transaction( String target, String operation, String timeStamp,int amount,Account account) {
        this.target = target;
        this.operation = operation;
        this.timeStamp = timeStamp;
        this.amount=amount;
        this.account=account;
    }


    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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
}