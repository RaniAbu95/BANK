package myBankApplication.beans;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "visa_card")
public class VisaCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int visaCardId;
    @Column(name = "visaCardNumber")
    private long visaCardNumber;
    @Column(name = "expiredDate")
    private Date expiredDate;
    @Column(name = "vvc")
    private int vvc;
    @Column(name = "company")
    private String company;
    @Column(name = "credit")
    private int credit;
    @Column(name = "Status")
    private String status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AccountId")
    private Account account;



    public VisaCard(){}

    public VisaCard(String status, int limit, String company, int vvc, Date expiredDate, long visaCardNumber) {
        this.status = status;
        this.credit = limit;
        this.company = company;
        this.vvc = vvc;
        this.expiredDate = expiredDate;
        this.visaCardNumber = visaCardNumber;
    }

    public int getVisaCardId() {
        return visaCardId;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(int limit) {
        this.credit = limit;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Integer getVvc() {
        return vvc;
    }

    public void setVvc(int vvc) {
        this.vvc = vvc;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
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
}
