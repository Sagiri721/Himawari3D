package com.himawari.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class YAMLReader {
    
    public static HashMap<String, String> readYAML(File filePath) {
        HashMap<String, String> yamlData = new HashMap<>();
        
        try {

            Scanner scanner = new Scanner(new FileInputStream(filePath));
            while (scanner.hasNextLine()) {

                String line = scanner.nextLine().trim();
                
                // Skip comments and empty lines
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                
                // Split the line into key and value
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    yamlData.put(key, value);
                }
            }

            scanner.close();
            
        } catch (IOException e) {
            Logger.LogError("Error reading YAML file: " + e.getMessage());
        }
        
        return yamlData;
    }
}
