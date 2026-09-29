package myBankApplication.config;

import myBankApplication.BL.UserBL;
import myBankApplication.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// יוצר את משתמש המנהל בהפעלת השרת אם הוא עדיין לא קיים.
// שם המשתמש חייב להיות ADMIN - לפיו CustomUserDetailsService נותן ROLE_ADMIN.
@Component
public class AdminInitializer implements ApplicationRunner {

    public static final String ADMIN_USER_NAME = "ADMIN";

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserBL userBL;
    private final UserDAO userDAO;
    private final String adminPassword;

    public AdminInitializer(UserBL userBL, UserDAO userDAO, @Value("${bank.admin.password:}") String adminPassword) {
        this.userBL = userBL;
        this.userDAO = userDAO;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userDAO.findByUserName(ADMIN_USER_NAME) != null) {
            return;
        }
        if (adminPassword == null || adminPassword.isBlank()) {
            log.warn("No admin user exists and bank.admin.password is not set - skipping admin creation");
            return;
        }
        userBL.createAdmin(ADMIN_USER_NAME, adminPassword);
        log.info("Created admin user '{}'", ADMIN_USER_NAME);
    }
}
