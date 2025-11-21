package com.mycompany.lab3bankingexample.programManagers;

import com.mycompany.lab3bankingexample.models.User;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserManager {

    private static Map<String, User> users = new HashMap<>();
    private static final String USERS_FILE = "users.txt";

    static {
        loadUsers();
    }

    private static void loadUsers() {
        File file = new File(USERS_FILE);

        if (!file.exists()) {
            createDefaultUsers();
            saveUsers();
        } else {
            readUsersFromFile();
        }
    }

    private static void createDefaultUsers() {
        users.put("john", new User("john", "password123", "John Smith"));
        users.put("emma", new User("emma", "password123", "Emma Johnson"));
        users.put("michael", new User("michael", "password123", "Michael Brown"));
        users.put("sophia", new User("sophia", "password123", "Sophia Davis"));
        users.put("william", new User("william", "password123", "William Wilson"));

        System.out.println("Created default users.");
    }

    private static void readUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String username = parts[0].trim();
                    String password = parts[1].trim();
                    String customerName = parts[2].trim();
                    users.put(username, new User(username, password, customerName));
                }
            }
            System.out.println("Loaded " + users.size() + " users from file.");
        } catch (IOException e) {
            System.err.println("Error reading users file: " + e.getMessage());
            createDefaultUsers();
        }
    }

    private static void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users.values()) {
                writer.write(user.getUsername() + "|" + user.getPassword() + "|" + user.getCustomerName());
                writer.newLine();
            }
            System.out.println("Users saved to file.");
        } catch (IOException e) {
            System.err.println("Error saving users file: " + e.getMessage());
        }
    }

    public static User authenticate(String username, String password) {
        User user = users.get(username.toLowerCase());
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public static void printAllUsers() {
        System.out.println("Available users:");
        for (User user : users.values()) {
            System.out.println("  Username: " + user.getUsername()
                    + " | Customer: " + user.getCustomerName());
        }
    }

}
