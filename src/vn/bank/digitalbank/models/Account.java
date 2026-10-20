package vn.bank.digitalbank.models;

import java.math.BigDecimal;

public class Account {

    private final String customerId;
    private final String accountNumber;
    private BigDecimal balance;

    public Account(String customerId, String accountNumber, BigDecimal balance) {

        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.balance = balance;

    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return customerId + " | "
                + accountNumber + " | "
                + balance + "đ";
    }

}
