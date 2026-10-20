package vn.bank.logs;

import vn.bank.digitalbank.commons.Utils;

import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    public static void logTransaction(String message) {
        try (FileWriter fw = new FileWriter(
                "log-transactions.txt",
                true);
        ) {
            fw.write(Utils.getDateTime() +
                    " - " +
                    message +
                    System.lineSeparator());
        } catch (IOException exc) {
            System.out.println("Error writing log " + exc.getMessage());
        }
    }

}
