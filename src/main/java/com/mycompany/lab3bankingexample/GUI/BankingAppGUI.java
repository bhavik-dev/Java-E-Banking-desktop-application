package com.mycompany.lab3bankingexample.GUI;

import com.mycompany.lab3bankingexample.customexception.InsufficientFundsException;
import com.mycompany.lab3bankingexample.database.CustomerDAO;
import com.mycompany.lab3bankingexample.models.BankAccount;
import com.mycompany.lab3bankingexample.models.CustomerAccount;
import com.mycompany.lab3bankingexample.models.User;
import com.mycompany.lab3bankingexample.programManagers.DatabaseManager;
import java.awt.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.*;

public class BankingAppGUI extends JFrame {

    private Map<String, CustomerAccount> customers;
    private CustomerAccount currentCustomer;
    private String selectedAccountType;
    private CustomerDAO customerDAO;
    private User loggedInUser;

    private JList<String> accountList;
    private DefaultListModel<String> accountListModel;
    private JPanel accountPanel;
    private JLabel customerNameLabel;
    private JLabel welcomeLabel;
    private JRadioButton savingRadio;
    private JRadioButton checkingRadio;
    private JLabel balanceLabel;
    private JTextField amountField;
    private JButton depositButton;
    private JButton withdrawButton;
    private JButton addInterestButton;
    private JButton deductFeesButton;
    private JButton logoutButton;
    private JPanel transferPanel;
    private JComboBox<String> transferToCombo;
    private JComboBox<String> transferAccountCombo;
    private JTextField transferAmountField;
    private JButton transferButton;
    private JTextArea transactionLog;

