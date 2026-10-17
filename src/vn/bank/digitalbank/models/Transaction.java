package vn.bank.digitalbank.models;

import vn.bank.digitalbank.commons.TransactionType;
import vn.bank.digitalbank.commons.Utils;

public class Transaction {

    private final TransactionType type;
    private final String accountNumber;
    private final double amount;
    private final String time;
    private final String status;

    /**
     * Create Transaction
     */
    public Transaction(TransactionType type, String accountNumber, double amount, String time, String status) {
        this.type = type;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.time = Utils.getDateTime();
        this.status = status;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getAmount(TransactionType type) {
        if(type == TransactionType.DEPOSIT) {
            return amount;
        } else {
            return -amount;
        }
    }

    public String getTime() {
        return time;
    }

    public String status() {
        return status;
    }

    public TransactionType getType() {
        return type;
    }

    @Override
    public String toString() {
        if(type == TransactionType.WITHDRAW
            || type == TransactionType.TRANSFER ) {
            return String.format("%10s | %10s | %30s |%30s",
                    "[GD] " + getAccountNumber(),
                    type,
                    "-" + Utils.formatBalance(amount) + " đ",
                    time);
        } else {
            return String.format("%10s | %10s | %30s |%30s",
                    "[GD] " + getAccountNumber(),
                    type,
                    Utils.formatBalance(amount) + " đ",
                    time);
        }
    }
}
