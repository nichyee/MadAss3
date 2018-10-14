package com.mad.assignment3.RetrofitFiles;

import com.google.gson.annotations.SerializedName;
import com.mad.assignment3.Models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RetroRecipe {

    @SerializedName("count")
    private int count;

    @SerializedName("recipes")
    private List<Recipe> recipes;

    public RetroRecipe(int count, List<Recipe> list){
        this.count = count;
        this.recipes = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}
