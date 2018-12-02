package jmml.monitor.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    private String logFile;
    private FileWriter fw;
    private File fnew;

    public Logger(LoggerRaw file) {
        if (file.getLogFile() == null) {
            this.logFile = "log.txt";
        } else {
            this.logFile = file.getLogFile();
        }

        File fold = new File(this.logFile);
        fold.delete();
        fnew = new File(this.logFile);

        try {
            this.fw = new FileWriter(fnew, true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void print(Object module, String msj) {
        String buffer;
        long timestamp = java.lang.System.currentTimeMillis();

        buffer = String.format("%014d | %5d | %-25.25s | %s\n", timestamp, Thread.currentThread().getId(), module.getClass().getName(),msj);


        try {
            this.fw.write(buffer);
        }catch (java.io.IOException e){
            System.out.print(e.toString());
        }

    }

    public void close() {
        try {
            this.fw.close();
        }catch (java.io.IOException e){
            System.out.print(e.toString());
        }

    }
}
