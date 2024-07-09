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
    private float amount;

    @Column(name="interset_rate")
    private float intersetRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;



    public Loan(){}
    public Loan(int loanId, String loanType, float amount, float intersetRate) {
        this.loanId = loanId;
        this.loanType = loanType;
        this.amount = amount;
        this.intersetRate = intersetRate;
        //this.account = null; // change this
    }


//    public Account getAccount() {
//        return account;
//    }
//
//    public void setAccount(Account account) {
//        this.account = account;
//    }

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

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getIntersetRate() {
        return intersetRate;
    }

    public void setIntersetRate(float intersetRate) {
        this.intersetRate = intersetRate;
    }

    public void setId(int id) {
        this.loanId = id;
    }

    public int getId() {
        return loanId;
    }
}
