package com.mad.assignment3.Models;

public class Item {

    private String mName;
    private String mAmount;

    public Item(String name, String amount){
        mName = name;
        mAmount = amount;
    }

    public Item(){}

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String amount) {
        mAmount = amount;
    }

}
