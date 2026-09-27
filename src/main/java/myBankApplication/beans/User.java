package myBankApplication.beans;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name="user_name")
    private String userName;
    @Column(name="password")
    private String password;
    @Column(name="role")
    private String role;
    @Column(name="location")
    private String location;
    @Column(name="email")
    private String email;
    @Column(name="EmailVerify")
    private String emailVerify;

    public User() {
    }

    public User(String userName, String password, String role, String location, String email) {
        this.password = password;
        this.userName = userName;
        this.role = role;
        this.location = location;
        this.email = email;
        this.emailVerify = "EmailUnVerifiyed";

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getEmailVerify() {
        return emailVerify;
    }

    public void setEmailVerify(String emailVerify) {
        this.emailVerify = emailVerify;
    }
}
