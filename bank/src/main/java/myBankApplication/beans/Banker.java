package myBankApplication.beans;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="bankers")
public class Banker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bankerId;
    @Column(name="name")
    private String balance;
    @Column(name="email")
    private String email;

    //
    @OneToMany(mappedBy = "banker", fetch = FetchType.LAZY)
    private List<Account> accounts;



    public Banker() {}
    public Banker(String balance, String email) {
        this.balance = balance;
        this.email = email;
    }

    public int getBankerId() {
        return bankerId;
    }

    public void setBankerId(int bankerId) {
        this.bankerId = bankerId;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
