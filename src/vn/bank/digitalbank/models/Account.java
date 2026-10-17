package vn.bank.digitalbank.models;


import vn.bank.digitalbank.commons.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static vn.bank.digitalbank.commons.TransactionType.*;

public class Account {

    private final String customerId;
    private final String accountNumber;
    private double balance;
    private final String type;

    private final double BALANCE_PREMIUM = 10_000_000;

    private final double AMOUNT_TRANSACT_MINIMUM = 1_000;

    /**
     * AtomicLong: generates lock ids
     * lockOrderId: lock id of Account
     */
    private static final AtomicLong ACCOUNT_LOCK_ID = new AtomicLong(0);

    private final long lockOrderId = ACCOUNT_LOCK_ID.incrementAndGet();

    /**
     * List transactions of Account
     */
    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * Create Account
     * @param accountNumber account's number
     * @param balance amount in account
     */
    public Account(String customerId, String accountNumber, double balance, String type) {

        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.type =type;

    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public String getType() {
        return type;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Check if Account is Premium
     * @return true or false
     */
    public boolean isPremiumAccount() {

        if(balance >= BALANCE_PREMIUM) {
            return true;
        }
        return false;

    }

    /**
     * Display Account's information
     * @return string describe Account's information
     */
    @Override
    public String toString() {
        return customerId + " | "
                + accountNumber + " | "
                + type + " | " + "\t"
                + Utils.formatBalance(balance) + "đ";
    }

    /**
     * Withdraw from Account
     * @param amount amount to withdraw
     */
    public void withdraw(double amount) {
        synchronized(this) {
            if(!checkAmountNotValid(amount)) {
                balance -= amount;

                System.out.println("Giao dịch rút tài khoản "
                        + accountNumber
                        + " thành công "
                        + Utils.formatBalance(amount)
                        + " . Số dư hiện tại: "
                        + Utils.formatBalance(balance));
                transactions.add(new Transaction (WITHDRAW,
                        accountNumber,
                        amount,
                        Utils.getDateTime(),
                        "Thành công"));
            } else {
                System.out.println("Giao dịch rút tài khoản "
                        + accountNumber
                        + " không thành công "
                        + Utils.formatBalance(amount)
                        + " . Số dư hiện tại: "
                        + Utils.formatBalance(balance));
                transactions.add(new Transaction (WITHDRAW,
                        accountNumber,
                        amount,
                        Utils.getDateTime(),
                        "Không hợp lệ"));
            }
        }
    }

    /**
     * Transfer from one Account to another
     * @param amount to transfer
     * @param accountToReceive Account to receive amount
     */
    public void transfer(double amount,
                         Account accountToReceive) {

        // References to object lock
        Account first;
        Account second;

        // Object lock has order id less will be taken first
        if (this.lockOrderId < accountToReceive.lockOrderId) {
            first = this;
            second = accountToReceive;
        } else {
            first = accountToReceive;
            second = this;
        }

        // Thread takes 2 locks to assure atomic transfer between accounts
        // After completing, it will unlock locks
        synchronized(first) {
            synchronized(second) {
                if(checkAmountNotValid(amount)) {
                    System.out.println("Giao dịch chuyển "
                            + accountNumber
                            + " không thành công " + Utils.formatBalance(amount)
                            + " đến tài khoản "
                            + accountToReceive.getAccountNumber()
                            + " . Số dư hiện tại: "
                            + Utils.formatBalance(balance));
                    transactions.add(new Transaction (TRANSFER,
                            accountNumber,
                            amount,
                            Utils.getDateTime(),
                            "Không hợp lệ"));
                } else {
                    balance -= amount;
                    System.out.println("Giao dịch chuyển "
                            + accountNumber
                            + " thành công " + Utils.formatBalance(amount)
                            + " đến tài khoản "
                            + accountToReceive.getAccountNumber()
                            + " . Số dư hiện tại: "
                            + Utils.formatBalance(balance));
                    transactions.add(new Transaction(TRANSFER,
                            accountNumber,
                            amount,
                            Utils.getDateTime(),
                            "Thành công"));

                    accountToReceive.deposit(amount);
                    accountToReceive.getTransactions().add(new Transaction(DEPOSIT,
                            accountToReceive.getAccountNumber(),
                            amount,
                            Utils.getDateTime(),
                            "Thành công"));
                }
            }
        }
    }

    /**
     * Deposit to Account
     * @param amount to deposit
     */
    public void deposit(double amount) {
        synchronized(this) {
            if(!checkAmountDepositNotValid(amount)) {
                balance += amount;
                System.out.println("Giao dịch nhận "
                        + accountNumber
                        + " thành công "
                        + Utils.formatBalance(amount)
                        + " . Số dư hiện tại: "
                        + Utils.formatBalance(balance));
                transactions.add(new Transaction (DEPOSIT,
                        accountNumber,
                        amount,
                        Utils.getDateTime(),
                        "Thành công"));
            } else {
                System.out.println("Giao dịch nhận "
                        + accountNumber
                        + " không thành công "
                        + Utils.formatBalance(amount)
                        + " . Số dư hiện tại: "
                        + Utils.formatBalance(balance));
                transactions.add(new Transaction (DEPOSIT,
                        accountNumber,
                        amount,
                        Utils.getDateTime(),
                        "Không hợp lệ"));
            }
        }
    }

    /**
     * Check if amount to transfer/withdraw is valid
     * @param amount amount to transfer/withdraw
     * @return true if amount is not valid / false if amount is valid
     */
    public boolean checkAmountNotValid(double amount) {
        return amount % AMOUNT_TRANSACT_MINIMUM != 0 ||
                balance - amount < 0 ||
                amount <= 0;
    }

    /**
     * Check if amount to deposit is valid
     * @param amount amount to deposit
     * @return true if amount is not valid / false if amount is valid
     */
    public boolean checkAmountDepositNotValid(double amount) {
        return amount % AMOUNT_TRANSACT_MINIMUM != 0 ||
                amount <= 0;
    }

    public double getBALANCE_PREMIUM() {
        return BALANCE_PREMIUM;
    }
}
