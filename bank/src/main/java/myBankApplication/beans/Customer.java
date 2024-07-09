package myBankApplication.beans;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerId;



    @Column(name="UserName")
    private String username;
    @Column(name="Email")
    private String email;
    @Column(name="Location")
    private String Location;


    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Account> accounts;


    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Loan> loans;

    public Customer(){};
    public Customer(String location, String username, String email) {
        Location = location;
        this.username = username;
        this.email = email;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Loan> getLoan() {
        return loans;
    }

    public void setLoan(List<Loan> loan) {
        this.loans = loan;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }


}
