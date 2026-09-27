package myBankApplication.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.AccountNotFoundException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

// מתרגם חריגות לתשובת JSON בפורמט של Spring ({ timestamp, status, error, message, path })
// עם סטטוס מתאים והודעה ברורה, במקום שהבקשה תגיע ל-/error ותחזור כ-403.
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String DOMAIN_PACKAGE = "myBankApplication.exceptions";

    private static final Map<String, String> MESSAGES = Map.ofEntries(
            Map.entry("UserUserNameErrorException", "חובה להזין שם משתמש"),
            Map.entry("UserPasswordErrorException", "חובה להזין סיסמה"),
            Map.entry("UserNotFoundException", "המשתמש לא נמצא"),
            Map.entry("UseerNotSavedInDataBaseErrorException", "שמירת המשתמש נכשלה"),
            Map.entry("CustomerIdErrorException", "תעודת הזהות אינה תקינה"),
            Map.entry("CustomerEmailErrorException", "כתובת הדוא\"ל אינה תקינה"),
            Map.entry("CustomerEmailUnVerfiyedErrorException", "כתובת הדוא\"ל של הלקוח עדיין לא אומתה"),
            Map.entry("CustomerLocationErrorException", "הכתובת אינה תקינה"),
            Map.entry("CustomerNotFoundException", "הלקוח לא נמצא"),
            Map.entry("CustomerIsNotExistException", "הלקוח לא קיים"),
            Map.entry("CustomerNotSavedInDataBaseErrorException", "שמירת הלקוח נכשלה"),
            Map.entry("CompanyErrorException", "פרטי החברה אינם תקינים"),
            Map.entry("AccountBalanceErrorException", "היתרה בחשבון אינה תקינה"),
            Map.entry("AccountCategoryErrorException", "סוג החשבון אינו תקין"),
            Map.entry("AccountPasswordErrorException", "סיסמת החשבון אינה תקינה"),
            Map.entry("AccountsAlreadyExistException", "החשבון כבר קיים"),
            Map.entry("AccountNotSavedInDataBaseErrorException", "שמירת החשבון נכשלה"),
            Map.entry("AccountNotFoundException", "החשבון לא נמצא"),
            Map.entry("BankerNameErrorException", "שם הבנקאי אינו תקין"),
            Map.entry("BankerEmailErrorException", "כתובת הדוא\"ל של הבנקאי אינה תקינה"),
            Map.entry("BankerNotFoundException", "הבנקאי לא נמצא"),
            Map.entry("BankerNotSavedInDataBaseErrorException", "שמירת הבנקאי נכשלה"),
            Map.entry("LoanAmountErrorException", "סכום ההלוואה אינו תקין"),
            Map.entry("LoanTypeErrorException", "סוג ההלוואה אינו תקין"),
            Map.entry("LoanAlreadyExist", "ההלוואה כבר קיימת"),
            Map.entry("LoanAlreadyExistException", "ההלוואה כבר קיימת"),
            Map.entry("LoanNotFoundException", "ההלוואה לא נמצאה"),
            Map.entry("businessLoanAmounLessThan10k", "הלוואה עסקית חייבת להיות לפחות 10,000"),
            Map.entry("IntersetRateErrorException", "הריבית אינה תקינה"),
            Map.entry("StatusErrorException", "הסטטוס אינו תקין"),
            Map.entry("LimitErrorException", "חריגה מהמסגרת המותרת"),
            Map.entry("operationErrorException", "הפעולה אינה תקינה"),
            Map.entry("TransactionAmountNotFoundErrorException", "סכום הפעולה אינו תקין"),
            Map.entry("TransactionOperationNotFoundErrorException", "סוג הפעולה אינו תקין"),
            Map.entry("TransactionTargetNotFoundErrorException", "חשבון היעד אינו תקין"),
            Map.entry("TransactionTimestampNotFoundErrorException", "מועד הפעולה אינו תקין"),
            Map.entry("TimeStampErrorException", "המועד אינו תקין"),
            Map.entry("TransactionAlreadyExistException", "הפעולה כבר קיימת"),
            Map.entry("TransactionNotFoundException", "הפעולה לא נמצאה"),
            Map.entry("TransactionNotSavedInDatabase", "שמירת הפעולה נכשלה"),
            Map.entry("VisaCardNumberErrorException", "מספר הכרטיס אינו תקין"),
            Map.entry("VVCErrorException", "קוד האבטחה של הכרטיס אינו תקין"),
            Map.entry("ExpiredDateErrorException", "תוקף הכרטיס אינו תקין"),
            Map.entry("VisaCardAlreadyExistException", "הכרטיס כבר קיים"),
            Map.entry("VisaCardNotFoundException", "הכרטיס לא נמצא"),
            Map.entry("VisaInstallmentsNotSavedInDatabase", "שמירת התשלומים נכשלה")
    );

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        return body(HttpStatus.UNAUTHORIZED, "שם משתמש או סיסמה שגויים", request);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<Map<String, Object>> handleMail(MailException ex, HttpServletRequest request) {
        log.error("Mail sending failed on {} {}", request.getMethod(), request.getRequestURI(), ex);
        return body(HttpStatus.SERVICE_UNAVAILABLE, "שליחת המייל נכשלה, נסו שוב מאוחר יותר", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handle(Exception ex, HttpServletRequest request) {
        // חריגות של Spring MVC (פרמטר חסר, JSON שבור וכו') כבר נושאות סטטוס משלהן
        if (ex instanceof ErrorResponse errorResponse) {
            HttpStatusCode status = errorResponse.getStatusCode();
            String detail = errorResponse.getBody().getDetail();
            return body(status, detail != null ? detail : "בקשה לא תקינה", request);
        }

        String name = ex.getClass().getSimpleName();
        boolean domain = ex.getClass().getPackageName().equals(DOMAIN_PACKAGE) || ex instanceof AccountNotFoundException;
        if (!domain) {
            log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
            return body(HttpStatus.INTERNAL_SERVER_ERROR, "אירעה שגיאה בשרת", request);
        }

        HttpStatus status = statusFor(name);
        if (status.is5xxServerError()) {
            log.error("{} on {} {}", name, request.getMethod(), request.getRequestURI(), ex);
        }
        return body(status, MESSAGES.getOrDefault(name, "בקשה לא תקינה (" + name + ")"), request);
    }

    private static HttpStatus statusFor(String name) {
        if (name.contains("NotSaved")) return HttpStatus.INTERNAL_SERVER_ERROR;
        // Transaction*NotFoundErrorException הן שגיאות ולידציה (שדה חסר), לא "משאב לא נמצא"
        if (name.contains("NotFoundError")) return HttpStatus.BAD_REQUEST;
        if (name.contains("NotFound") || name.contains("IsNotExist")) return HttpStatus.NOT_FOUND;
        if (name.contains("AlreadyExist")) return HttpStatus.CONFLICT;
        return HttpStatus.BAD_REQUEST;
    }

    private static ResponseEntity<Map<String, Object>> body(HttpStatusCode status, String message, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        HttpStatus resolved = HttpStatus.resolve(status.value());
        body.put("error", resolved != null ? resolved.getReasonPhrase() : "Error");
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
