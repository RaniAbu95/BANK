package myBankApplication.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// נקודת קצה ציבורית וקלה: שירות ניטור (UptimeRobot) פונה אליה כדי שהשרת לא יירדם,
// והלקוח פונה אליה בטעינה כדי להעיר אותו מוקדם
@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}
