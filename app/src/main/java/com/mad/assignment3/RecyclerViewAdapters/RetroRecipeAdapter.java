package com.mad.assignment3.RecyclerViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mad.assignment3.Models.Recipe;
import com.mad.assignment3.R;
import com.mad.assignment3.RetrofitFiles.RetroRecipe;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.List;

public class RetroRecipeAdapter extends RecyclerView.Adapter<RetroRecipeAdapter.CustomViewHolder> {

    private List<Recipe> mDataList;
    private Context mContext;

    public RetroRecipeAdapter(Context context, List<Recipe> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.search_result_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        final Recipe recipe= mDataList.get(position);

        holder.name.setText(recipe.getTitle());
        holder.code.setText(recipe.getPublisher());
        holder.rating.setText(new StringBuilder().append("Recipe Rating: ").append(String.valueOf(recipe.getSocialRank())).toString());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWebsite(recipe.getSourceURL());
            }
        });
    }

    private void gotoWebsite(URL url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
        mContext.startActivity(browserIntent);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView name, rating, code;
        public RelativeLayout layout;

        CustomViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recipe_name_text);
            rating = itemView.findViewById(R.id.rank_text);
            code = itemView.findViewById(R.id.recipe_id_text);
            layout = itemView.findViewById(R.id.recipe_result_item);
        }
    }
}
