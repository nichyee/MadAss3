package com.mad.assignment3.Models;

public class Item {

    private String mName;
    private String mAmount;

    /**
     * this constructor creates an item object with two parameters
     * @param name this becomes the item name
     * @param amount this is the quantity that is required
     */
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
