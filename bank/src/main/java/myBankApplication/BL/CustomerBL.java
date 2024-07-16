package myBankApplication.BL;

import myBankApplication.beans.Account;
import myBankApplication.beans.Customer;
import myBankApplication.dao.CustomerDAO;

import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CustomerBL {

    @Autowired
    private CustomerDAO customerDAO;
    public void checkCustomer(Customer customer) throws CustomerIsNotExistException, CustomerEmailErrorException, CustomerIdErrorException, CustomerLocationErrorException {
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

    }

    public Customer getCustomer(int id) throws CustomerNotFoundException {
        Optional<Customer> customer = this.customerDAO.findById(id);
        if(customer.isPresent()){
            return customer.get();
        }
        throw new CustomerNotFoundException();
    }


    public void addNewCustomer(Customer customer) throws CustomerEmailErrorException, CustomerLocationErrorException, CustomerIdErrorException, CustomerIsNotExistException, CustomerNotSavedInDataBaseErrorException {
        checkCustomer(customer);
        saveCustomerInDataBase(customer);
    }

    public CustomerDAO getCustomerDao() {
        return customerDAO;
    }

    public void setCustomerDoa(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers() throws CustomerNotFoundException {
        return this.customerDAO.findAll();
    }

    public Customer updateCustomerEmail(int customerId, String newEmail) throws CustomerNotFoundException, CustomerNotSavedInDataBaseErrorException {
        //check the email authintication
        Optional<Customer> customerToUpdate = this.customerDAO.findById(customerId);
        if(customerToUpdate.isPresent()){
            //customerDAO.updateCustomerEmail(customerId,newEmail);
            customerToUpdate.get().setEmail(newEmail);
            saveCustomerInDataBase(customerToUpdate.get());
            return customerToUpdate.get();
        }
        else {
            throw new CustomerNotFoundException();

        }
    }

    public Customer updateCustomerLocation(int customerId, String newLocation) throws CustomerNotFoundException, CustomerNotSavedInDataBaseErrorException {
        //check the  authintication
        Optional<Customer> customerToUpdate = this.customerDAO.findById(customerId);
        if (customerToUpdate.isPresent()) {
            customerToUpdate.get().setLocation(newLocation);
            saveCustomerInDataBase(customerToUpdate.get());
            return customerToUpdate.get();
        } else {
            throw new CustomerNotFoundException();
        }
    }

        public boolean saveCustomerInDataBase(Customer customer) throws CustomerNotSavedInDataBaseErrorException {
            try{
                this.customerDAO.save(customer);
                return true;
            }
            catch(Exception e){
                throw new CustomerNotSavedInDataBaseErrorException();
            }
        }

    }