    public BankingAppGUI(User user) {
        this.loggedInUser = user;

        setTitle("e-Banking Application - " + user.getCustomerName());
        setSize(1200, 700);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DatabaseManager.initializeDatabase();
        DatabaseManager.insertSampleData();

        customerDAO = new CustomerDAO();
        loadCustomersFromDatabase();

        currentCustomer = customers.get(user.getCustomerName());

        if (currentCustomer == null) {
            JOptionPane.showMessageDialog(this,
                    "Customer account not found for user: " + user.getCustomerName(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        createTopPanel();
        createAccountListPanel();
        createAccountPanel();
        createTransactionLogPanel();

        setVisible(true);

        logTransaction("User " + user.getUsername() + " logged in");
    }

    private void loadCustomersFromDatabase() {
        customers = customerDAO.loadAllCustomers();
        System.out.println("Loaded " + customers.size() + " customers from database.");
    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(25, 118, 210));
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        welcomeLabel = new JLabel("Welcome, " + loggedInUser.getCustomerName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.WHITE);

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 13));
        logoutButton.setBackground(new Color(211, 47, 47));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> performLogout());

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            logTransaction("User " + loggedInUser.getUsername() + " logged out");
            dispose();
            SwingUtilities.invokeLater(() -> new LoginGUI());
        }
    }

    private void createAccountListPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        leftPanel.setPreferredSize(new Dimension(200, 0));
        leftPanel.setBackground(new Color(250, 250, 250));

        JLabel titleLabel = new JLabel("My Accounts", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(33, 33, 33));
        leftPanel.add(titleLabel, BorderLayout.NORTH);

        accountListModel = new DefaultListModel<>();

        if (currentCustomer.hasSavingAccount()) {
            accountListModel.addElement("Saving Account");
        }
        if (currentCustomer.hasCheckingAccount()) {
            accountListModel.addElement("Checking Account");
        }

        accountList = new JList<>(accountListModel);
        accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountList.setFont(new Font("Arial", Font.PLAIN, 14));
        accountList.setBackground(Color.WHITE);
        accountList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onAccountSelected();
            }
        });

        JScrollPane scrollPane = new JScrollPane(accountList);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
    }

    private void onAccountSelected() {
        String selectedAccount = accountList.getSelectedValue();
        if (selectedAccount != null) {
            if (selectedAccount.equals("Saving Account")) {
                savingRadio.setSelected(true);
                onAccountTypeSelected("Saving");
            } else if (selectedAccount.equals("Checking Account")) {
                checkingRadio.setSelected(true);
                onAccountTypeSelected("Checking");
            }
        }
    }

    private void enableAccountComponents(boolean enabled) {
        amountField.setEnabled(enabled);
        depositButton.setEnabled(enabled);
        withdrawButton.setEnabled(enabled);
        transferToCombo.setEnabled(enabled);
        transferAccountCombo.setEnabled(enabled);
        transferAmountField.setEnabled(enabled);
    }

    private void createAccountPanel() {
        accountPanel = new JPanel(new BorderLayout(10, 10));
        accountPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerNameLabel = new JLabel("Select an account to begin");
        customerNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        customerNameLabel.setForeground(new Color(33, 33, 33));
        topPanel.add(customerNameLabel);
        accountPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        JPanel operationsPanel = new JPanel(new BorderLayout(10, 10));
        operationsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(158, 158, 158), 2),
                "Account Operations",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        operationsPanel.setBackground(Color.WHITE);

        JPanel accountTypePanel = new JPanel(new GridLayout(3, 1, 5, 10));
        accountTypePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        accountTypePanel.setBackground(Color.WHITE);

        JLabel selectAccountLabel = new JLabel("Account Type:");
        selectAccountLabel.setFont(new Font("Arial", Font.BOLD, 13));

        savingRadio = new JRadioButton("Saving Account");
        checkingRadio = new JRadioButton("Checking Account");
        savingRadio.setFont(new Font("Arial", Font.PLAIN, 13));
        checkingRadio.setFont(new Font("Arial", Font.PLAIN, 13));
        savingRadio.setBackground(Color.WHITE);
        checkingRadio.setBackground(Color.WHITE);

        ButtonGroup accountGroup = new ButtonGroup();
        accountGroup.add(savingRadio);
        accountGroup.add(checkingRadio);

        savingRadio.addActionListener(e -> onAccountTypeSelected("Saving"));
        checkingRadio.addActionListener(e -> onAccountTypeSelected("Checking"));

        savingRadio.setEnabled(currentCustomer.hasSavingAccount());
        checkingRadio.setEnabled(currentCustomer.hasCheckingAccount());

        accountTypePanel.add(selectAccountLabel);
        accountTypePanel.add(savingRadio);
        accountTypePanel.add(checkingRadio);

        operationsPanel.add(accountTypePanel, BorderLayout.NORTH);

        JPanel transactionPanel = new JPanel(new GridLayout(6, 1, 5, 15));
        transactionPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        transactionPanel.setBackground(Color.WHITE);

        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setForeground(new Color(46, 125, 50));

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 13));
        amountField = new JTextField(10);
        amountField.setFont(new Font("Arial", Font.PLAIN, 14));
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 189, 189), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        depositButton = new JButton("Deposit");
        withdrawButton = new JButton("Withdraw");

        // Style Deposit Button
        depositButton.setFont(new Font("Arial", Font.BOLD, 14));
        depositButton.setBackground(new Color(67, 160, 71));
        depositButton.setForeground(Color.WHITE);
        depositButton.setFocusPainted(false);
        depositButton.setBorderPainted(false);
        depositButton.setPreferredSize(new Dimension(120, 40));
        depositButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Style Withdraw Button
        withdrawButton.setFont(new Font("Arial", Font.BOLD, 14));
        withdrawButton.setBackground(new Color(229, 57, 53));
        withdrawButton.setForeground(Color.WHITE);
        withdrawButton.setFocusPainted(false);
        withdrawButton.setBorderPainted(false);
        withdrawButton.setPreferredSize(new Dimension(120, 40));
        withdrawButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        depositButton.addActionListener(e -> performDeposit());
        withdrawButton.addActionListener(e -> performWithdraw());

        transactionPanel.add(balanceLabel);
        transactionPanel.add(amountLabel);
        transactionPanel.add(amountField);
        transactionPanel.add(depositButton);
        transactionPanel.add(withdrawButton);

        JPanel specialOpsPanel = new JPanel(new GridLayout(2, 1, 5, 8));
        specialOpsPanel.setBackground(Color.WHITE);
        
        addInterestButton = new JButton("Add Interest (Saving)");
        deductFeesButton = new JButton("Deduct Fees (Checking)");

        // Style Add Interest Button
        addInterestButton.setFont(new Font("Arial", Font.BOLD, 12));
        addInterestButton.setBackground(new Color(251, 140, 0));
        addInterestButton.setForeground(Color.WHITE);
        addInterestButton.setFocusPainted(false);
        addInterestButton.setBorderPainted(false);
        addInterestButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Style Deduct Fees Button
        deductFeesButton.setFont(new Font("Arial", Font.BOLD, 12));
        deductFeesButton.setBackground(new Color(156, 39, 176));
        deductFeesButton.setForeground(Color.WHITE);
        deductFeesButton.setFocusPainted(false);
        deductFeesButton.setBorderPainted(false);
        deductFeesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addInterestButton.addActionListener(e -> performAddInterest());
        deductFeesButton.addActionListener(e -> performDeductFees());

        specialOpsPanel.add(addInterestButton);
        specialOpsPanel.add(deductFeesButton);

        transactionPanel.add(specialOpsPanel);

        operationsPanel.add(transactionPanel, BorderLayout.CENTER);

        createTransferPanel();

        centerPanel.add(operationsPanel);
        centerPanel.add(transferPanel);

        accountPanel.add(centerPanel, BorderLayout.CENTER);

        enableAccountComponents(false);

        add(accountPanel, BorderLayout.CENTER);
    }

    private void performTransfer() {
        try {
            double amount = Double.parseDouble(transferAmountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String toCustomerName = (String) transferToCombo.getSelectedItem();
            String toAccountType = (String) transferAccountCombo.getSelectedItem();

            if (toCustomerName == null) {
                JOptionPane.showMessageDialog(this, "Select a customer to transfer to", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CustomerAccount toCustomer = customers.get(toCustomerName);

            BankAccount fromAccount = selectedAccountType.equals("Saving")
                    ? currentCustomer.getSavingAccount()
                    : currentCustomer.getCheckingAccount();

            BankAccount toAccount = toAccountType.equals("Saving")
                    ? toCustomer.getSavingAccount()
                    : toCustomer.getCheckingAccount();

            if (toAccount == null) {
                JOptionPane.showMessageDialog(this,
                        toCustomerName + " doesn't have a " + toAccountType + " account",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                fromAccount.transfer(toAccount, amount);
            } catch (InsufficientFundsException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                return;
            }

            updateAccountInDatabase(currentCustomer, selectedAccountType);
            updateAccountInDatabase(toCustomer, toAccountType);

            String description = String.format("Transferred to %s's %s", toCustomerName, toAccountType);
            customerDAO.logTransaction(currentCustomer.getName(), selectedAccountType,
                    "TRANSFER_OUT", amount, description);

            description = String.format("Received from %s's %s", currentCustomer.getName(), selectedAccountType);
            customerDAO.logTransaction(toCustomerName, toAccountType,
                    "TRANSFER_IN", amount, description);

            logTransaction(String.format("Transferred $%.2f from your %s to %s's %s",
                    amount, selectedAccountType, toCustomerName, toAccountType));
            updateBalanceDisplay();
            transferAmountField.setText("");

            JOptionPane.showMessageDialog(this,
                    String.format("Successfully transferred $%.2f to %s", amount, toCustomerName),
                    "Transfer Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performAddInterest() {
        if (currentCustomer != null && currentCustomer.hasSavingAccount()) {
            double oldBalance = currentCustomer.getSavingBalance();
            currentCustomer.getSavingAccount().addPeriodicInterest();
            double newBalance = currentCustomer.getSavingBalance();
            double interest = newBalance - oldBalance;

            updateAccountInDatabase(currentCustomer, "Saving");
            customerDAO.logTransaction(currentCustomer.getName(), "Saving",
                    "INTEREST", interest, "Periodic interest added");

            logTransaction(String.format("Added interest $%.2f to your Saving account", interest));
            updateBalanceDisplay();
        }
    }

    private void performDeductFees() {
        try {
            if (currentCustomer != null && currentCustomer.hasCheckingAccount()) {
                currentCustomer.getCheckingAccount().deductFees();

                updateAccountInDatabase(currentCustomer, "Checking");
                customerDAO.logTransaction(currentCustomer.getName(), "Checking",
                        "FEE", 0, "Transaction fees deducted");

                logTransaction("Deducted fees from your Checking account");
                updateBalanceDisplay();
            }
        } catch (InsufficientFundsException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void performDeposit() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedAccountType.equals("Saving")) {
                currentCustomer.getSavingAccount().deposit(amount);
            } else {
                currentCustomer.getCheckingAccount().deposit(amount);
            }

            updateAccountInDatabase(currentCustomer, selectedAccountType);
            customerDAO.logTransaction(currentCustomer.getName(), selectedAccountType,
                    "DEPOSIT", amount, "Deposit");

            logTransaction(String.format("Deposited $%.2f to your %s account", amount, selectedAccountType));
            updateBalanceDisplay();
            amountField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logTransaction(String message) {
        String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        transactionLog.append(String.format("[%s] %s\n", timestamp, message));
        transactionLog.setCaretPosition(transactionLog.getDocument().getLength());
    }

    private void performWithdraw() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedAccountType.equals("Saving")) {
                currentCustomer.getSavingAccount().withdraw(amount);
            } else {
                currentCustomer.getCheckingAccount().withdraw(amount);
            }

            updateAccountInDatabase(currentCustomer, selectedAccountType);
            customerDAO.logTransaction(currentCustomer.getName(), selectedAccountType,
                    "WITHDRAW", amount, "Withdrawal");

            logTransaction(String.format("Withdrew $%.2f from your %s account", amount, selectedAccountType));
            updateBalanceDisplay();
            amountField.setText("");
        } catch (InsufficientFundsException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAccountInDatabase(CustomerAccount customer, String accountType) {
        double balance;
        int transactionCount;

        if (accountType.equals("Saving")) {
            balance = customer.getSavingBalance();
            transactionCount = customer.getSavingAccount().getTransactionCount();
        } else {
            balance = customer.getCheckingBalance();
            transactionCount = customer.getCheckingAccount().getTransactionCount();
        }

        customerDAO.updateAccountBalance(customer.getName(), accountType, balance, transactionCount);
    }

    private void onAccountTypeSelected(String accountType) {
        if (currentCustomer == null) {
            return;
        }

        selectedAccountType = accountType;
        customerNameLabel.setText("Account: " + accountType);
        updateBalanceDisplay();
        enableAccountComponents(true);
        transferButton.setEnabled(true);

        addInterestButton.setEnabled(accountType.equals("Saving") && currentCustomer.hasSavingAccount());
        deductFeesButton.setEnabled(accountType.equals("Checking") && currentCustomer.hasCheckingAccount());
    }

    private void updateBalanceDisplay() {
        if (currentCustomer != null && selectedAccountType != null) {
            double balance = selectedAccountType.equals("Saving")
                    ? currentCustomer.getSavingBalance()
                    : currentCustomer.getCheckingBalance();
            balanceLabel.setText(String.format("Balance: $%.2f", balance));
        }
    }

    private void createTransferPanel() {
        transferPanel = new JPanel(new BorderLayout(10, 10));
        transferPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(158, 158, 158), 2),
                "Transfer Money to Others",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        transferPanel.setBackground(Color.WHITE);

        JPanel transferFormPanel = new JPanel(new GridLayout(7, 1, 5, 12));
        transferFormPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        transferFormPanel.setBackground(Color.WHITE);

        JLabel transferToLabel = new JLabel("Transfer To Customer:");
        transferToLabel.setFont(new Font("Arial", Font.BOLD, 13));

        transferToCombo = new JComboBox<>();
        transferToCombo.setFont(new Font("Arial", Font.PLAIN, 13));

        for (String customerName : customers.keySet()) {
            if (!customerName.equals(currentCustomer.getName())) {
                transferToCombo.addItem(customerName);
            }
        }

        JLabel transferAccountLabel = new JLabel("To Account Type:");
        transferAccountLabel.setFont(new Font("Arial", Font.BOLD, 13));

        transferAccountCombo = new JComboBox<>(new String[]{"Saving", "Checking"});
        transferAccountCombo.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel transferAmountLabel = new JLabel("Transfer Amount:");
        transferAmountLabel.setFont(new Font("Arial", Font.BOLD, 13));

        transferAmountField = new JTextField(10);
        transferAmountField.setFont(new Font("Arial", Font.PLAIN, 14));
        transferAmountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 189, 189), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        transferButton = new JButton("Transfer");
        transferButton.setFont(new Font("Arial", Font.BOLD, 14));
        transferButton.setBackground(new Color(30, 136, 229));
        transferButton.setForeground(Color.WHITE);
        transferButton.setFocusPainted(false);
        transferButton.setBorderPainted(false);
        transferButton.setPreferredSize(new Dimension(120, 40));
        transferButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        transferButton.addActionListener(e -> performTransfer());

        transferFormPanel.add(transferToLabel);
        transferFormPanel.add(transferToCombo);
        transferFormPanel.add(transferAccountLabel);
        transferFormPanel.add(transferAccountCombo);
        transferFormPanel.add(transferAmountLabel);
        transferFormPanel.add(transferAmountField);
        transferFormPanel.add(transferButton);

        transferPanel.add(transferFormPanel, BorderLayout.CENTER);
    }

    private void createTransactionLogPanel() {
        JPanel logPanel = new JPanel(new BorderLayout(5, 5));
        logPanel.setBorder(new EmptyBorder(0, 10, 10, 10));
        logPanel.setPreferredSize(new Dimension(0, 150));
        logPanel.setBackground(Color.WHITE);

        JLabel logLabel = new JLabel("Transaction Log");
        logLabel.setFont(new Font("Arial", Font.BOLD, 15));
        logLabel.setForeground(new Color(33, 33, 33));
        logPanel.add(logLabel, BorderLayout.NORTH);

        transactionLog = new JTextArea(5, 20);
        transactionLog.setEditable(false);
        transactionLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        transactionLog.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(transactionLog);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 189, 189), 1));
        logPanel.add(scrollPane, BorderLayout.CENTER);

        add(logPanel, BorderLayout.SOUTH);
    }
}