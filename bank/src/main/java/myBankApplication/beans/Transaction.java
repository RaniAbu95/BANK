package myBankApplication.beans;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="Transaction")
public class Transaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;
    @Column(name="TimeStamp")
    private DateTimeFormat timeStamp;
    @Column(name="Operation")
    private String operation;
    @Column(name="Target")
    private String target;



    public Transaction() {
    }

    public Transaction( String target, String operation, DateTimeFormat timeStamp) {
        this.target = target;
        this.operation = operation;
        this.timeStamp = timeStamp;
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

    public DateTimeFormat getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(DateTimeFormat timeStamp) {
        this.timeStamp = timeStamp;
    }
}