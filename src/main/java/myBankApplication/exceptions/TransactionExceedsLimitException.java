package myBankApplication.exceptions;

// יורש מ-AccountBalanceErrorException כדי שיעבור דרך כל חתימות ה-throws הקיימות
public class TransactionExceedsLimitException extends AccountBalanceErrorException{
}
