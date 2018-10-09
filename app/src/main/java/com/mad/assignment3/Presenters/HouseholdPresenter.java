package com.mad.assignment3.Presenters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.mad.assignment3.Models.Household;
import com.mad.assignment3.Models.Item;
import com.mad.assignment3.Models.User;
import com.mad.assignment3.R;
import com.mad.assignment3.RecyclerViewAdapters.HouseholdAdapter;
import com.mad.assignment3.Views.LoginActivity;
import com.mad.assignment3.Views.ShoppingListActivity;

import java.util.ArrayList;

public class HouseholdPresenter {

    private ProgressDialog mDialog;
    private View mView;
    private Activity mActivity;
    private String mHouseholdName;
    private FirebaseAuth mAuth;
    private ArrayList<User> mUsers;
    private DatabaseReference mReference;
    private Context mContext;
    private User mCurrentUser;

    public HouseholdPresenter(Context context, Activity activity, FirebaseAuth auth, DatabaseReference ref){
        this.mContext = context;
        this.mActivity = activity;
        this.mAuth = auth;
        this.mReference = ref;
        this.mUsers = new ArrayList<>();
    }

    public void goToActivity(View view) {
        TextView textView = view.findViewById(R.id.house_key);
        mHouseholdName = textView.getText().toString();

        Intent intent = new Intent(view.getContext(), ShoppingListActivity.class);
        intent.putExtra(ShoppingListActivity.HOUSEHOLD_KEY, mHouseholdName);
        mActivity.startActivity(intent);
    }

    public void setUserList(ArrayList<User> users){
        this.mUsers = users;
    }

    public void setupRecyclerView(ArrayList<Household> mHouseholds, RecyclerView recyclerView){

    }

    public void showAddDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View addHouseholdView = layoutInflater.inflate(R.layout.dialog_create_household, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setView(addHouseholdView);

        final EditText householdNameEt = addHouseholdView.findViewById(R.id.household_name_edit_text);
        mHouseholdName = householdNameEt.getText().toString();

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Create",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mHouseholdName = householdNameEt.getText().toString();
                                //mDialog.dismiss();
                                new AddHouseholdAsync(mActivity).execute();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void signOut(){
        Intent intent = new Intent(mContext, LoginActivity.class);
        mActivity.startActivity(intent);
    }

    @SuppressLint("StaticFieldLeak")
    private class AddHouseholdAsync extends AsyncTask<Void, Void, Household> {

        private AddHouseholdAsync(Activity activity) {
            mDialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Please Wait");
            mDialog.show();
        }

        @Override
        protected Household doInBackground(Void... voids) {
            return generateHousehold();
        }

        @Override
        protected void onPostExecute(Household household) {
            super.onPostExecute(household);

            if (!(household == null)) {
                DatabaseReference ref = mReference.child("households").push();

                ref.setValue(household);
                ref.child("users").push().setValue(mCurrentUser);
                ref.child("firebaseKey").setValue(ref.getKey());

            }

            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    }

    private Household generateHousehold() {
        String name = mAuth.getCurrentUser().getDisplayName();
        String email = mAuth.getCurrentUser().getEmail();

        mCurrentUser = new User(name, email);

        return new Household(mHouseholdName);
    }
}
