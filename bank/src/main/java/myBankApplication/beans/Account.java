package myBankApplication.beans;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountId;

    @Column(name="balance")
    private int balance;
    @Column(name="category")
    private String category;
    @Getter
    @Column(name="password")
    private String Password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banker_id")
    private Banker banker;


    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Loan> loans;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Transaction> transactions;


    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<VisaCard> visaCards;


    public Account( String category, String password) {
        this.balance = 0;
        this.category = category;
        this.Password = password;

    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", balance='" + balance + '\'' +
                ", category='" + category + '\'' +
                ", Password='" + Password + '\'' +
                ", customer=" + this.customer +
//                ", banker=" + banker +
//                ", loans=" + loans +
//                ", transactions=" + transactions +
//                ", visaCards=" + visaCards +
                '}';
    }

    public Account() {

    }

    public String getPassword() {
        return Password;
    }

    public Banker getBanker() {
        return banker;
    }

    public void setBanker(Banker banker) {
        this.banker = banker;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    public List<VisaCard> getVisaCards() {
        return visaCards;
    }

    public void setVisaCards(List<VisaCard> visaCards) {
        this.visaCards = visaCards;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPassword(String password) {
        Password = password;
    }


}
