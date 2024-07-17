package myBankApplication.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="bankers")
public class Banker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bankerId;
    @Column(name="name")
    private String name;
    @Column(name="email")
    private String email;
    @Column (name="number_of_accounts")
    private int numberOfAccounts;

    //
    @OneToMany(mappedBy = "banker", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Account> accounts;



    public Banker(String name, String email) {
        this.name = name;
        this.email = email;
        this.numberOfAccounts = 0;
    }

    public Banker() {

    }

    public int getNumberOfAccounts() {
        return numberOfAccounts;
    }

    public void setNumberOfAccounts(int numberOfAccounts) {
        this.numberOfAccounts = numberOfAccounts;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public int getBankerId() {
        return bankerId;
    }

    public void setBankerId(int bankerId) {
        this.bankerId = bankerId;
    }

    public String getName() {
        return name;
    }

    public void setBalance(String balance) {
        this.name = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Banker{" +
                "bankerId=" + bankerId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", numberOfAccounts=" + numberOfAccounts +
                //", accounts=" + accounts +
                '}';
    }
}
