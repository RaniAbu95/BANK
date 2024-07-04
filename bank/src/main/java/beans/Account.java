package beans;

import jakarta.persistence.*;

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

    @ManyToOne
    @JoinColumn(name = "Customer_id")
    private Customer customer;

    public Account(){};
    public Account(String balance, int accountId, String category, String password) {
        this.balance = balance;
        this.accountId = accountId;
        this.category = category;
        Password = password;
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
