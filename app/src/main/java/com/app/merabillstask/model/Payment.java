package com.app.merabillstask.model;

public class Payment {
    private String type;
    private double amount;
    private String provider;
    private String transactionRef;

    public Payment(String type, double amount, String provider, String transactionRef) {
        this.type = type;
        this.amount = amount;
        this.provider = provider;
        this.transactionRef = transactionRef;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getProvider() {
        return provider;
    }

    public String getTransactionRef() {
        return transactionRef;
    }
}
