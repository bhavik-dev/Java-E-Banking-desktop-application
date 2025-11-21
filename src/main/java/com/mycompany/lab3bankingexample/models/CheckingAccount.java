package com.mycompany.lab3bankingexample.models;

import com.mycompany.lab3bankingexample.customexception.InsufficientFundsException;

public class CheckingAccount extends BankAccount {

    private static final int FREE_TRANSACTIONS = 3;
    private static final double TRANSACTION_FEE = 0.50;
    private int transactionCount;

    public CheckingAccount() {
        transactionCount = 0;
    }

    public CheckingAccount(double initialAmount) {
        super(initialAmount);
        transactionCount = 0;
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        super.withdraw(amount);
        transactionCount++;
    }

    public void deposit(double amount) {
        super.deposit(amount);
        transactionCount++;
    }

    public void deductFees() throws InsufficientFundsException {
        if (transactionCount > FREE_TRANSACTIONS) {
            double fee = TRANSACTION_FEE * (transactionCount - FREE_TRANSACTIONS);
            super.withdraw(fee);
        }
        transactionCount = 0;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }
}