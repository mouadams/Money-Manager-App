package com.example.moneymanager.model;

public class Transaction {
    private int id;
    private String type;
    private double amount;
    private String reason;
    private String date;

    public Transaction(int id, String type, double amount, String reason, String date) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.reason = reason;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}