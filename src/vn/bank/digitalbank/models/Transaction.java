package vn.bank.digitalbank.models;

import vn.bank.digitalbank.commons.TransactionType;
import vn.bank.digitalbank.commons.Utils;

public class Transaction {

    /**
     * Loại giao dịch
     */
    TransactionType type;

    /**
     * Mã GD, số TK GD, số tiền GD, thời gian GD, trạng thái GD
     */
    private final String accountNumber;
    private final double amount;
    private final String time;
    private final String status;

    /**
     * Khởi tạo đối tượng
     * @param amount số tiền GD
     * @param time thời gian GD
     * @param status trạng thái GD
     * @param type loại GD
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
