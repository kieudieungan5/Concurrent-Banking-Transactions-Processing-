Concurrent Transaction Processing 

Overview

The first goal is to test how multiple transaction affect the same bank account and different accounts
when executed concurrently. And result is that concurrent transactions can lead to race 
condition that can lead to incorrect account balances.

Implementation
* Thread Pool: Uses Executors.newFixedThreadPool() to manage up to a number of worker thread
* Concurrent Execution: Submits 10 transaction task using ExecutorService.submit()
* Task Monitoring: Stores returned Future objects and call Future.get() to wait for tasks completion 
and detect uncaught task exceptions
* Transaction Testing: Runs withdrawal and transfer operations against account objects 
to investigate concurrent balance updates

Test Results
- [Main Java](src/Main.java) Source code for concurrent transaction test
- [Multithreading Output](test-results/multithreading-output.txt) Console output captured during execution

Development Milestones

Subsequent version focus on synchronizing consistent account balances
See Git commit history to compare implementation changes across versions