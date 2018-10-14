package com.mad.assignment3.Views;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.mad.assignment3.Models.Recipe;
import com.mad.assignment3.Presenters.SearchActivityPresenter;
import com.mad.assignment3.R;
import com.mad.assignment3.RecyclerViewAdapters.RetroRecipeAdapter;
import com.mad.assignment3.RetrofitFiles.GetDataService;
import com.mad.assignment3.Models.RetroRecipe;
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
    private SearchActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        mPresenter = new SearchActivityPresenter(this, mRecyclerView);

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Call<RetroRecipe> call = service.getAllRecipes();
        call.enqueue(new Callback<RetroRecipe>() {
            @Override
            public void onResponse(@NonNull Call<RetroRecipe> call, @NonNull Response<RetroRecipe> response) {
                mPresenter.generateDataList(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<RetroRecipe> call, @NonNull Throwable t) {
                Toast.makeText(SearchActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });


    }
}
