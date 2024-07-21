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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VisaCard_Id")
    @JsonIgnoreProperties("Visa_Installments") // Ignore the 'accounts' field in Customer to break the circular reference
    private VisaCard visaCard;

    public VisaInstallments() {
    }

    public VisaInstallments(int numberOfInstallments, int valueOfInstallments, int visaCardId) {
        this.numberOfInstallments = numberOfInstallments;
        this.valueOfInstallments = valueOfInstallments;
        this.visaCardInstallmentsId = visaCardId;
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
}
