package myBankApplication.services;

import myBankApplication.beans.User;
import myBankApplication.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        // Create a list of roles or authorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(!user.getUserName().equals("ADMIN")){
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));// Example role
            user.setRole("ROLE_USER");

        }
        else{
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN")); // Example role
            user.setRole("ROLE_ADMIN");
        }
        userDAO.save(user);
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
    }
}
