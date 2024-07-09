package myBankApplication.dao;

import myBankApplication.beans.VisaCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisaCardDAO extends JpaRepository<VisaCard,Integer> {
    VisaCard findById(int visaCardId);
}
