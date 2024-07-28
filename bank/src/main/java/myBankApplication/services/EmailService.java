package myBankApplication.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service

public class EmailService {

    @Autowired
    private JavaMailSender mailSender;


    private Map<String, String> verificationCodes = new HashMap<>();

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }


    public void sendAccountEmail(String email, String subject, String text) {
        sendSimpleEmail(email, subject, text);
    }

    public void sendVerificationEmail(String email) {
        String code = generateVerificationCode();
        verificationCodes.put(email, code);

        String subject = "Email Verification";
        String text = "Your verification code is: " + code;
        sendSimpleEmail(email, subject, text);
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode.equals(code);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        return String.valueOf(code);
    }
}
