package myBankApplication.BL;

import myBankApplication.beans.Transaction;
import myBankApplication.beans.VisaCard;
import myBankApplication.beans.VisaInstallments;
import myBankApplication.dao.AccountDAO;
import myBankApplication.dao.VisaCardDAO;
import myBankApplication.dao.VisaInstallmentsDAO;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Optional;

@Service

public class VisaInstallmentsBL {
    @Autowired
    private VisaCardDAO visaCardDAO ;
    @Autowired
    private VisaInstallmentsDAO visaInstallmentsDAO;


    public VisaInstallments createNewVisaInstallments(int numberOfInstallments, int valueOfInstallments, int visaCardInstallmentsId) throws  VisaInstallmentsNotSavedInDatabase {

        VisaInstallments visaInstallments = new VisaInstallments(numberOfInstallments,valueOfInstallments,visaCardInstallmentsId);
        checkVisaInstallments(visaInstallments);
        return visaInstallments;
    }
    private void checkVisaInstallments(VisaInstallments visaInstallments) throws VisaInstallmentsNotSavedInDatabase {



        saveVisaInstallmentsInDatebase(visaInstallments);



    }
    public boolean saveVisaInstallmentsInDatebase(VisaInstallments visaInstallments) throws VisaInstallmentsNotSavedInDatabase {
        try {
            this.visaInstallmentsDAO.save(visaInstallments);
            return true;
        } catch (Exception e) {
            throw new VisaInstallmentsNotSavedInDatabase();
        }
    }
}
