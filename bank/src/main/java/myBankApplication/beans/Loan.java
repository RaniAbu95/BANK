package myBankApplication.beans;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int loanId;

    @Column(name="loan_Type")
    private String loanType;

    @Column(name="amount")
    private double amount;

    @Column(name="interset_rate")
    private Integer intersetRate;


    @Column(name="completedPayments")
    private int completedPayments;

    @Column(name="numberOfPayments")
    private int numberOfPayments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;


    public Loan(){}

    public Loan(Account account, double amount) {
        this.account = account;
        this.amount = amount;
        this.completedPayments = 0;
        this.intersetRate = null;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Integer getIntersetRate() {
        return intersetRate;
    }

    public void setIntersetRate(Integer intersetRate) {
        this.intersetRate = intersetRate;
    }

    public void setId(int id) {
        this.loanId = id;
    }

    public int getId() {
        return loanId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getCompletedPayments() {
        return completedPayments;
    }

    public void setCompletedPayments(int completedPayments) {
        this.completedPayments = completedPayments;
    }

    public int getNumberOfPayments() {
        return numberOfPayments;
    }

    public void setNumberOfPayments(int numberOfPayments) {
        this.numberOfPayments = numberOfPayments;
    }


}
