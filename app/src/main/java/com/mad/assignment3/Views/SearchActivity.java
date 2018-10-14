package com.mad.assignment3.Views;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.mad.assignment3.Models.Recipe;
import com.mad.assignment3.R;
import com.mad.assignment3.RecyclerViewAdapters.RetroRecipeAdapter;
import com.mad.assignment3.RetrofitFiles.GetDataService;
import com.mad.assignment3.RetrofitFiles.RetroRecipe;
import com.mad.assignment3.RetrofitFiles.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.search_recycler_view) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<RetroRecipe> call = service.getAllRecipes();
        Log.d("ASDASDSADA", "hello" + call.toString());
        findViewById(R.id.search_recycler_view);
        call.enqueue(new Callback<RetroRecipe>() {
            @Override
            public void onResponse(@NonNull Call<RetroRecipe> call, @NonNull Response<RetroRecipe> response) {
                Integer respond = response.code();
                Log.d("ASDASDSAD", respond.toString());
                Log.d("ASOKDNJASND", response.toString());
                Log.d("ASDKOJOASJD", call.toString());
                generateDataList(response.body());
            }

            @Override
            public void onFailure(Call<RetroRecipe> call, Throwable t) {
                Log.d("ASDASDSAD", t.getMessage());
                Toast.makeText(SearchActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void generateDataList(RetroRecipe recipeList) {
        Log.d("ASDASDASD", recipeList.getRecipes().get(0).toString());
        Log.d("CLASS", recipeList.getRecipes().get(0).getClass().toString());
        Log.d("COUNT", String.valueOf(recipeList.getCount()));

        List<Recipe> recipes= new ArrayList<>();

        recipes.addAll(recipeList.getRecipes());

        RetroRecipeAdapter adapter = new RetroRecipeAdapter(this, recipes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
    }
}
