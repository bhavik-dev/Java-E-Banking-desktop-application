package com.mycompany.lab3bankingexample.models;

public class CustomerAccount {

    private String name;
    private SavingAccount savingAccount;
    private CheckingAccount checkingAccount;

    public CustomerAccount(String name, double savingBalance, double checkingBalance, double interestRate) {
        this.name = name;
        if (savingBalance > 0) {
            this.savingAccount = new SavingAccount(interestRate, savingBalance);
        }
        if (checkingBalance > 0) {
            this.checkingAccount = new CheckingAccount(checkingBalance);
        }
    }

    public String getName() {
        return name;
    }

    public SavingAccount getSavingAccount() {
        return savingAccount;
    }

    public CheckingAccount getCheckingAccount() {
        return checkingAccount;
    }

    public boolean hasSavingAccount() {
        return savingAccount != null;
    }

    public boolean hasCheckingAccount() {
        return checkingAccount != null;
    }

    public double getSavingBalance() {
        return savingAccount != null ? savingAccount.getBalance() : 0;
    }

    public double getCheckingBalance() {
        return checkingAccount != null ? checkingAccount.getBalance() : 0;
    }

}