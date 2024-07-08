package myBankApplication.BL;

import myBankApplication.beans.Customer;
import myBankApplication.dao.CustomerDAO;

import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CustomerBL {

    @Autowired
    private CustomerDAO customerDAO;
    public boolean checkCustomer(Customer customer) throws CustomerIsNotExistException, CustomerEmailErrorException, CustomerIdErrorException, CustomerLocationErrorException {
        Optional<Customer> existingCustomer = this.customerDAO.findById(customer.getCustomerId());

        if(existingCustomer.isPresent()){
            throw new CustomerIsNotExistException();
        }
        if(customer.getEmail()==null){
            throw new CustomerEmailErrorException();
        }

        if(customer.getLocation() == null){
            throw new CustomerLocationErrorException();
        }
        if(customer.getCustomerId() == null){
            throw new CustomerIdErrorException();
        }

        return true;
    }


    public void addNewCustomer(Customer customer) throws CustomerEmailErrorException, CustomerLocationErrorException, EmailErrorException, CustomerIdErrorException, CustomerIsNotExistException {
        if(checkCustomer(customer)){
            customerDAO.save(customer);
        }

    }

    public CustomerDAO getCustomerDoa() {
        return customerDAO;
    }

    public void setCustomerDoa(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }
}
