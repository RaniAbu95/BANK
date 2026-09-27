package myBankApplication.beans;

public class OperationRequest {
    private Integer target;
    private String operation;
    private String timeStamp;
    private int amount;
    private String foreigCurrencyToExchange;

    // Getters and Setters
    public Integer getTarget() { return target; }
    public void setTarget(Integer target) { this.target = target; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getTimeStamp() { return timeStamp; }
    public void setTimeStamp(String timeStamp) { this.timeStamp = timeStamp; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getForeigCurrencyToExchange() {
        return foreigCurrencyToExchange;
    }

    public void setForeigCurrencyToExchange(String foreigCurrencyToExchange) {
        this.foreigCurrencyToExchange = foreigCurrencyToExchange;
    }


}
