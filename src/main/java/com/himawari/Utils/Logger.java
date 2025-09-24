package com.himawari.Utils;

import java.io.FileWriter;

public class Logger {

    public static String LOG_FILE = "logs/himawari.log";
    private static boolean isInitialized = false;

    private static final String blueAnsi = "\u001B[34m";
    private static final String redAnsi = "\u001B[31m";
    private static final String yellowAnsi = "\u001B[33m";
    private static final String resetAnsi = "\u001B[0m";
    
    public static void LogInfo(String message){
        String msg = (blueAnsi + "[INFO] " + resetAnsi + message);
        log(msg);
    }

    public static void LogWarning(String message){
        String msg = (yellowAnsi + "[WARNING] " + resetAnsi + message);
        log(msg);
    }

    public static void LogError(String message){
        String msg = (redAnsi + "[ERROR] " + resetAnsi + message);
        log(msg);
    }

    private static void log(String msg) {

        System.out.println(msg);

        // Append to log file
        try {

            String logStr = msg.replace(blueAnsi, "").replace(redAnsi, "").replace(yellowAnsi, "").replace(resetAnsi, ": ");
            
            FileWriter writer = new FileWriter(LOG_FILE, isInitialized);
            writer.write(logStr + "\n");
            writer.close();

            isInitialized = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
