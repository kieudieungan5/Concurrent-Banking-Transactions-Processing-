package vn.bank.digitalbank.models;

import vn.bank.digitalbank.exceptions.CustomerIdNotValidException;

public class CustomerIdValidator {

    /**
     * Kiểm tra mã CC có hợp lệ không
     * @param customerId mã CC
     * @return true nếu hợp lệ, ném ra ngoại lệ CustomerIdNotValidException nếu không hợp lệ
     */
    public static boolean validateCustomerId(String customerId) throws CustomerIdNotValidException {

        // Nếu mã CC không đúng 12 kí tự thì ném ra ngoại lệ
        if(customerId.length() != 12) {
            throw new CustomerIdNotValidException(
                    "Căn cước phải có 12 chữ số từ 0 đến 9"
            );
        }

        // Mã CC có chứa kí tự không phải là số thì ném ra ngoại lệ
        for(int i = 0; i < customerId.length(); i++) {
            if(customerId.charAt(i) < '0' || customerId.charAt(i) > '9') {
                throw new CustomerIdNotValidException(
                        "Căn cước phải có 12 chữ số từ 0 đến 9"
                );
            }
        }

        return true;
    }

}
