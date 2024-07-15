package myBankApplication.controllers;


import myBankApplication.BL.CustomerBL;
import myBankApplication.beans.Customer;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class Customers {

    @Autowired
    private CustomerBL çustomerBL;
    @Autowired
    private CustomerBL customerBL;


    @PostMapping("add")
    public String add(String location, String username, String email)throws CustomerEmailErrorException, CustomerLocationErrorException, CustomerIdErrorException, CustomerIsNotExistException
    {
        Customer customer = new Customer(location, username, email);
        çustomerBL.addNewCustomer(customer);
        return customer.toString();
    }

    @GetMapping("get/{customerId}")
    public String getCustomer(@PathVariable int customerId) throws CustomerNotFoundException {
        Customer customer =customerBL.getCustomer(customerId);
        return customer.toString();
    }

    @GetMapping("getAll")
    public List<Customer> getAllAccounts() throws  CustomerNotFoundException {
       return customerBL.getAllCustomers();
    }

    @PutMapping("updateEmail")
        public String updateCustomerEmail(@RequestParam int customerId, @RequestParam String newEmail) throws CustomerNotFoundException {
        Customer customer =this.customerBL.updateCustomerEmail(customerId, newEmail);
        return customer.toString();
    }

    @PutMapping("updateLocation")
    public String updateCustomerLocation(@RequestParam int customerId, @RequestParam String newLocation) throws CustomerNotFoundException, InterruptedException {
        Customer customer = this.customerBL.updateCustomerLocation(customerId, newLocation);
        //wait(5);
        customer =customerBL.getCustomer(customerId);
        customer =customerBL.getCustomer(customerId);
        return customer.toString();

    }

}
