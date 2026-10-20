import vn.bank.digitalbank.daos.AccountDAO;
import vn.bank.digitalbank.models.Account;
import vn.bank.digitalbank.services.TransactionService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {

        /**
         * ExecutorService: manage pool of workers
         * : execute transactions concurrently
         */
        ExecutorService pool = Executors.newFixedThreadPool(10);
        List<Future<?>> futures = new ArrayList<>();

        List<Account> accounts = AccountDAO.loadAccounts();

        if (accounts.isEmpty()) {
            System.out.println("Empty list");
            return;
        }

        Random rd = new Random();

        final long TOTAL_TEST_TRANSACTIONS = 20_000;

        for (int i = 0; i < TOTAL_TEST_TRANSACTIONS; i++) {

            // 1. Generate a random source account
            int sourceIndex = rd.nextInt(accounts.size());
            Account source = accounts.get(sourceIndex);

            // 2. Generate a random amount
            long amountLong = rd.nextLong(1_000_000, 10_000_001);

            BigDecimal amount = BigDecimal.valueOf(amountLong);

            // 3. Generate a random transaction
            int operation = rd.nextInt(3);

            Future<?> future;

            switch (operation) {

                case 0:
                    // Transfer: Generate a random destination account
                    // condition: do not duplicate source account
                    if (accounts.size() < 2) {
                        System.out.println("List only has 1 account");
                        continue;
                    }

                    int destinationIndex;
                    do {
                        destinationIndex = rd.nextInt(accounts.size());
                    } while (destinationIndex == sourceIndex);

                    Account destination = accounts.get(destinationIndex);

                    future = pool.submit(() -> {
                        TransactionService.transfer(source.getCustomerId(),
                                source.getAccountNumber(),
                                destination.getAccountNumber(),
                                amount);
                    });

                    futures.add(future);
                    break;

                case 1:
                    // Withdraw:
                    future = pool.submit(() -> {
                        TransactionService.withdraw(source.getCustomerId(),
                                source.getAccountNumber(),
                                amount);
                    });
                    futures.add(future);
                    break;

                case 2:
                    // Deposit
                    future = pool.submit(() -> {
                        TransactionService.deposit(source.getCustomerId(),
                                source.getAccountNumber(),
                                amount);
                    });
                    futures.add(future);
                    break;

            }

        }

        /**
         * Stop accept new task
         */
        pool.shutdown();

        /**
         * Check if task is run normally / interrupted / encounter
         */
        for (Future<?> future : futures) {
            try {
                future.get();
//                System.out.println("Task hoàn thành bình thường");
            } catch (InterruptedException e) {
                System.out.println("Task is interrupted: " + e.getCause());
            } catch (ExecutionException e) {
                System.out.println("Task encounters error: " + e.getCause());
            }

        }

    }

}
