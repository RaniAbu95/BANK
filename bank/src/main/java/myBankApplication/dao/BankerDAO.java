package myBankApplication.dao;

import myBankApplication.beans.Banker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankerDAO  extends JpaRepository<Banker,Integer> {

}
