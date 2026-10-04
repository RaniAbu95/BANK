package myBankApplication.beans;

// גוף הבקשה ל-/login/google: טוקן הזהות (JWT) שכפתור Google מחזיר ללקוח
public record GoogleAuthRequest(String credential) {
}
