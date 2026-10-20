package vn.bank.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {

    // Address of db: "jdbc:mysql://HOST:PORT/DATABASE_NAME"
    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/digital_bank";

    // Account to access to db
    private static final String USER =
            "digital_app";

    // Password to access to db
    private static final String PASS =
            "Hanhphuc2026@";

    // MySQl verifies account & access right of account to db,
    // grants connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

}
