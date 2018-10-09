package com.mad.assignment3.RecyclerViewAdapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.mad.assignment3.Models.Household;
import com.mad.assignment3.Models.User;
import com.mad.assignment3.R;
import com.mad.assignment3.Views.HouseholdActivity;
import com.mad.assignment3.Views.ShoppingListActivity;

import java.util.ArrayList;

public class HouseholdAdapter extends RecyclerView.Adapter<HouseholdAdapter.ViewHolder> {

    private ArrayList<Household> mHouseholdList;
    private Context mContext;
    private ArrayList<User> mUsers;
    private String mHouse;

    public HouseholdAdapter(Context context, ArrayList<Household> list){
        this.mContext = context;
        this.mHouseholdList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.household_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Household household = mHouseholdList.get(position);
        holder.houseName.setText(household.getName());

        mUsers = household.getUsers();
        StringBuilder name = new StringBuilder();
        for (User user : mUsers) {
            name.append(user.getName()).append("    ");
        }
        holder.users.setText(name.toString());

        holder.key.setText(household.getFirebaseKey());
        Log.d("asdokfnijsadnf", holder.key.getText().toString());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(v, household.getFirebaseKey());
            }
        });
    }

    private void goToActivity(View view, String key) {
        Intent intent = new Intent(mContext, ShoppingListActivity.class);
        intent.putExtra(ShoppingListActivity.HOUSEHOLD_KEY, key);

        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mHouseholdList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView houseName, users, key;
        public LinearLayout layout;

        ViewHolder(View itemView) {
            super(itemView);
            houseName = itemView.findViewById(R.id.household_name_tv);
            users = itemView.findViewById(R.id.household_users_tv);
            key = itemView.findViewById(R.id.house_key);
            layout = itemView.findViewById(R.id.household_recycler);
        }
    }

}