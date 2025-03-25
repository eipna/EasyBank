package com.eipna.easybank.data;

public class Transaction {

    private int ID;
    private int accountID;
    private String type;
    private String date;
    private double amount;

    public Transaction() {
        this.ID = -1;
        this.accountID = -1;
        this.type = null;
        this.date = null;
        this.amount = -1;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}