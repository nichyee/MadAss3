package com.mad.assignment3.Models;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Household {

    private String mName;
    private ArrayList<User> mUsers;
    private List<Item> mShoppingList;

    public Household(String name, User user) {
        this.mName = name;
        mUsers = new ArrayList<>();
        mUsers.add(user);
    }

    public Household(){

    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ArrayList<User> getUsers() {
        return mUsers;
    }

    public void setUsers(ArrayList<User> users) {
        mUsers = users;
    }

    public List<Item> getShoppingList() {
        return mShoppingList;
    }

    public void setShoppingList(List<Item> shoppingList) {
        mShoppingList = shoppingList;
    }


}
