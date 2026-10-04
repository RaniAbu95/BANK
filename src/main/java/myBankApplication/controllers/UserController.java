package myBankApplication.controllers;


import myBankApplication.BL.CustomerBL;
import myBankApplication.BL.UserBL;
import myBankApplication.beans.Account;
import myBankApplication.beans.AuthRequest;
import myBankApplication.beans.GoogleAuthRequest;
import myBankApplication.beans.User;
import myBankApplication.exceptions.CustomerNotFoundException;
import myBankApplication.exceptions.CustomerNotSavedInDataBaseErrorException;
import myBankApplication.exceptions.GoogleEmailUnVerifiedException;
import myBankApplication.exceptions.UseerNotSavedInDataBaseErrorException;
import myBankApplication.services.EmailService;
import myBankApplication.services.GoogleTokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;


import myBankApplication.services.CustomUserDetailsService;
import myBankApplication.util.JwtUtil;

import java.util.Map;



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
    private CustomerBL customerBL;

    @Autowired
    private EmailService emailService;

    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) throws Exception {
        // שולחים קודם את מייל האימות, כדי שכשל בשליחה לא ישאיר משתמש יתום ולא מאומת ב-DB
        emailService.sendVerificationEmail(user.getEmail());
        Account account = customerBL.registerUser(user);
        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "accountNumber", account.getAccountNumber()));
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
            throw new BadCredentialsException("inavalid username/password");
        }
        // Load user details
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequest.getUserName());

        return jwtUtil.generateToken(userDetails);

    }

    // התחברות עם Google: הלקוח שולח את טוקן הזהות שקיבל מ-Google ומקבל JWT של הבנק, כמו ב-/login.
    // משתמש שעוד אין לו חשבון עם הדוא"ל הזה נרשם אוטומטית
    @PostMapping("/login/google")
    public String googleLogin(@RequestBody GoogleAuthRequest request) throws Exception {
        String email = googleTokenVerifier.verifiedEmail(request.credential());
        User user = userBl.getUserByEmail(email);
        if (user == null) {
            user = customerBL.registerGoogleUser(email);
        } else if (!"EmailVerfiyed".equals(user.getEmailVerify())) {
            // מי שנרשם עם הדוא"ל הזה בלי לאמת אותו מחזיק בסיסמה של המשתמש —
            // לא מכניסים את בעל חשבון ה-Google לחשבון שמישהו אחר עלול לשלוט בו
            throw new GoogleEmailUnVerifiedException();
        }
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUserName());
        return jwtUtil.generateToken(userDetails);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String email, @RequestParam String code, @RequestParam(required = false) Integer userId) throws CustomerNotSavedInDataBaseErrorException, CustomerNotFoundException, UseerNotSavedInDataBaseErrorException, UseerNotSavedInDataBaseErrorException, CustomerNotSavedInDataBaseErrorException, CustomerNotFoundException {
        if (emailService.verifyCode(email, code)) {
            // userId is optional - the user is found by email, since signup doesn't return the id
            if (userId != null) {
                userBl.emailVerfiyed(userId);
            } else {
                userBl.emailVerfiyedByEmail(email);
            }
            return ResponseEntity.ok("Email verified successfully.");
        } else {
            return ResponseEntity.status(400).body("Verification failed. Invalid code.");
        }
    }
}
