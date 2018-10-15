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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.assignment3.Models.Household;
import com.mad.assignment3.Models.Item;
import com.mad.assignment3.Models.User;
import com.mad.assignment3.R;
import com.mad.assignment3.RecyclerViewAdapters.HouseholdAdapter;
import com.mad.assignment3.Views.HouseholdActivity;
import com.mad.assignment3.Views.LoginActivity;
import com.mad.assignment3.Views.ShoppingListActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class HouseholdActivityPresenter {

    private static final String HOUSEHOLD_CONSTANT = "households";
    private static final String USERS_CONSTANT = "users";
    private static final String FIREBASE_KEY_CONSTANT = "firebaseKey";
    private static final String NAME_CONSTANT = "name";
    private static final String EMAIL_CONSTANT = "email";
    private static final String SHOPPING_LIST_CONSTANT = "shoppingList";
    private static final String AMOUNT_CONSTANT = "amount";


    private ProgressDialog mDialog;
    private View mView;
    private Activity mActivity;
    private String mHouseholdName;
    private FirebaseAuth mAuth;
    private ArrayList<User> mUsers;
    private DatabaseReference mReference;
    private Context mContext;
    private User mCurrentUser;

    public HouseholdActivityPresenter(Context context, Activity activity, FirebaseAuth auth, DatabaseReference ref){
        this.mContext = context;
        this.mActivity = activity;
        this.mAuth = auth;
        this.mReference = ref;
        this.mUsers = new ArrayList<>();
    }

    /**
     * This method generates the dialog used to create a new household
     */
    public void showAddDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View addHouseholdView = layoutInflater.inflate(R.layout.dialog_create_household, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setView(addHouseholdView);

        final EditText householdNameEt = addHouseholdView.findViewById(R.id.household_name_edit_text);
        mHouseholdName = householdNameEt.getText().toString();

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.create,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mHouseholdName = householdNameEt.getText().toString();
                                new AddHouseholdAsync(mActivity).execute();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * This method is called when the user wishes to sign out
     */
    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(mContext, LoginActivity.class);
        mActivity.startActivity(intent);
    }

    /**
     * This Async Task is called when a user attempts to generate a new household
     */
    @SuppressLint("StaticFieldLeak")
    private class AddHouseholdAsync extends AsyncTask<Void, Void, Household> {

        private AddHouseholdAsync(Activity activity) {
            mDialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage(mActivity.getString(R.string.please_waits));
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
                DatabaseReference ref = mReference.child(HOUSEHOLD_CONSTANT).push();

                ref.setValue(household);
                ref.child(USERS_CONSTANT).push().setValue(mCurrentUser);
                ref.child(FIREBASE_KEY_CONSTANT).setValue(ref.getKey());

            }

            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    }

    /**
     * This method is called in the Async task to create a new household object
     * @return this returns a newly generated household object
     */
    private Household generateHousehold() {
        String name = mAuth.getCurrentUser().getDisplayName();
        String email = mAuth.getCurrentUser().getEmail();

        mCurrentUser = new User(name, email);

        return new Household(mHouseholdName);
    }

    /**
     * This method creates the list of households to be used in the Recycler View
     * @param dataSnapshot a DataSnapshot object generated from Firebase
     * @return a list of households created from Firebase information
     */
    public ArrayList<Household> setupLists(DataSnapshot dataSnapshot) {
        ArrayList<Household> households = new ArrayList<>();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            HashMap hashMap = (HashMap) data.getValue();

            ArrayList<User> users = new ArrayList<>();
            ArrayList<Item> items = new ArrayList<>();

            HashMap userHashMap = (HashMap) hashMap.get(USERS_CONSTANT);
            if (userHashMap != null) {
                for (Object object : userHashMap.values()) {
                    HashMap temp = (HashMap) object;
                    String name = temp.get(NAME_CONSTANT).toString();
                    String email = temp.get(EMAIL_CONSTANT).toString();
                    User user = new User(name, email);
                    users.add(user);
                }
            }

            HashMap shoppingListHashMap = (HashMap) hashMap.get(SHOPPING_LIST_CONSTANT);
            if (shoppingListHashMap != null) {
                for (Object object : shoppingListHashMap.values()) {
                    HashMap temp = (HashMap) object;
                    String name = temp.get(NAME_CONSTANT).toString();
                    String amount = temp.get(AMOUNT_CONSTANT).toString();
                    Item item = new Item(name, amount);
                    items.add(item);
                }
            }

            String householdName = hashMap.get(NAME_CONSTANT).toString();
            Household household;

            if (hashMap.get(FIREBASE_KEY_CONSTANT) != null) {
                String firebaseKey = hashMap.get(FIREBASE_KEY_CONSTANT).toString();
                household = new Household(householdName, users, items, firebaseKey);
            } else {
                household = new Household(householdName, users, items);
            }

            for (User user : users) {
                if (user.getEmail().equals(mAuth.getCurrentUser().getEmail())) {
                    households.add(household);
                }
            }
        }
        return households;
    }

    /**
     * This method sets up the recycler view, generating the appropriate visuals
     * @param context this is the current context that is being used
     * @param households a list of households to be presented by the recycler view
     * @param view the exact view found from the layout file
     */
    public void setupRecycler(Context context, ArrayList<Household> households, RecyclerView view){
        HouseholdAdapter adapter = new HouseholdAdapter(context, households);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        view.setLayoutManager(layoutManager);
        view.setItemAnimator(new DefaultItemAnimator());
        view.setAdapter(adapter);
    }
}
