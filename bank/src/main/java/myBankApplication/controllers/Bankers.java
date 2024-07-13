package myBankApplication.controllers;

import myBankApplication.BL.AccountBL;
import myBankApplication.BL.BankerBL;
import myBankApplication.beans.Account;
import myBankApplication.beans.Banker;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;


@RestController
@RequestMapping("/bankers")
public class Bankers {
        @Autowired
        private AccountBL accountBL;

        @Autowired
        private BankerBL bankerBL;

        @PostMapping("add")
        public String add(String name, String email) throws BankerEmailErrorException, BankerNameErrorException
        {
            Banker banker = new Banker(name, email);
            bankerBL.addNewBanker(banker);
            return banker.toString();
        }

        @GetMapping("get/{bankerId}")
        public String getBanker(@PathVariable int bankerId) throws AccountNotFoundException {
            Banker banker = bankerBL.getBanker(bankerId);
            return banker.toString();
        }

        @GetMapping("getAll")
        public List<Banker> getAllBankers()  {
            return bankerBL.getAllBankers();
        }


        @PutMapping("update/{bankerId}")
        public String updateBankerAccounts(@PathVariable int bankerId) throws AccountNotFoundException {
            Banker newBanker = this.bankerBL.updateBankerAccounts(bankerId);
            return newBanker.toString();
        }
    }

