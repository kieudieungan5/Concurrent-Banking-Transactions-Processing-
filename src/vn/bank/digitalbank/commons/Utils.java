package vn.bank.digitalbank.commons;
import vn.bank.digitalbank.models.Account;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    private static Account account;

    public static void getUI() {

        System.out.println("+----------+-------------------------+--------------------------------+");
        System.out.println("| NGAN HANG SO | FX92501@v4.0.0                                       |");
        System.out.println("+----------+-------------------------+--------------------------------+");
        System.out.println(" 1. Xem danh sách khách hàng                                          |");
        System.out.println(" 2. Nhập danh sách khách hàng                                         |");
        System.out.println(" 3. Thêm tài khoản ATM                                                |");
        System.out.println(" 4. Chuyển tiền                                                       |");
        System.out.println(" 5. Rút tiền                                                          |");
        System.out.println(" 6. Tra cứu lịch sử giao dịch                                         |");
        System.out.println(" 0. Thoát                                                             |");
        System.out.println("+----------+-------------------------+--------------------------------+");

    }
    /**
     * Hiện phần phân cách
     *
     * @return chuỗi phân cách
     */
    public static String getDivider() {

        return "+----------+------------------------------+--------------------------------+";

    }

//    /**
//     * Hiện tiêu đề biên lai
//     * @return chuỗi tiêu đề
//     */
//    public static String getTitle() {
//
//        if(account instanceof SavingsAccount) {
//            return "BIEN LAI GIAO DICH SAVINGS";
//        } else {
//            return "BIEN LAI GIAO DICH LOAN";
//        }
//
//    }

    /**
     * Hiện ngày, giờ giao dịch
     * @return chuỗi ngày giờ
     */
    public static String getDateTime() {

        LocalDateTime transactionTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return transactionTime.format(formatter);

    }

    /**
     * Định dạng số tiền
     * @param amount số thực cần định dạng
     * @return chuỗi
     */
    public static String formatBalance(double amount) {

        if(amount == 0) {
            return "00.00";
        }

        DecimalFormat df = new DecimalFormat("#,###.00");
        return df.format(amount);

    }

}

