package vn.bank.digitalbank.services;

import vn.bank.database.Db;
import vn.bank.digitalbank.commons.TransactionStatus;
import vn.bank.digitalbank.commons.TransactionType;
import vn.bank.digitalbank.daos.AccountDAO;
import vn.bank.digitalbank.daos.CustomerDAO;
import vn.bank.digitalbank.daos.TransactionDAO;
import vn.bank.digitalbank.models.Transaction;
import vn.bank.logs.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class TransactionService {

    /**
     * Update balances, add SUCCESS transactions
     * Roll back balances and add FAILED transactions if transfer can not execute
     * Handle roll back error, insert transaction error, commit error (writing to log file)
     * @param customerId cus_id
     * @param accountSource acc_source
     * @param accountDestination acc_receive
     * @return true/ false
     */
    public static boolean transfer(String customerId,
                                            String accountSource,
                                            String accountDestination,
                                            BigDecimal amount) {

        // 1. Validate if customer transfers between different accounts
        if (accountSource.compareTo(accountDestination) == 0) {
            System.out.println("Can not make transaction cause account is duplicated");
            return false;
        }

        // 2. Validate if customer transfers invalid amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be greater than 0");
            return false;
        }

        // 3. Validate customer & account
        // Check if customer is existed
        // Check if source account is existed
        // Check if destination account is existed
        // Check if customer executing transaction owns account
        try {
            if (!CustomerDAO.existById(customerId)) {
                System.out.println("Customer does not exist");
                return false;
            }

            if (!AccountDAO.existByAccountNumber(accountSource)) {
                System.out.println("Source account is not existed");
                return false;
            }

            if (!AccountDAO.existByAccountNumber(accountDestination)) {
                System.out.println("Destination account is not existed");
                return false;
            }

            if (!AccountDAO.existByCustomerAndAccount(customerId, accountSource)) {
                System.out.println("Customer does not own this source account");
                return false;
            }
        } catch (RuntimeException exc) {
            System.out.println("Validation error: " + exc.getMessage());
            return false;
        }

        // 4. Create transaction before execute transer
        Transaction successTrans = new Transaction(
                TransactionType.TRANSFER,
                accountSource,
                accountDestination,
                amount,
                TransactionStatus.SUCCESS
        );

        // 5. Execute transfer in one db connection
        try (Connection conn = Db.getConnection()) {

            // Turn of auto commit
            conn.setAutoCommit(false);

            // 3.1. Update balance & add transaction history
            try {
                if (!AccountDAO.subtractBalance(conn, accountSource, amount)) {
                    throw new SQLException("Account source is not existed or has not enough balance");
                }

                if (!AccountDAO.addBalance(conn, accountDestination, amount)) {
                    throw new SQLException("Account destination is not existed");
                }

                if (!TransactionDAO.insert(conn, successTrans)) {
                    throw new SQLException("Can not save transaction");
                }

                // 4. If error occurs before commit
                // Roll back balance changes & transaction history
            } catch (SQLException processingError) {

                try {
                    conn.rollback();

                    // 4.1 If roll back is not success, add error to previous error
                    // Roll back error do not assume transaction was undone
                    // Write roll back error to log
                } catch (SQLException rollbackError) {
                    processingError.addSuppressed(rollbackError);

                    Logger.logTransaction(
                            "ROLLBACK outcome uncertain"
                                    + " | transactionId=" + successTrans.getId()
                                    + " | source=" + accountSource
                                    + " | destination=" + accountDestination
                                    + " | amount=" + amount
                                    + " | processingError=" + processingError.getMessage()
                                    + " | rollbackError=" + rollbackError.getMessage()
                    );
                    return false;
                }

                // 4.2 If roll back successes
                // Save FAILED transaction through a seperate connection
                try (Connection rollBackConn = Db.getConnection()) {
                    Transaction failedTrans = new Transaction(TransactionType.TRANSFER,
                            accountSource,
                            accountDestination,
                            amount,
                            TransactionStatus.FAILED);

                    if (!TransactionDAO.insert(rollBackConn, failedTrans)) {
                        throw new SQLException(
                                "Cannot save FAILED transaction history");
                    }

                    // Write insert error to log
                } catch (SQLException insertError) {
                    Logger.logTransaction(
                            "FAILED history could not be saved"
                                    + " | attemptedTransactionId=" + successTrans.getId()
                                    + " | source=" + accountSource
                                    + " | destination=" + accountDestination
                                    + " | amount=" + amount
                                    + " | processingError=" + processingError.getMessage()
                                    + " | historyError=" + insertError.getMessage()
                    );
                }
                return false;
            }

            // 5. Commit changes
            try {
                // 5.1. Commit success & return to application
                conn.commit();
                return true;

                // 5.2. Commit success but return error to application
                // Do not assume that transaction is not commited
                // Write commit error to log
            } catch (SQLException commitError) {
                Logger.logTransaction(
                        "COMMIT outcome uncertain"
                                + " | transactionId=" + successTrans.getId()
                                + " | source=" + accountSource
                                + " | destination=" + accountDestination
                                + " | amount=" + amount
                                + " | error=" + commitError.getMessage()
                );
                return false;
            }
        } catch (SQLException exc) {
            System.out.println("Can not access to database " + exc.getMessage());
            return false;
        }
    }

    /**
     * Update balance, add SUCCESS transaction
     * Roll back balance and add FAILED transactions if withdraw can not execute
     * Handle roll back error, insert transaction error, commit error (writing to log file)
     * @param customerId cus_id
     * @param accountSource acc_source
     * @param amount to withdraw
     * @return true/ false
     */
    public static boolean withdraw(
            String customerId,
            String accountSource,
            BigDecimal amount) {

        // 1. Validate input
        if (accountSource == null) {
            System.out.println("Account number must not be null");
            return false;
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be greater than 0");
            return false;
        }

        // 2. Validate customer and account
        try {
            if (!CustomerDAO.existById(customerId)) {
                System.out.println("Customer does not exist");
                return false;
            }

            if (!AccountDAO.existByAccountNumber(accountSource)) {
                System.out.println("Account does not exist");
                return false;
            }

            if (!AccountDAO.existByCustomerAndAccount(
                    customerId, accountSource)) {
                System.out.println("Customer does not own this account");
                return false;
            }

        } catch (RuntimeException exc) {
            System.out.println("Validation error: " + exc.getMessage());
            return false;
        }

        // 3. Create transaction before processing to retain its ID for logging
        Transaction successTrans = new Transaction(
                TransactionType.WITHDRAW,
                accountSource,
                null,
                amount,
                TransactionStatus.SUCCESS
        );

        // 4. Execute withdrawal in one database transaction
        try (Connection conn = Db.getConnection()) {

            conn.setAutoCommit(false);

            // 4.1 Update balance and insert transaction history
            try {
                if (!AccountDAO.subtractBalance(
                        conn, accountSource, amount)) {
                    throw new SQLException(
                            "Account does not exist or has insufficient balance");
                }

                if (!TransactionDAO.insert(conn, successTrans)) {
                    throw new SQLException(
                            "Cannot save transaction history");
                }

            } catch (SQLException processingError) {

                // 5. Roll back balance changes and SUCCESS history
                try {
                    conn.rollback();

                } catch (SQLException rollbackError) {
                    processingError.addSuppressed(rollbackError);

                    Logger.logTransaction(
                            "WITHDRAW rollback outcome uncertain"
                                    + " | transactionId=" + successTrans.getId()
                                    + " | account=" + accountSource
                                    + " | amount=" + amount
                                    + " | processingError=" + processingError.getMessage()
                                    + " | rollbackError=" + rollbackError.getMessage()
                    );

                    return false;
                }

                // 6. Rollback succeeded: save FAILED history separately
                try (Connection logConn = Db.getConnection()) {

                    Transaction failedTrans = new Transaction(
                            TransactionType.WITHDRAW,
                            accountSource,
                            null,
                            amount,
                            TransactionStatus.FAILED
                    );

                    if (!TransactionDAO.insert(logConn, failedTrans)) {
                        throw new SQLException(
                                "Cannot save FAILED transaction history");
                    }

                } catch (SQLException logError) {
                    Logger.logTransaction(
                            "WITHDRAW FAILED history could not be saved"
                                    + " | attemptedTransactionId=" + successTrans.getId()
                                    + " | account=" + accountSource
                                    + " | amount=" + amount
                                    + " | processingError=" + processingError.getMessage()
                                    + " | historyError=" + logError.getMessage()
                    );
                }
                return false;
            }

            // 6. Commit separately so commit errors are not treated
            // as ordinary processing errors.
            try {
                conn.commit();
                return true;

            } catch (SQLException commitError) {

                Logger.logTransaction(
                        "WITHDRAW COMMIT outcome uncertain"
                                + " | transactionId=" + successTrans.getId()
                                + " | account=" + accountSource
                                + " | amount=" + amount
                                + " | error=" + commitError.getMessage()
                );
                return false;
            }

        } catch (SQLException exc) {
            System.out.println(exc.getMessage());
            return false;
        }

    }

    /**
     * Update balance, add SUCCESS transaction
     * Roll back balance and add FAILED transaction if transfer can not execute
     * Handle roll back error, insert transaction error, commit error (writing to log file)
     * @param customerId cus_id
     * @param accountSource acc_source
     * @param amount to deposit
     * @return true/ false
     */
    public static boolean deposit(
            String customerId,
            String accountSource,
            BigDecimal amount) {

        // 1. Validate input
        if (accountSource == null) {
            System.out.println("Account number must not be null");
            return false;
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be greater than 0");
            return false;
        }

        // 2. Validate customer and account
        try {
            if (!CustomerDAO.existById(customerId)) {
                System.out.println("Customer does not exist");
                return false;
            }

            if (!AccountDAO.existByAccountNumber(accountSource)) {
                System.out.println("Account does not exist");
                return false;
            }

            if (!AccountDAO.existByCustomerAndAccount(
                    customerId, accountSource)) {
                System.out.println("Customer does not own this account");
                return false;
            }

        } catch (RuntimeException exc) {
            System.out.println("Validation error: " + exc.getMessage());
            return false;
        }

        // 3. Create transaction before processing to retain its ID for logging
        Transaction successTrans = new Transaction(
                TransactionType.DEPOSIT,
                accountSource,
                null,
                amount,
                TransactionStatus.SUCCESS
        );

        // 4. Execute deposit in one database transaction
        try (Connection conn = Db.getConnection()) {

            conn.setAutoCommit(false);

            // 4.1 Update balance and insert transaction history
            try {
                if (!AccountDAO.addBalance(
                        conn, accountSource, amount)) {
                    throw new SQLException(
                            "Account does not exist");
                }

                if (!TransactionDAO.insert(conn, successTrans)) {
                    throw new SQLException(
                            "Cannot save transaction history");
                }

            } catch (SQLException processingError) {

                // 4.2. Roll back balance changes and SUCCESS history
                try {
                    conn.rollback();

                } catch (SQLException rollbackError) {
                    processingError.addSuppressed(rollbackError);

                    Logger.logTransaction(
                            "DEPOSIT rollback outcome uncertain"
                                    + " | transactionId=" + successTrans.getId()
                                    + " | account=" + accountSource
                                    + " | amount=" + amount
                                    + " | processingError=" + processingError.getMessage()
                                    + " | rollbackError=" + rollbackError.getMessage()
                    );

                    return false;
                }

                // 4.3. Rollback succeeded: save FAILED history separately
                try (Connection logConn = Db.getConnection()) {

                    Transaction failedTrans = new Transaction(
                            TransactionType.DEPOSIT,
                            accountSource,
                            null,
                            amount,
                            TransactionStatus.FAILED
                    );

                    if (!TransactionDAO.insert(logConn, failedTrans)) {
                        throw new SQLException(
                                "Cannot save FAILED transaction history");
                    }

                } catch (SQLException logError) {
                    Logger.logTransaction(
                            "DEPOSIT FAILED history could not be saved"
                                    + " | attemptedTransactionId=" + successTrans.getId()
                                    + " | account=" + accountSource
                                    + " | amount=" + amount
                                    + " | processingError=" + processingError.getMessage()
                                    + " | historyError=" + logError.getMessage()
                    );
                }
                return false;
            }

            // 5. Commit separately
            try {
                conn.commit();
                return true;

            } catch (SQLException commitError) {

                Logger.logTransaction(
                        "DEPOSIT COMMIT outcome uncertain"
                                + " | transactionId=" + successTrans.getId()
                                + " | account=" + accountSource
                                + " | amount=" + amount
                                + " | error=" + commitError.getMessage()
                );
                return false;
            }

        } catch (SQLException exc) {
            System.out.println("Cannot access database: " + exc.getMessage());
            return false;
        }
    }


}
