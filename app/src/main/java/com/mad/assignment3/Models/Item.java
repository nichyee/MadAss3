package com.mad.assignment3.Models;

public class Item {

    private String mName;
    private double mAmount;

    public Item(String name, double amount){
        mName = name;
        mAmount = amount;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double amount) {
        mAmount = amount;
    }






}
