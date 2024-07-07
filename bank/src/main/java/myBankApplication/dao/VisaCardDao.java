package myBankApplication.dao;

import myBankApplication.beans.Account;
import myBankApplication.beans.VisaCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisaCardDao extends JpaRepository<VisaCard,Integer> {
    VisaCard findById(int visaCardId);
}
