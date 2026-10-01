package myBankApplication.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service

public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // כשמוגדר מפתח, המיילים נשלחים דרך ה-API של Resend (HTTPS) במקום SMTP,
    // כי Render בחינם חוסם את יציאות ה-SMTP
    @Value("${bank.resend.api-key:}")
    private String resendApiKey;

    @Value("${bank.resend.from:}")
    private String resendFrom;

    private final RestClient resendClient = RestClient.create("https://api.resend.com");


    private Map<String, String> verificationCodes = new HashMap<>();

    public void sendSimpleEmail(String to, String subject, String text) {
        if (!resendApiKey.isBlank()) {
            sendWithResend(to, subject, text);
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    private void sendWithResend(String to, String subject, String text) {
        try {
            resendClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + resendApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "from", resendFrom.isBlank() ? "Bank <onboarding@resend.dev>" : resendFrom,
                            "to", List.of(to),
                            "subject", subject,
                            "text", text))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            // MailException כדי ש-GlobalExceptionHandler יחזיר את אותה תשובה כמו בכשל SMTP
            throw new MailSendException("Resend request failed: " + e.getMessage(), e);
        }
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
