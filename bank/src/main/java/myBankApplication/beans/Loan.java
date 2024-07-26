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
    private int intersetRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    //to be removed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;



    public Loan(){}

    public Loan(Account account, int intersetRate, double amount, String loanType) {
        this.account = account;
        this.intersetRate = intersetRate;
        this.amount = amount;
        this.loanType = loanType;
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

    public int getIntersetRate() {
        return intersetRate;
    }

    public void setIntersetRate(int intersetRate) {
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
}
