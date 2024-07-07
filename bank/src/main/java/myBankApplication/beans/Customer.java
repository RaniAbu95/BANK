package myBankApplication.beans;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name="CustomerId")
    private int customerId;
    @Column(name="UserName")
    private String username;
    @Column(name="Email")
    private String email;
    @Column(name="Location")
    private String Location;


    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Account> accounts;


//    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
//    private List<Loan> loan;

    public Customer(){};
    public Customer(String location, String username, int customerId, String email) {
        Location = location;
        this.username = username;
        this.customerId = customerId;
        this.email = email;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
