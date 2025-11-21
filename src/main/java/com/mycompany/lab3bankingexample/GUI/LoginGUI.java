package com.mycompany.lab3bankingexample.GUI;

import com.mycompany.lab3bankingexample.models.User;
import com.mycompany.lab3bankingexample.programManagers.UserManager;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginGUI extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel statusLabel;
    
    public LoginGUI() {
        setTitle("e-Banking Application - Login");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        createLoginPanel();
        
        setVisible(true);
    }
    
    private void createLoginPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        mainPanel.setBackground(new Color(240, 240, 240));
        
        JLabel titleLabel = new JLabel("e-Banking System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 150, 243));
        
        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        
        JPanel headerContainer = new JPanel(new GridLayout(2, 1, 0, 5));
        headerContainer.setBackground(new Color(240, 240, 240));
        headerContainer.add(titleLabel);
        headerContainer.add(subtitleLabel);
        
        mainPanel.add(headerContainer, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 10, 15));
        formPanel.setBackground(new Color(240, 240, 240));
        formPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(statusLabel);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(76, 175, 80));
        loginButton.setForeground(Color.GREEN);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> performLogin());
        
        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 14));
        exitButton.setBackground(new Color(244, 67, 54));
        exitButton.setForeground(Color.RED);
        exitButton.setFocusPainted(false);
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        
        JPanel instructionsPanel = new JPanel();
        instructionsPanel.setBackground(new Color(240, 240, 240));
        instructionsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel instructionsLabel = new JLabel("<html><center>Default users: john, emma, michael, sophia, william<br>Default password: password123</center></html>");
        instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        instructionsLabel.setForeground(Color.GRAY);
        instructionsPanel.add(instructionsLabel);
        
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(new Color(240, 240, 240));
        bottomContainer.add(buttonPanel, BorderLayout.CENTER);
        bottomContainer.add(instructionsPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomContainer, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        passwordField.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        User user = UserManager.authenticate(username, password);
        
        if (user != null) {
            statusLabel.setText("Login successful! Loading...");
            statusLabel.setForeground(new Color(76, 175, 80));
            
            loginButton.setEnabled(false);
            exitButton.setEnabled(false);
            
            SwingUtilities.invokeLater(() -> {
                new BankingAppGUI(user);
                dispose();
            });
        } else {
            statusLabel.setText("Invalid username or password");
            statusLabel.setForeground(Color.RED);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
}