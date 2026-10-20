package vn.bank.digitalbank.services;

import vn.bank.digitalbank.daos.AccountDAO;

public class AccountService {
    public static boolean createAccount(String accountNumber, String customerId) {
        // Id must have 12 digit
        if (customerId == null || !customerId.matches("\\d{12}")) {
            System.out.println("Customer Id must have 12 digits");
            return false;
        }

        // Account number must have 6 digit
        if (accountNumber == null || !accountNumber.matches("\\d{6}")) {
            System.out.println("Account number must have 6 digits");
            return false;
        }

        // Stores account into db
        boolean success = AccountDAO.insert(accountNumber, customerId);

        if (success) {
            System.out.println("Created new account");
        }

        return success;
    }
}
