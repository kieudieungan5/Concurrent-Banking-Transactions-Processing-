package vn.bank.digitalbank.exceptions;

public class AccountNumberNotValidException extends RuntimeException {
    public AccountNumberNotValidException(String message) {
        super(message);
    }
}
