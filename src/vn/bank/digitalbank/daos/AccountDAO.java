package vn.bank.digitalbank.daos;

import vn.bank.database.Db;
import vn.bank.digitalbank.models.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    /**
     * Add account to db
     * @param accountNumber number_account
     * @param customerId cus_id
     * @return success or unsuccess adding
     */
    public static boolean insert(String accountNumber, String customerId) {

        // SQL statement to add account
        String sql = """
                INSERT INTO ACC (number_account, cus_id)
                VALUES (?, ?)
                """;

        // Connect Java & MySQL
        try(
                Connection conn = Db.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            // Set customer id
            pstmt.setString(1, accountNumber);

            // Set customer name
            pstmt.setString(2, customerId);

            // Insert to table ACC
            return pstmt.executeUpdate() == 1;

        } catch (SQLException exc) {
            System.out.println("Can not add new account" + exc.getMessage());
            return false;
        }
    }

    /**
     * Check account is existed in db or not
     * @param accountNumber number_account
     * @return true/ false
     */
    public static boolean existByAccountNumber(String accountNumber) {
        String sql = """
                SELECT 1 FROM ACC 
                WHERE number_account = ?
                """;
        try(
                Connection conn = Db.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, accountNumber);

            ResultSet rs = pstmt.executeQuery();

            if(!rs.next()) {
                return false;
            }
        } catch (SQLException exc) {
            throw new RuntimeException("Can not access to database", exc);
        }

        return true;
    }

    /**
     * Check if customer owns account
     * @param customerId cus_id
     * @param accountNumber number_account
     * @return
     */
    public static boolean existByCustomerAndAccount(String customerId,
                                                    String accountNumber) {
        String sql = """
                SELECT 1 FROM ACC 
                WHERE number_account = ? AND cus_id = ?
                """;

        try(
                Connection conn = Db.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, customerId);

            ResultSet rs = pstmt.executeQuery();

            if(!rs.next()) {
                return false;
            }
        } catch (SQLException exc) {
            throw new RuntimeException("Can not access to database", exc);
        }

        return true;
    }

    /**
     * Subtract balance from source account in transfer transaction
     * or in withdraw transaction
     * @param conn Connection
     * @param accountSource number_account
     * @param amount amount to transfer
     * @return true/ false
     * @throws SQLException if a database access error occurs
     * or this method is called on a closed connection
     */
    public static boolean subtractBalance(Connection conn,
                                          String accountSource,
                                          BigDecimal amount) throws SQLException {
        String sql = """
                UPDATE ACC
                SET balance = balance - ?
                WHERE number_account = ?
                AND balance >= ?
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, amount);
            pstmt.setString(2, accountSource);
            pstmt.setBigDecimal(3, amount);

            return pstmt.executeUpdate() == 1;

        }
    }

    /**
     * Add balance to receive account in transfer transaction
     * or to source account in deposit transaction
     * @param conn Connection
     * @param accountNumber number_account
     * @param amount to add
     * @return true/ false
     * @throws SQLException if a database access error occurs
     * or this method is called on a closed connection
     */
    public static boolean addBalance(Connection conn,
                                          String accountNumber,
                                          BigDecimal amount) throws SQLException {
        String sql = """
                UPDATE ACC
                SET balance = balance + ?
                WHERE number_account = ?
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, amount);
            pstmt.setString(2, accountNumber);

            return pstmt.executeUpdate() == 1;

        }
    }

    /**
     * Query data of account
     * @param accountNumber number_account
     * @return Account object
     */
    public static Account getAccount(String accountNumber) {
        String sql = """
                SELECT cus_id, balance
                FROM ACC
                WHERE number_account = ?
                """;

        try (
                Connection conn = Db.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);

            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            String customerId = rs.getString(1);
            BigDecimal balance = rs.getBigDecimal(2);

            return new Account(customerId, accountNumber, balance);

        } catch (SQLException exc) {
            throw new RuntimeException("Can not access to database", exc);
        }
    }

    /**
     * Query all accounts in db
     * @return list of Account objects
     */
    public static List<Account> loadAccounts() {
        List<Account> accounts = new ArrayList<>();

        String sql = """
                SELECT *
                FROM ACC
                """;

        try (
                Connection conn = Db.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String accountNumber = rs.getString(1);
                String customerId = rs.getString(2);
                BigDecimal balance = rs.getBigDecimal(3);

                accounts.add(new Account(customerId, accountNumber, balance));
            }

            return accounts;
        } catch (SQLException exc) {
            throw new RuntimeException("Can not access to database", exc);
        }
    }

}
