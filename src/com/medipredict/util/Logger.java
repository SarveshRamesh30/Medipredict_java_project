package com.medipredict.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void info(String message) {
        log("INFO", message);
    }

    public static void warn(String message) {
        log("WARN", message);
    }

    public static void error(String message, Throwable t) {
        log("ERROR", message);
        if (t != null) {
            t.printStackTrace(System.err);
        }
    }

    public static void error(String message) {
        error(message, null);
    }

    private static synchronized void log(String level, String message) {
        String timestamp = SDF.format(new Date());
        String formatted = String.format("[%s] [%s] %s", timestamp, level, message);
        if ("ERROR".equals(level)) {
            System.err.println(formatted);
        } else {
            System.out.println(formatted);
        }
    }
}
