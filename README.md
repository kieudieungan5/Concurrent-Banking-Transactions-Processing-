#### Version 3 - Generate ordered lock ids to accounts and synchronize transactions

Use AtomicLong.incrementAndGet() to generate a unique lock id for each Account instance. During a transfer, thread acquires source and destination account locks in ascending lock id order, it completes transfer atomically then release both locks. Consistent lock ordering prevents deadlocks, but other threads have to wait until the required locks released

Implementation

-   AtomicLong.incrementAndGet()

Test Results

-   [Account.java - Version 3](src/vn/bank/digitalbank/models/Account.java)
-   [ordered_lock_ids_and_synchronized-output.txt](test-results/ordered_lock_ids_and_synchronized-output.txt)

Development Milestones

Preserve accounts balance and transaction history after application restart

#### Version 2 - Synchronize transactions on the same account using synchronized

Use synchronized with individual account locks to protect each account's balance update. But does not guarantee that a transfer between 2 accounts is atomic

Implementation

-   synchronized(this): Use account object to lock read, change, update balance, add transaction
-   Add transaction testing: Add transactions from 1 account to another & reversed

Test Results

-   [Account.java - Version 2](https://github.com/kieudieungan5/Concurrent-Banking-Transactions-Processing-/blob/ff92136/src/vn/bank/digitalbank/models/Account.java) Lock: read, change, update balance, add transaction
-   [Main.java - Version 2](https://github.com/kieudieungan5/Concurrent-Banking-Transactions-Processing-/blob/ff92136/src/Main.java) See added transactions
-   [synchronized-output.txt - Version 2](test-results/synchronized-output.txt)

Development Milestones

Deadlock happens when 2 accounts transfer to each other at same time: each hold transfer's lock and wait for receiver's lock. When 1 thread transfers and update transfer's balance, gives CPU to other thread, not update receiver's balance concurrently. Then other thread takes CPU, receiver deposits to its account and read wrong balance.

#### Version 1 - Concurrent Transaction Processing

The goal is to test how multiple transaction affect the same bank account and different accounts when executed concurrently. And result is that concurrent transactions can lead to race condition that can lead to incorrect account balances.

Implementation

-   Thread Pool: Uses Executors.newFixedThreadPool() to manage up to a number of worker thread
-   Concurrent Execution: Submits 10 transaction task using ExecutorService.submit()
-   Task Monitoring: Stores returned Future objects and call Future.get() to wait for tasks completion and detect
-   uncaught task exceptions
-   Transaction Testing: Runs withdrawal and transfer operations against account objects to investigate concurrent
-   balance updates

Test Results

-   [Main Java - Version 1](https://github.com/kieudieungan5/Concurrent-Banking-Transactions-Processing-/blob/1289658/src/Main.java) Source code for concurrent transaction test
-   [Multithreading Output - Version 1](https://github.com/kieudieungan5/Concurrent-Banking-Transactions-Processing-/blob/1289658/test-results/multithreading-output.txt) Console output captured during execution

Development Milestones

Subsequent version focus on synchronizing consistent account balances See Git commit history to compare implementation changes across versions