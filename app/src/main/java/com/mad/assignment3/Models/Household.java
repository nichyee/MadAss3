package com.mad.assignment3.Models;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Household {

    private String mName;
    private ArrayList<User> mUsers;
    private List<Item> mShoppingList;
    private String mFirebaseKey;

    public Household(String name) {
        this.mName = name;
    }

    public Household(String name, ArrayList<User> users, ArrayList<Item> items, String firebaseKey) {
        this.mName = name;
        this.mUsers = users;
        this.mShoppingList = items;
        this.mFirebaseKey = firebaseKey;
    }

    public Household(){

    }

    public Household(String householdName, ArrayList<User> users, ArrayList<Item> items) {
        this.mName = householdName;
        this.mUsers = users;
        this.mShoppingList = items;
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

    public void addUser(User user){
        mUsers.add(user);
    }


    public String getFirebaseKey() {
        return mFirebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        mFirebaseKey = firebaseKey;
    }
}
