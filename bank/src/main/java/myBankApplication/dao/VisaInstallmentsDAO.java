package myBankApplication.dao;

import myBankApplication.beans.Transaction;
import myBankApplication.beans.VisaCard;
import myBankApplication.beans.VisaInstallments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisaInstallmentsDAO extends JpaRepository<VisaInstallments,Integer> {
    VisaInstallments findById(int visaInstallmentsId);


}
