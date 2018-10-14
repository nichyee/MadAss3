package com.mad.assignment3.Presenters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mad.assignment3.Models.Recipe;
import com.mad.assignment3.Models.RetroRecipe;
import com.mad.assignment3.RecyclerViewAdapters.RetroRecipeAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivityPresenter {

    private Context mContext;
    private RecyclerView mRecyclerView;

    public SearchActivityPresenter(Context context, RecyclerView recyclerView) {
        this.mContext = context;
        this.mRecyclerView = recyclerView;
    }

    /**
     * This method generates a list of data from the web service and displays it through a recycler view
     * @param recipeList the list gathered from the web service
     */
    public void generateDataList(RetroRecipe recipeList) {

        List<Recipe> recipes = new ArrayList<>(recipeList.getRecipes());

        RetroRecipeAdapter adapter = new RetroRecipeAdapter(mContext, recipes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
    }

}
