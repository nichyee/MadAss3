package com.mad.assignment3.RecyclerViewAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.assignment3.Models.User;
import com.mad.assignment3.R;

import java.util.ArrayList;

public class UserAdapter extends  RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private static final String HOUSEHOLD_CONSTANT = "households";
    private static final String USERS_CONSTANT = "users";

    private ArrayList<User> mUsers;
    private String mFirebaseKey;
    private Context mContext;

    public UserAdapter(Context context, ArrayList<User> list, String key){
        this.mContext = context;
        this.mUsers = list;
        this.mFirebaseKey = key;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_item, parent, false);
        return new UserAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = mUsers.get(position);

        holder.userName.setText(user.getName());
        holder.userEmail.setText(user.getEmail());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser(user);
                holder.layout.setVisibility(View.GONE);
            }
        });

    }

    /**
     * This method adds a user to the household
     * @param user the user that is to be added to the household
     */
    private void addUser(User user) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(HOUSEHOLD_CONSTANT).child(mFirebaseKey).child(USERS_CONSTANT);
        databaseReference.push().setValue(user);

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, userEmail;
        public LinearLayout layout;

        private ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.member_name);
            userEmail = itemView.findViewById(R.id.member_email);
            layout = itemView.findViewById(R.id.member_linear_layout);
        }
    }
}
