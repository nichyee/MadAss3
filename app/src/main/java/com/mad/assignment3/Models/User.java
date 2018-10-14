package com.mad.assignment3.Models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String mName;
    private String mEmail;
    private ArrayList<Household> mHouseholds;

    public ArrayList<Household> getHouseholds() {
        return mHouseholds;
    }

    public void setHouseholds(ArrayList<Household> households) {
        mHouseholds = households;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }


    /**
     * This constructor uses two parameters to create an object
     * @param name the name of the user
     * @param email the user's email
     */
    public User(String name, String email){
        mName = name;
        mEmail = email;
        mHouseholds = new ArrayList<>();
    }

    public User(){

    }

    public void addHousehold(Household household) {
        mHouseholds.add(household);
    }



}
