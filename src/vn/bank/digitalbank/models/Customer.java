package vn.bank.digitalbank.models;


import vn.bank.digitalbank.commons.Utils;
import vn.bank.digitalbank.exceptions.AccountNumberNotValidException;

import java.util.List;

public class Customer{

    private final String name;

    private final String customerId;

    List<Account> accountsOfCustomer;

    public String getName() {
        return name;
    }

    public String getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return name + " " + customerId;
    }

    /**
     * Create Customer
     * @param name name
     * @param customerId id
     */
    public Customer(String customerId, String name) {
        this.name = name;
        this.customerId = customerId;
        this.accountsOfCustomer = DigitalBank.getAccountsById(customerId);
    }

    public void showAccounts() {
        List<Account> accounts = DigitalBank.getAccounts();
        // Hiển thị thông tin KH
        System.out.println(customerId
                + " | " + name
                + " | " + isPremium(accountsOfCustomer)
                + " | " + Utils.formatBalance(getSumBalance(accountsOfCustomer)) + "đ");

        // Hiển thị thông tin TK
        for (int i = 0; i < accounts.size(); i++) {

            Account account = accounts.get(i);
            System.out.println((i + 1) + "\t" + account.toString());

        }
    }

    public boolean addAccount(String accountNumber,
                              String type) {
        try {
            Account newAccount = DigitalBank.getAccountByNumber(accountNumber);
            DigitalBank.getAccounts().add(newAccount);
            System.out.println("Đã thêm tài khoản " + accountNumber);
            return true;
        } catch (AccountNumberNotValidException exc) {
            System.out.println(exc.getMessage());
            return false;
        }
    }

    /**
     * Check is Customer is premium or not
     * @param accounts list accounts of customer
     * @return PREMIUM / NORMAL
     */
    public String isPremium(List<Account> accountsOfCustomer) {
        boolean check = accountsOfCustomer.stream()
                .anyMatch(account -> account.getBalance() >= account.getBALANCE_PREMIUM());
        if (check) {
            return "PREMIUM";
        } else {
            return "NORMAL";
        }
    }

    /**
     * Get sum of balance on all accounts of one customer
     * @param accounts list accounts
     * @return sum of balance
     */
    public double getSumBalance(List<Account> accountsOfCustomer) {
        double balance = 0;

        for(Account acc : accountsOfCustomer) {
            balance += acc.getBalance() ;
        }

        return balance;

    }

    public Account getAccountByNumber(String accountNumber) {
        return accountsOfCustomer.stream()
                .filter(acc -> accountNumber.equals(acc.getAccountNumber()))
                .findFirst()
                .orElseThrow(() -> new AccountNumberNotValidException(
                        "Tài khoản không tồn tại " + accountNumber
                ));
    }

//    public void displayTransactionInformation(List<Account> accountsOfCustomer)  {
//
//        // Hiện thông tin KH và TK
//        showAccounts();
//
//        // Hiện lịch sử giao dịch của KH
//        accountsOfCustomer.forEach(Account::displayTransactions);
//
//    }

    /**
     * Check Account is existed or not
     * If yes, call Account.withdraw()
     * @param accountNumber number of account
     * @param amount to withdraw
     */
    public void withdraw(String accountNumber,
                         double amount) {
        try {
            Account accountToWithdraw = DigitalBank.getAccountByNumber(accountNumber);
            accountToWithdraw.withdraw(amount);
        } catch (AccountNumberNotValidException exc) {
            System.out.println(exc.getMessage());
        }
    }

    /**
     * Check Account is existed or not
     * If yes, call Account.transfer()
     * @param accountReceiver number of account to receive
     * @param accountTransfer number of account to transfer
     * @param amount to transfer
     */
    public void transfer(String accountReceiver,
                         String accountTransfer,
                         double amount) {
        Account accountToReceive = null;
        try {
            accountToReceive = DigitalBank.getAccountByNumber(accountReceiver);
        } catch (AccountNumberNotValidException exc) {
            System.out.println(exc.getMessage());
        }

        try {
            Account accountToTransfer = DigitalBank.getAccountByNumber(accountTransfer);
            accountToTransfer.transfer(amount, accountToReceive);
        } catch (AccountNumberNotValidException exc) {
            System.out.println(exc.getMessage());
        }
    }

    /**
     * Check Account is existed or not
     * If yes, call Account.deposit()
     * @param accountNumber number of account
     * @param amount to deposit
     */
    public void deposit(String accountNumber,
                         double amount) {
        try {
            Account accountToDeposit = DigitalBank.getAccountByNumber(accountNumber);
            accountToDeposit.deposit(amount);
        } catch (AccountNumberNotValidException exc) {
            System.out.println(exc.getMessage());
        }
    }

    public static boolean validateAccountNumber(String accountNumber) {
        if (accountNumber.length() != 6) {
            return false;
        }

        for (int i = 0; i < accountNumber.length(); i++) {
            if (accountNumber.charAt(i) < '0'
                    || accountNumber.charAt(i) > '9') {
                return false;
            }
        }

        return true;
    }


}
