package vn.bank.digitalbank.services;

import vn.bank.digitalbank.daos.CustomerDAO;
import vn.bank.digitalbank.models.Customer;

import java.util.List;

public class CustomerService {

    public static boolean createCustomer(String customerId, String name) {
        // Id must have 12 digit
        if (customerId == null || !customerId.matches("\\d{12}")) {
            System.out.println("Customer Id must have 12 digit");
            return false;
        }

        // Name must not be blank
        if (name == null || name.isBlank()) {
            System.out.println("Name must be filled");
            return false;
        }

        // Store customer into db
        boolean success = CustomerDAO.insert(customerId, name);

        if (success) {
            System.out.println("Congratulate new customer");
        }

        return success;
    }

    public static List<Customer> showAllCustomers() {
        List<Customer> customers;
        try {
            customers = CustomerDAO.showAll();
        } catch (RuntimeException exc) {
            System.out.println(exc.getMessage());
            return null;
        }
        return customers;
    }

}
