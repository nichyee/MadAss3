package com.mad.assignment3.RetrofitFiles;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {

    @GET("/api/search?key=07dda1f752e84987f78b06eab6a970d2")
    Call<RetroRecipe> getAllRecipes();
}
