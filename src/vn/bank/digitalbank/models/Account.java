package vn.bank.digitalbank.models;


import vn.bank.digitalbank.commons.Utils;

import java.util.ArrayList;
import java.util.List;

import static vn.bank.digitalbank.commons.TransactionType.*;

public class Account {

    private final String customerId;
    private final String accountNumber;
    private double balance;
    private final String type;

    private final double BALANCE_PREMIUM = 10_000_000;

    private final double AMOUNT_TRANSACT_MINIMUM = 1_000;

    /**
     * Danh sách giao dịch
     */
    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * Khởi tạo đối tượng Account
     * @param accountNumber số TK
     * @param balance số dư
     */
    public Account(String customerId, String accountNumber, double balance, String type) {

        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.type =type;

    }

    /**
     * Lấy mã CC
     * @return mã CC
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Lấy số TK
     * @return số TK
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Lấy số dư
     * @return số dư
     */
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
     * Kiểm tra TK có phải là hạng Premium không
     * @return true nếu là Premium, false nếu không phải
     */
    public boolean isPremiumAccount() {

        if(balance >= BALANCE_PREMIUM) {
            return true;
        }
        return false;

    }

    /**
     * Hiển thị TK và số dư
     * @return TK và số dư
     */
    @Override
    public String toString() {
        return customerId + " | "
                + accountNumber + " | "
                + type + " | " + "\t"
                + Utils.formatBalance(balance) + "đ";
    }

    /**
     * Lấy danh sách giao dịch của TK và hiển thị
     */
    public void displayTransactions() {

        // In giao dịch
        transactions.forEach(System.out::println);

    }

    public void withdraw(double amount) {
        if(!checkAmountNotValid(amount)) {
            balance -= amount;
            System.out.println("Giao dịch rút tài khoản "
                    + accountNumber
                    + " thành công "
                    + Utils.formatBalance(amount));
            transactions.add(new Transaction (WITHDRAW,
                    accountNumber,
                    amount,
                    Utils.getDateTime(),
                    "Thành công"));
        } else {
            System.out.println("Giao dịch rút tài khoản "
                    + accountNumber
                    + " không thành công "
                    + Utils.formatBalance(amount));
            transactions.add(new Transaction (WITHDRAW,
                    accountNumber,
                    amount,
                    Utils.getDateTime(),
                    "Không hợp lệ"));
        }
    }

    public void transfer(double amount,
                         Account accountToReceive) {
        if(checkAmountNotValid(amount)) {
            System.out.println("Giao dịch chuyển "
                    + accountNumber
                    + " không thành công " + Utils.formatBalance(amount)
                    + " đến tài khoản "
                    + accountToReceive.getAccountNumber());
            transactions.add(new Transaction (TRANSFER,
                    accountNumber,
                    amount,
                    Utils.getDateTime(),
                    "Không hợp lệ"));
        } else {
            balance -= amount;
            accountToReceive.deposit(amount);
            System.out.println("Giao dịch chuyển "
                    + accountNumber
                    + " thành công " + Utils.formatBalance(amount)
                    + " đến tài khoản "
                    + accountToReceive.getAccountNumber());
            transactions.add(new Transaction (TRANSFER,
                    accountNumber,
                    amount,
                    Utils.getDateTime(),
                    "Thành công"));

            accountToReceive.getTransactions().add(new Transaction (DEPOSIT,
                    accountToReceive.getAccountNumber(),
                    amount,
                    Utils.getDateTime(),
                    "Thành công"));
        }
    }

    public void deposit(double amount) {
        if(!checkAmountDepositNotValid(amount)) {
            balance += amount;
            System.out.println("Giao dịch nạp "
                    + accountNumber
                    + " thành công "
                    + Utils.formatBalance(amount));
            transactions.add(new Transaction (DEPOSIT,
                    accountNumber,
                    amount,
                    Utils.getDateTime(),
                    "Thành công"));
        } else {
            System.out.println("Giao dịch nạp "
                    + accountNumber
                    + " không thành công "
                    + Utils.formatBalance(amount));
            transactions.add(new Transaction (DEPOSIT,
                    accountNumber,
                    amount,
                    Utils.getDateTime(),
                    "Không hợp lệ"));
        }
    }

    public boolean checkAmountNotValid(double amount) {
        return amount % AMOUNT_TRANSACT_MINIMUM != 0 ||
                balance - amount < 0 ||
                amount <= 0;
    }

    public boolean checkAmountDepositNotValid(double amount) {
        return amount % AMOUNT_TRANSACT_MINIMUM != 0 ||
                amount <= 0;
    }

    public double getBALANCE_PREMIUM() {
        return BALANCE_PREMIUM;
    }
}
