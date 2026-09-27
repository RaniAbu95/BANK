package myBankApplication.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "visa_cards")
public class VisaCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int visaCardId;
    @Column(name = "visaCardNumber")
    private long visaCardNumber;
    @Column(name = "expiredDate")
    private String expiredDate;
    @Column(name = "vvc")
    private int cvv;
    @Column(name = "company")
    private String company;
    @Column(name = "credit")
    private int limit;
    @Column(name = "Status")
    private String status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AccountId")
    private Account account;

    @OneToMany(mappedBy = "visaCard", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("visaCard")
    private List<VisaInstallments> visaInstallments;




    public VisaCard(){}

    public VisaCard(Account account, String status, String company, int cvv, String expiredDate, long visaCardNumber, int limit) {
        this.account = account;
        this.status = status;
        this.company = company;
        this.cvv = cvv;
        this.expiredDate = expiredDate;
        this.visaCardNumber = visaCardNumber;
        this.limit = limit;
    }

    public int getVisaCardId() {
        return visaCardId;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public long getVisaCardNumber() {
        return visaCardNumber;
    }

    public void setVisaCardNumber(long visaCardNumber) {
        this.visaCardNumber = visaCardNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
