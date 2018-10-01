package com.mad.assignment3.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.assignment3.Models.Household;
import com.mad.assignment3.Models.User;
import com.mad.assignment3.R;
import com.mad.assignment3.Views.HouseholdActivity;

import java.util.ArrayList;
import java.util.List;

public class HouseholdAdapter extends RecyclerView.Adapter<HouseholdAdapter.ViewHolder> {

    private ArrayList<Household> mHouseholdList;
    private Context mContext;
    private ArrayList<User> mUsers;
    private DatabaseReference mUserReference;

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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Household household = mHouseholdList.get(position);
        holder.houseName.setText(household.getName());
        mUsers = household.getUsers();
        StringBuilder name = new StringBuilder();
        for (User user : mUsers) {
            name.append(user.getName()).append("    ");
        }
        holder.users.setText(name.toString());

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView houseName, users;

        ViewHolder(View itemView) {
            super(itemView);
            houseName = itemView.findViewById(R.id.household_name_tv);
            users = itemView.findViewById(R.id.household_users_tv);
        }
    }

}