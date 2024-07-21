package myBankApplication.controllers;

import myBankApplication.BL.CustomUserDetailsService;
import myBankApplication.BL.UserBL;
import myBankApplication.beans.AuthRequest;
import myBankApplication.beans.User;
import myBankApplication.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class WelcomeController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserBL userBl;

    @GetMapping("/user")
    public String welcome() {
        return "Welcome to javatechie !!";
    }

//    @PostMapping("/authenticate")
//    public String generateToken(@RequestBody AuthRequest authRequest) throws Exception {
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                    authRequest.getUserName(),
//                    authRequest.getPassword())
//            );
//        }
//        catch (Exception ex) {
//            throw new Exception("inavalid username/password");
//        }
//        // Load user details
//        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequest.getUserName());
//
//        int x = 5;
//        return jwtUtil.generateToken(userDetails);
//
//    }





}
