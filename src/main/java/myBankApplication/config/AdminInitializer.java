package myBankApplication.config;

import myBankApplication.BL.UserBL;
import myBankApplication.beans.Banker;
import myBankApplication.dao.BankerDAO;
import myBankApplication.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// יוצר את משתמש המנהל בהפעלת השרת אם הוא עדיין לא קיים.
// שם המשתמש חייב להיות ADMIN - לפיו CustomUserDetailsService נותן ROLE_ADMIN.
// בנוסף יוצר בנקאי ברירת מחדל כשאין אף בנקאי, כי בלי בנקאי אי אפשר לפתוח חשבון בהרשמה.
@Component
public class AdminInitializer implements ApplicationRunner {

    public static final String ADMIN_USER_NAME = "ADMIN";

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private final UserBL userBL;
    private final UserDAO userDAO;
    private final BankerDAO bankerDAO;
    private final String adminPassword;
    private final String defaultBankerName;
    private final String defaultBankerEmail;

    public AdminInitializer(UserBL userBL, UserDAO userDAO, BankerDAO bankerDAO,
                            @Value("${bank.admin.password:}") String adminPassword,
                            @Value("${bank.default-banker.name:Default Banker}") String defaultBankerName,
                            @Value("${bank.default-banker.email:banker@bank.local}") String defaultBankerEmail) {
        this.userBL = userBL;
        this.userDAO = userDAO;
        this.bankerDAO = bankerDAO;
        this.adminPassword = adminPassword;
        this.defaultBankerName = defaultBankerName;
        this.defaultBankerEmail = defaultBankerEmail;
    }

    @Override
    public void run(ApplicationArguments args) {
        createAdmin();
        createDefaultBanker();
    }

    private void createAdmin() {
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

    private void createDefaultBanker() {
        if (bankerDAO.count() > 0) {
            return;
        }
        bankerDAO.save(new Banker(defaultBankerName, defaultBankerEmail));
        log.info("Created default banker '{}'", defaultBankerName);
    }
}
