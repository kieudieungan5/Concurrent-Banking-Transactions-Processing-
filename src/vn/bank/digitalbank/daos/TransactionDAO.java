package vn.bank.digitalbank.daos;

import vn.bank.digitalbank.models.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDAO {

    /**
     * Add transaction to db
     * @param conn Connection
     * @param trans Transaction object
     * @return true/ false
     * @throws SQLException if a database access error occurs
     * or this method is called on a closed connection
     */
    public static boolean insert(Connection conn,
                              Transaction trans) throws SQLException {
        String sql = """
                INSERT INTO TRANS (
                id_trans,
                acc_source,
                acc_receive,
                type_trans,
                amount,
                status) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,trans.getId());
            pstmt.setString(2,trans.getAccountSource());
            pstmt.setString(3,trans.getAccountDestination());
            pstmt.setString(4,trans.getType().name());
            pstmt.setBigDecimal(5,trans.getAmount());
            pstmt.setString(6,trans.status().name());

            return pstmt.executeUpdate() == 1;

        }
    }

}
