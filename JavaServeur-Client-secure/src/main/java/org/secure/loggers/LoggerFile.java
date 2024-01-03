package org.secure.loggers;

import org.secure.utils.interfaces.Logs;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerFile implements Logs {

    private boolean trunc;

    public LoggerFile(){
        this.trunc = false;
    }

    @Override
    public void writeLog(String message) {
        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "\\logs.txt",trunc));
            bw.write("[__" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss")) + "__]: " + message + "\n");
            bw.close();

            if(!trunc) trunc = true;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws IOException {
        System.out.println(System.getProperty("user.dir") + "\\.txt");

        LoggerFile lg = new LoggerFile();


        for (int i = 0; i < 4 ; i++)lg.writeLog("Hello world");
    }
}



