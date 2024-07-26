package myBankApplication.controllers;

import myBankApplication.BL.AccountBL;
import myBankApplication.BL.BankerBL;
import myBankApplication.beans.Banker;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;


@RestController
@RequestMapping("/bankers")
public class BankerController {
        @Autowired
        private AccountBL accountBL;

        @Autowired
        private BankerBL bankerBL;

        @PostMapping("add")
        public ResponseEntity<String> add(@RequestParam String name,@RequestParam String email) throws BankerEmailErrorException, BankerNameErrorException, BankerNotSavedInDataBaseErrorException {
            Banker banker = new Banker(name, email);
            bankerBL.addNewBanker(banker);
            return ResponseEntity.ok("Banker added successfully");
        }

        @GetMapping("get/{bankerId}")
        public Banker getBanker(@PathVariable int bankerId) throws AccountNotFoundException {
            return bankerBL.getBanker(bankerId);

        }

        @GetMapping("getAll")
        public List<Banker> getAllBankers()  {
            return bankerBL.getAllBankers();
        }


    }

