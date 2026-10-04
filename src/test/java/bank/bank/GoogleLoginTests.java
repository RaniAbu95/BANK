package myBankApplication;

import myBankApplication.beans.User;
import myBankApplication.dao.CustomerDAO;
import myBankApplication.dao.UserDAO;
import myBankApplication.exceptions.GoogleTokenErrorException;
import myBankApplication.services.GoogleTokenVerifier;
import myBankApplication.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// מאמת את /login/google כש-GoogleTokenVerifier מוחלף בדמה (בלי פנייה אמיתית ל-Google)
@SpringBootTest(classes = BankApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class GoogleLoginTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private GoogleTokenVerifier googleTokenVerifier;

    private String login(String credential) throws Exception {
        return mockMvc.perform(post("/login/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"credential\":\"" + credential + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void firstLoginCreatesVerifiedUserAndSecondLoginReusesIt() throws Exception {
        when(googleTokenVerifier.verifiedEmail("new-token")).thenReturn("new.google@example.com");

        String token = login("new-token");

        User user = userDAO.findFirstByEmail("new.google@example.com");
        assertNotNull(user);
        assertEquals("new.google", jwtUtil.extractUsername(token));
        assertEquals("EmailVerfiyed", user.getEmailVerify());
        assertEquals(1, customerDAO.findFirstByUsername("new.google").getAccounts().size());

        assertEquals("new.google", jwtUtil.extractUsername(login("new-token")));
    }

    @Test
    void takenUserNameGetsSuffix() throws Exception {
        userDAO.save(new User("taken", "pw", "ROLE_USER", "", "someone.else@example.com"));
        when(googleTokenVerifier.verifiedEmail("taken-token")).thenReturn("taken@example.com");

        assertEquals("taken1", jwtUtil.extractUsername(login("taken-token")));
    }

    @Test
    void existingUnverifiedEmailIsRejected() throws Exception {
        userDAO.save(new User("unverified", "pw", "ROLE_USER", "", "unverified@example.com"));
        when(googleTokenVerifier.verifiedEmail("unverified-token")).thenReturn("unverified@example.com");

        mockMvc.perform(post("/login/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"credential\":\"unverified-token\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidTokenIsUnauthorized() throws Exception {
        when(googleTokenVerifier.verifiedEmail("bad-token")).thenThrow(new GoogleTokenErrorException());

        mockMvc.perform(post("/login/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"credential\":\"bad-token\"}"))
                .andExpect(status().isUnauthorized());
    }
}
