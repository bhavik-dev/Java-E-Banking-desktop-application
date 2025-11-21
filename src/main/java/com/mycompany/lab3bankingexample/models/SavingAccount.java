package com.mycompany.lab3bankingexample.models;

public class SavingAccount extends BankAccount {

    private double interestRate;
    private int transactionCount;

    // constructor 1
    public SavingAccount(double rate) {
        this.interestRate = rate;
        this.transactionCount = 0;
    }

    // constructor 2
    public SavingAccount(double rate, double initialAmount) {
        super(initialAmount);
        this.interestRate = rate;
        this.transactionCount = 0;
    }

    /**
     * Add interest for the current period to the account balance.
     */
    public void addPeriodicInterest() {
        double interest = getBalance() * interestRate / 100.0;
        deposit(interest);
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

}