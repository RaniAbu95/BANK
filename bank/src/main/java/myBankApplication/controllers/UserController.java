package myBankApplication.controllers;


import myBankApplication.BL.UserBL;
import myBankApplication.beans.AuthRequest;
import myBankApplication.beans.User;
import myBankApplication.exceptions.CustomerNotFoundException;
import myBankApplication.exceptions.CustomerNotSavedInDataBaseErrorException;
import myBankApplication.exceptions.UseerNotSavedInDataBaseErrorException;
import myBankApplication.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;


import myBankApplication.services.CustomUserDetailsService;
import myBankApplication.util.JwtUtil;



@RestController
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserBL userBl;

    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody User user) throws Exception {
        userBl.addNewUser(user);
        emailService.sendVerificationEmail(user.getEmail());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public String generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getUserName(),
                    authRequest.getPassword())
            );
        }
        catch (Exception ex) {
            throw new Exception("inavalid username/password");
        }
        // Load user details
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequest.getUserName());

        return jwtUtil.generateToken(userDetails);

    }


    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String email, @RequestParam String code, @RequestParam int userId) throws CustomerNotSavedInDataBaseErrorException, CustomerNotFoundException, UseerNotSavedInDataBaseErrorException, UseerNotSavedInDataBaseErrorException, CustomerNotSavedInDataBaseErrorException, CustomerNotFoundException {
        if (emailService.verifyCode(email, code)) {
            userBl.emailVerfiyed(userId);
            return ResponseEntity.ok("Email verified successfully.");
        } else {
            return ResponseEntity.status(400).body("Verification failed. Invalid code.");
        }
    }
}
