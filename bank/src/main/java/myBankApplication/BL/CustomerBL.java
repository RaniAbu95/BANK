package myBankApplication.BL;

import myBankApplication.beans.Account;
import myBankApplication.beans.Customer;
import myBankApplication.dao.CustomerDoa;

import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CustomerBL {

    @Autowired
    private CustomerDoa customerDoa;
    public Customer checkCustomer(Customer customer) throws CustomerIsNotExistException, EmailErrorException, CustomerEmailErrorException, CustomerIdErrorException, CustomerLocationErrorException {
        //have to take the parameters from customer opject to create new account


        Optional<Customer> existingCustomer= Optional.ofNullable(this.customerDoa.findById(customer.getId()));
        //Create intercase Dao
        //create findById function
        //create in class getId getter
        if(existingCustomer == null){
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

        return this.customerDoa.save(customer);
    }
    public Customer getCustomer(int id) throws CustomerNotFoundException {
        Optional<Customer>customer = Optional.ofNullable(this.customerDoa.findById(id));
        if(customer.isPresent()){
            return customer.get();
        }
        throw new CustomerNotFoundException();
    }


    public Customer createNewCustomer(Customer customer) throws CustomerEmailErrorException, CustomerLocationErrorException, EmailErrorException, CustomerIdErrorException, CustomerIsNotExistException, CustomerNotFoundException {

//        if(getCustomer(id).getCustomerId() ==null){
//            throw new CustomerNotFoundException();
//        }


        Customer customerToAdd = checkCustomer( customer);


        return  customerToAdd;
    }
}
