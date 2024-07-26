package myBankApplication.BL;

import myBankApplication.beans.Account;
import myBankApplication.beans.User;
import myBankApplication.dao.UserDAO;
import myBankApplication.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class UserBL {


    @Autowired
    private UserDAO userDAO ;


    @Autowired
    private PasswordEncoder passwordEncoder;


    public void checkUser(User user) throws UserUserNameErrorException, UserPasswordErrorException {
        //add logic to check user
        if (user==null){

        }

        if(user.getUserName() ==null){
            throw new UserUserNameErrorException();
        }
        if(user.getPassword() == null){
            throw new UserPasswordErrorException();
        }
    }


    public User addNewUser(User user) throws UseerNotSavedInDataBaseErrorException, UserUserNameErrorException, UserPasswordErrorException {
        checkUser(user);

        //add your email function here - check code before add to the DB

        //createUser(user.getUserName(), user.getPassword(),user.ge )
        saveUserInDataBase(user);



        return user;
    }

    public User getUser(int id) throws UserNotFoundException {
        Optional<User>user = this.userDAO.findById(id);
        if(user.isPresent()){
            return user.get();
        }
        throw new UserNotFoundException();
    }

    public List<User> getAllUsers() {
        return this.userDAO.findAll();
    }


    public boolean saveUserInDataBase(User user) throws  UseerNotSavedInDataBaseErrorException {
        try{
            this.userDAO.save(user);
            return true;
        }
        catch(Exception e){
            throw new UseerNotSavedInDataBaseErrorException();
        }
    }



    public Account updatePassword(int accountId) throws AccountNotFoundException, AccountNotSavedInDataBaseErrorException {
        return null;
    }


    public void createUser(String username, String password, List<String> roles) {
        User user = new User();
        user.setUserName(username);
        //user.setPassword(passwordEncoder.encode(password));
        user.setPassword(password);
        user.setRole(roles.get(1)); // Save roles in the user object
        userDAO.save(user);



    }

    public void createAdmin(String username, String password) {
        createUser(username, password, List.of("ADMIN"));
    }


}
