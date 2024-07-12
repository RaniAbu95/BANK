package myBankApplication.beans;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountId;

    @Column(name="balance")
    private String balance;
    @Column(name="category")
    private String category;
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


    public Account(String balance, String category, String password) {
        this.balance = balance;
        this.category = category;
        this.Password = password;

    }

    public Account() {

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

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }


}
