package com.teckiz.InventorySystem;

import java.io.File;
import java.util.Scanner;

public class EnvLoader {
    public static void load(String filePath) {
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        System.setProperty(key, value); // Set as system property
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
