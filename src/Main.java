import vn.bank.digitalbank.commons.Utils;
import vn.bank.digitalbank.models.Account;
import vn.bank.digitalbank.models.Customer;
import vn.bank.digitalbank.models.DigitalBank;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    /**
     * Bank
     */
    private static final DigitalBank bank = new DigitalBank();

    /**
     * Hàm main
     * @param args
     */
    public static void main(String[] args) {

        /**
         * Customer Test
         */
        Customer customer1 = new Customer("001215000001", "Customer1");
        Customer customer2 = new Customer("001215000002", "Customer2");

        DigitalBank.getCustomers().add(customer1);
        DigitalBank.getCustomers().add(customer2);

        System.out.println("Khách hàng: ");
        DigitalBank.getCustomers().forEach((customer) -> System.out.println(customer.toString()));

        Account account1 = new Account("001215000001"
                , "123456"
                , 10_000_000
                , "SAVINGS");

        Account account2 = new Account("001215000002"
                , "123457"
                , 15_000_000
                , "SAVINGS");

        DigitalBank.getAccounts().add(account1);
        DigitalBank.getAccounts().add(account2);

        System.out.println("Tài khoản: ");
        DigitalBank.getAccounts().forEach((account) -> System.out.println(account.toString()));


        /**
         * ExecutorService: manage pool of workers
         * : execute transactions concurrently
         */
        ExecutorService pool = Executors.newFixedThreadPool(4);

        List<Future<?>> futures = new ArrayList<>();

        /**
         * Transactions Test
         */
        for (int i = 1; i <= 10; i++) {
            int transactionId = i;

            Future<?> future = pool.submit(() -> {

                System.out.println("==========Tasks Run===========");
                System.out.println("Số dư ban đầu: "
                        + account1.getAccountNumber()
                        + " là: "
                        + Utils.formatBalance(account1.getBalance()));
                System.out.println("Số dư ban đầu: "
                        + account2.getAccountNumber()
                        + " là: "
                        + Utils.formatBalance(account2.getBalance()));

                if (transactionId % 2 == 0) {
                    bank.withdraw(account1.getCustomerId(),
                            account1.getAccountNumber(),
                            3_000_000);
                    System.out.println("Nhiệm vụ "
                            + transactionId
                            + " cập nhật số dư sau rút 2tr: "
                            + account1.getAccountNumber()
                            + " là: "
                            + Utils.formatBalance(account1.getBalance()));
                } else {
                    bank.transfer(account1.getCustomerId(),
                            account2.getAccountNumber(),
                            account1.getAccountNumber(),
                            1000_000);
                    System.out.println("Nhiệm vụ "
                            + transactionId
                            + " cập nhật số dư sau chuyển 1tr: "
                            + account1.getAccountNumber()
                            + " là: "
                            + Utils.formatBalance(account1.getBalance()));

                    System.out.println("Nhiệm vụ "
                            + transactionId
                            + " cập nhật số dư sau nhận 1tr: "
                            + account2.getAccountNumber()
                            + " là: "
                            + Utils.formatBalance(account2.getBalance()));
                }

                System.out.println(
                        "Task " + transactionId
                                + " chạy trên "
                                + Thread.currentThread().getName());
            });

            futures.add(future);


        }

        /**
         * Stop accepting new task
         */
        pool.shutdown();

        /**
         * Check
         */
        for (Future<?> future : futures) {
            try {
                future.get();
//                System.out.println("Task hoàn thành bình thường");
            } catch (InterruptedException e) {

            } catch (ExecutionException e) {
                System.out.println("Task gặp lỗi: " + e.getCause());
            }

        }

        /**
         * Check final balance
         */
        System.out.println("=========Final Balance=========");
        System.out.println(account1.getAccountNumber() +
                " : " +
                Utils.formatBalance(account1.getBalance()));
        System.out.println(account2.getAccountNumber() +
                " : " +
                Utils.formatBalance(account2.getBalance()));
    }

}
