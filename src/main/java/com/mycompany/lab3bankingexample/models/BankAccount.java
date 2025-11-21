package com.mycompany.lab3bankingexample.models;

import com.mycompany.lab3bankingexample.customexception.InsufficientFundsException;

public class BankAccount {

    private double balance;

    // constructor 1
    public BankAccount() {
        this.balance = 0.00;
    }

    // constructor 2
    public BankAccount(double initialAmount) {
        this.balance = initialAmount;
    }

    // Functions/methods
    public void deposit(double amount) {
        this.balance = this.balance + amount;
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > this.balance) {
            throw new InsufficientFundsException("Insufficient balance. Cannot withdraw " + amount);
        }
        this.balance = this.balance - amount;
    }

    public double getBalance() {
        return this.balance;
    }

    public void transfer(BankAccount other, double amount) throws InsufficientFundsException {
        this.withdraw(amount);
        other.deposit(amount);
    }

    @Override
    public String toString() {
        return "Account balance: " + this.balance;
    }

}