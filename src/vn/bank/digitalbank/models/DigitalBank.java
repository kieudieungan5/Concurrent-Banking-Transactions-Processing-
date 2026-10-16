package vn.bank.digitalbank.models;

import vn.bank.digitalbank.exceptions.AccountNumberNotValidException;
import vn.bank.digitalbank.exceptions.CustomerIdNotValidException;

import java.util.ArrayList;
import java.util.List;

public class DigitalBank {

    private static final List<Customer> customers = new ArrayList<>();
    private static final List<Account> accounts = new ArrayList<>();

    public static List<Customer> getCustomers() {
        return customers;
    }

    public static List<Account> getAccounts() {
        return accounts;
    }

    public static boolean checkCustomerNotExisted(String customerId) {
        for (Customer customer : customers) {
            if (customerId.equals(customer.getCustomerId())) {
                return false;
            }
        }
        return true;
    }

    public static Account getAccountByNumber(String accountNumber)
            throws AccountNumberNotValidException {

        return accounts.stream()
                .filter(acc -> accountNumber.equals(acc.getAccountNumber()))
                .findFirst()
                .orElseThrow(() -> new AccountNumberNotValidException(
                        "Tài khoản không tồn tại trong ngân hàng: " + accountNumber
                ));
    }

    public static Customer getCustomerById(String customerId)
            throws CustomerIdNotValidException {

        return customers.stream()
                    .filter(c -> customerId.equals(c.getCustomerId()))
                    .findFirst()
                    .orElseThrow(() -> new CustomerIdNotValidException (
                            "Khách hàng chưa có trong hệ thống: " + customerId
        ));

    }

    public static List<Account> getAccountsById(String customerId) {
        return accounts.stream()
                .filter(acc -> customerId.equals(acc.getCustomerId()))
                .toList();

    }

    public boolean addCustomer(String customerId, String name) {
        if(checkCustomerNotExisted(customerId)) {
            Customer newCustomer = new Customer(customerId, name);
            customers.add(newCustomer);
            System.out.println("New customer was added ");
            return true;
        } else {
            System.out.println("This customer exited.");
            return false;
        }
    }

    public void withdraw(String customerId
            , String accountNumber
            , double amount) {
        try {
            Customer customer = getCustomerById(customerId);
            customer.withdraw(accountNumber, amount);
        } catch (CustomerIdNotValidException exc) {
            System.out.println(exc.getMessage());
        }
    }

    public void transfer(String customerId
            , String accountReceiver
            , String accountTransfer
            , double amount) {
        Customer customer;
        try {
            customer = getCustomerById(customerId);
            customer.transfer(accountReceiver, accountTransfer, amount);
        } catch (CustomerIdNotValidException exc) {
            System.out.println(exc.getMessage());
        }
    }

    public void deposit(String customerId
            , String accountNumber
            , double amount) {
        Customer customer;
        try {
            customer = getCustomerById(customerId);
                customer.deposit(accountNumber, amount);
        } catch (CustomerIdNotValidException exc) {
            System.out.println(exc.getMessage());
        }
    }
}
