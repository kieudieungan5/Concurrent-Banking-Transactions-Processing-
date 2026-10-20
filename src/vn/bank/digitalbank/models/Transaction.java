package vn.bank.digitalbank.models;

import vn.bank.digitalbank.commons.TransactionStatus;
import vn.bank.digitalbank.commons.TransactionType;
import vn.bank.digitalbank.commons.Utils;

import java.math.BigDecimal;

public class Transaction {

    private final String id;
    private final TransactionType type;
    private final String accountDestination;
    private final String accountSource;
    private final BigDecimal amount;
    private final TransactionStatus status;

    public Transaction(TransactionType type,
                       String accountSource,
                       String accountDestination,
                       BigDecimal amount,
                       TransactionStatus status) {

        this.id = Utils.generateTransactionId();
        this.type = type;
        this.accountDestination = accountDestination;
        this.accountSource = accountSource;
        this.amount = amount;
        this.status = status;
    }

    public String getAccountDestination() {
        return accountDestination;
    }

    public String getAccountSource() {
        return accountSource;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionStatus status() {
        return status;
    }

    public TransactionType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

}
