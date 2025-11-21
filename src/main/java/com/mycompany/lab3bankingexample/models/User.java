package com.mycompany.lab3bankingexample.models;

public class User {

    private String username;
    private String password;
    private String customerName;

    public User(String username, String password, String customerName) {
        this.username = username;
        this.password = password;
        this.customerName = customerName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCustomerName() {
        return customerName;
    }

    @Override
    public String toString() {
        return "User{"
                + "username='" + username + '\''
                + ", customerName='" + customerName + '\''
                + '}';
    }

}
