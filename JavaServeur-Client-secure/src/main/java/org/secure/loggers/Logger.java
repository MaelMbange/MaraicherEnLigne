package org.secure.loggers;

import org.secure.utils.interfaces.Logs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger implements Logs {
    @Override
    public void writeLog(String message) {
        System.out.println("[__" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss")) + "__]: " + message + "\n");
    }
}
