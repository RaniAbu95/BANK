package myBankApplication.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="customers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int customerId;

    @Column(name="UserName")
    private String username;
    @Column(name="Password")
    private String password;
    @Column(name="Email")
    private String email;
    @Column(name="Location")
    private String location;
    @Column (name="Status")
    private String status;
    @Column(name="EmailVerify")
    private String emailVerify;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer")
    @JsonIgnoreProperties
    private List<Account> accounts;






    public Customer(){};
    public Customer(String location, String username, String email, String password) {
        this.location = location;
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = "Active";
        this.emailVerify = "EmailUnVerify";
    }

    public String getStatus() {
        return status;
    }



    public void setStatus(String status) {
        this.status = status;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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




    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailVerify() {
        return emailVerify;
    }

    public void setEmailVerify(String emailVerify) {
        this.emailVerify = emailVerify;
    }
}
