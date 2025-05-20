package com.himawari.Utils;

import java.io.BufferedReader;
import java.io.File;

public class CVSReader {
    
    public static String[][] readCSV(File filePath) {

        String[][] data = null;
        BufferedReader reader = null;
        
        try {

            reader = new BufferedReader(new java.io.FileReader(filePath));

            String line;
            int rowCount = 0;

            // First, count the number of rows
            while ((line = reader.readLine()) != null) {
                rowCount++;
            }

            // Reset the reader to the beginning of the file
            reader.close();
            reader = new BufferedReader(new java.io.FileReader(filePath));

            // Initialize the data array with the correct size
            data = new String[rowCount][];

            int currentRow = 0;

            // Read the file again and populate the data array
            while ((line = reader.readLine()) != null) {
                data[currentRow] = line.split(";");
                currentRow++;
            }

        } catch (Exception e) {

            Logger.LogError("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                Logger.LogError("Error closing CSV file: " + e.getMessage());
            }
        }

        return data;
    }
}
