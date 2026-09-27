package myBankApplication.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
@Entity
@Table(name="Visa_Installments")
public class VisaInstallments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int InstallmentsId;
    @Column(name="number_Of_Installments")
    private int numberOfInstallments;
    @Column(name="value_Of_Installments")
    private int valueOfInstallments;
    @Column(name="visaCard_Installments_Id")
    private int visaCardInstallmentsId;
    @Column(name="numberOfPayments")
    private Integer numberOfPayments;
    @Column(name="instalmentCompleted")
    private Boolean instalmentCompleted;


    // Define the relationship with VisaCard
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visaCard_Installments_Id", referencedColumnName = "visaCardId", insertable = false, updatable = false)
    @JsonIgnoreProperties("visaInstallments")
    private VisaCard visaCard;


    public VisaInstallments() {
    }

    public VisaInstallments(int numberOfInstallments, int valueOfInstallments, int visaCardId) {
        this.numberOfInstallments = numberOfInstallments;
        this.valueOfInstallments = valueOfInstallments;
        this.visaCardInstallmentsId = visaCardId;
        this.numberOfPayments = 0;
        this.instalmentCompleted = Boolean.FALSE;
    }

    public int getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(int numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public int getValueOfInstallments() {
        return valueOfInstallments;
    }

    public void setValueOfInstallments(int valueOfInstallments) {
        this.valueOfInstallments = valueOfInstallments;
    }

    public int getInstallmentsId() {
        return InstallmentsId;
    }

    public void setInstallmentsId(int installmentsId) {
        InstallmentsId = installmentsId;
    }

    public int getVisaCardInstallmentsId() {
        return visaCardInstallmentsId;
    }

    public void setVisaCardInstallmentsId(int visaCardInstallmentsId) {
        this.visaCardInstallmentsId = visaCardInstallmentsId;
    }

    public int getNumberOfPayments() {
        return numberOfPayments;
    }

    public void setNumberOfPayments(int numberOfPayments) {
        this.numberOfPayments = numberOfPayments;
    }

    public VisaCard getVisaCard() {
        return visaCard;
    }

    public void setVisaCard(VisaCard visaCard) {
        this.visaCard = visaCard;
    }

    public Boolean isInstalmentCompleted() {
        return instalmentCompleted;
    }

    public void setInstalmentCompleted(boolean instalmentCompleted) {
        this.instalmentCompleted = instalmentCompleted;
    }
}
