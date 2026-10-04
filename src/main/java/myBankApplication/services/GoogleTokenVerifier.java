package myBankApplication.services;

import myBankApplication.exceptions.GoogleLoginDisabledException;
import myBankApplication.exceptions.GoogleTokenErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

// מאמת את טוקן הזהות (ID token) שהלקוח מקבל מכפתור "התחברות עם Google":
// חתימה מול המפתחות הציבוריים של Google, תוקף, מנפיק, ושהטוקן הונפק לאתר שלנו (aud = ה-Client ID).
@Service
public class GoogleTokenVerifier {

    private static final String GOOGLE_JWK_SET_URI = "https://www.googleapis.com/oauth2/v3/certs";
    private static final Set<String> GOOGLE_ISSUERS = Set.of("https://accounts.google.com", "accounts.google.com");

    private final String clientId;
    private final NimbusJwtDecoder decoder;

    public GoogleTokenVerifier(@Value("${bank.google.client-id:}") String clientId) {
        this.clientId = clientId;
        this.decoder = NimbusJwtDecoder.withJwkSetUri(GOOGLE_JWK_SET_URI).build();
        this.decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(),
                new JwtClaimValidator<String>(JwtClaimNames.ISS, GOOGLE_ISSUERS::contains),
                new JwtClaimValidator<List<String>>(JwtClaimNames.AUD, aud -> aud != null && aud.contains(clientId))
        ));
    }

    /** מחזיר את כתובת הדוא"ל מהטוקן, רק אם הטוקן תקין ו-Google אימתה את הכתובת */
    public String verifiedEmail(String idToken) throws GoogleLoginDisabledException, GoogleTokenErrorException {
        if (clientId.isBlank()) {
            throw new GoogleLoginDisabledException();
        }
        if (idToken == null || idToken.isBlank()) {
            throw new GoogleTokenErrorException();
        }
        Jwt jwt;
        try {
            jwt = decoder.decode(idToken);
        } catch (JwtException e) {
            throw new GoogleTokenErrorException();
        }
        String email = jwt.getClaimAsString("email");
        if (email == null || !Boolean.TRUE.equals(jwt.getClaimAsBoolean("email_verified"))) {
            throw new GoogleTokenErrorException();
        }
        return email;
    }
}
