package myBankApplication.controllers;


import myBankApplication.BL.CustomerBL;
import myBankApplication.beans.Customer;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerBL çustomerBL;
    @Autowired
    private CustomerBL customerBL;


    @PostMapping("add")
    public ResponseEntity<String> add(@RequestParam String location, @RequestParam String username,
                                      @RequestParam String email, @RequestParam String password) throws CustomerEmailErrorException, CustomerLocationErrorException, CustomerIdErrorException, CustomerIsNotExistException, CustomerNotSavedInDataBaseErrorException, UseerNotSavedInDataBaseErrorException, UserUserNameErrorException, UserPasswordErrorException {
        Customer customer = new Customer(location, username, email, password);
        çustomerBL.addNewCustomer(customer);
        return ResponseEntity.ok("Customer added successfully");
    }

    @GetMapping("get")
    public Customer getCustomer(@RequestParam int customerId) throws CustomerNotFoundException {
        return customerBL.getCustomer(customerId);
    }

    @GetMapping("getAll")
    public List<Customer> getAllAccounts() throws  CustomerNotFoundException {
       return customerBL.getAllCustomers();
    }

    @PutMapping("updateEmail")
        public ResponseEntity<String> updateCustomerEmail(@RequestParam int customerId, @RequestParam String newEmail) throws CustomerNotFoundException, CustomerNotSavedInDataBaseErrorException {
        this.customerBL.updateCustomerEmail(customerId, newEmail);
        return ResponseEntity.ok("Customer e-mail updated successfully");
    }

    @PutMapping("updateLocation")
    public ResponseEntity<String> updateCustomerLocation(@RequestParam int customerId, @RequestParam String newLocation) throws CustomerNotFoundException, CustomerNotSavedInDataBaseErrorException {
        this.customerBL.updateCustomerLocation(customerId, newLocation);
        return ResponseEntity.ok("Customer location updated successfully");
    }


    @PostMapping("delete")
    public ResponseEntity<String> delete(@RequestParam int customerId) throws CustomerEmailErrorException, CustomerLocationErrorException, CustomerIdErrorException, CustomerIsNotExistException, CustomerNotSavedInDataBaseErrorException, UseerNotSavedInDataBaseErrorException, UserUserNameErrorException, UserPasswordErrorException, AccountNotSavedInDataBaseErrorException, AccountNotFoundException, BankerNotSavedInDataBaseErrorException, BankerNotFoundException {
        çustomerBL.deleteCustomer(customerId);
        return ResponseEntity.ok("Customer suspended successfully");
    }


}
