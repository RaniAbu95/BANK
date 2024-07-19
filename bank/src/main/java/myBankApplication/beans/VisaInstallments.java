package myBankApplication.beans;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class VisaInstallments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int InstallmentsId;
    @Column(name="number_Of_Installments")
    private int numberOfInstallments;
    @Column(name="value_Of_Installments")
    private int valueOfInstallments;

    public VisaInstallments() {
    }

    public VisaInstallments(int installmentsId, int valueOfInstallments, int numberOfInstallments) {
        InstallmentsId = installmentsId;
        this.valueOfInstallments = valueOfInstallments;
        this.numberOfInstallments = numberOfInstallments;
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
