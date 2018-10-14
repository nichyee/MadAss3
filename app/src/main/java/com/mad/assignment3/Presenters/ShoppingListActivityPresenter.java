package com.mad.assignment3.Presenters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.assignment3.Models.Household;
import com.mad.assignment3.Models.Item;
import com.mad.assignment3.Models.User;
import com.mad.assignment3.R;
import com.mad.assignment3.RecyclerViewAdapters.ItemAdapter;
import com.mad.assignment3.RecyclerViewAdapters.UserAdapter;
import com.mad.assignment3.Views.HouseholdActivity;
import com.mad.assignment3.Views.ShoppingListActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ShoppingListActivityPresenter {

    private static final String HOUSEHOLD_CHILD =  "households";
    private static final String SHOPPING_LIST_CHILD = "shoppingList";

    private Context mContext;
    private Activity mActivity;
    private FirebaseAuth mAuth;
    private ArrayList<User> mUsers;
    private ArrayList<Household> mHouseholds;
    private String mHouseholdKey;
    private DatabaseReference mReference;

    public ShoppingListActivityPresenter(Context context, Activity activity, FirebaseAuth auth, String householdKey){
        this.mContext = context;
        this.mActivity = activity;
        this.mAuth = auth;
        this.mHouseholdKey = householdKey;
        this.mReference = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * This method is called when the user attempts to add more users to a household
     * @param context the current context of the application
     * @param users a list of users available to be added to the household
     */
    public void addMemberDialog(Context context, ArrayList<User> users) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View addItemView = layoutInflater.inflate(R.layout.dialog_add_member, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(addItemView);

        RecyclerView recyclerView = addItemView.findViewById(R.id.add_member_recycler);

        UserAdapter adapter = new UserAdapter(mContext, users, mHouseholdKey);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        alertDialogBuilder.setCancelable(true);

        AlertDialog alertDialog = alertDialogBuilder.create();


        alertDialog.show();
    }

    /**
     * This method sets up a list of all users that are available to be added to the household
     * @param dataSnapshot an object received from Firebase, containing a list of users
     * @param currentUsers a list of all users currently associated with the household
     * @return a list of users that can be added to the household
     */
    public ArrayList<User> setupNewUserList(DataSnapshot dataSnapshot, ArrayList<User> currentUsers) {
        ArrayList<User> users = new ArrayList<>();
        HashMap hashMap = (HashMap) dataSnapshot.getValue();
        if (hashMap != null) {
            for (Object object : hashMap.values()) {
                HashMap temp = (HashMap) object;
                String name = temp.get("name").toString();
                String email = temp.get("email").toString();
                User newUser = new User(name, email);
                users.add(newUser);
            }
        }
        for (User user : currentUsers) {
            for (int i = 0; i < users.size(); i++){
                if (user.getName().equals(users.get(i).getName())) {
                    users.remove(i);
                }
            }
        }
        return users;
    }

    /**
     * This method generates a list of all currently registered users
     * @param dataSnapshot an object received from Firebase, containing a list of users
     * @return a list of all currently registered users
     */
    public ArrayList<User> setupUserList(DataSnapshot dataSnapshot) {
        ArrayList<User> users = new ArrayList<>();
        HashMap hashMap = (HashMap) dataSnapshot.getValue();
        if (hashMap != null) {
            for (Object object : hashMap.values()) {
                HashMap temp = (HashMap) object;
                String name = temp.get("name").toString();
                String email = temp.get("email").toString();
                User user = new User(name, email);
                users.add(user);
            }
        }
        return users;
    }

    /**
     * This method is called when the user indicates that they have completeed their shop, and no longer needs the current items
     */
    public void nukeShoppingList() {
        DatabaseReference itemRef = mReference.child(HOUSEHOLD_CHILD).child(mHouseholdKey).child(SHOPPING_LIST_CHILD);
        itemRef.removeValue();
        Toast toast = Toast.makeText(mContext, R.string.cleared_list, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * This method generates the dialog used to add items to the shopping list
     * @param context the current context of the application
     */
    public void showAddDialog(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams") View addItemView = layoutInflater.inflate(R.layout.dialog_add_new_item, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(addItemView);

        final EditText itemNameEditText = addItemView.findViewById(R.id.item_name_edit_text);
        final EditText itemQuantityEditText = addItemView.findViewById(R.id.item_quantity_edit_text);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.create,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = itemNameEditText.getText().toString();
                                String quantity = itemQuantityEditText.getText().toString();
                                if (!validateForm(itemNameEditText, itemQuantityEditText)) {
                                    Toast.makeText(mActivity, R.string.name_quantity_warning,
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }
                                new AddItemAsync(mActivity, name, quantity).execute();
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
     * This method is called to check if the inputs into the form are all valid
     * @return a boolean value, indicating whether or not the form is valid
     */
    private boolean validateForm(EditText nameEditText, EditText amountEditText) {
        boolean valid = true;

        String name = nameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError(mContext.getString(R.string.required));
            valid = false;
        } else {
            nameEditText.setError(null);
        }

        String amount = amountEditText.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            amountEditText.setError(mContext.getString(R.string.required));
            valid = false;
        } else {
            amountEditText.setError(null);
        }

        return valid;
    }

    /**
     * This method sets up the recycler view to display the information
     * @param context the current context of the application
     * @param mItems a list of shopping list items
     * @param recyclerView the target recycler view
     */
    public void setupRecyclerView(Context context, ArrayList<Item> mItems, RecyclerView recyclerView) {
        ItemAdapter adapter = new ItemAdapter(context, mItems);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    /**
     * This method generates a list of shopping list items
     * @param dataSnapshot the data from Firebase
     * @return a list of shopping list items attached to the household
     */
    public ArrayList<Item> setupItemList(DataSnapshot dataSnapshot) {
        ArrayList<Item> items = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Item item = ds.getValue(Item.class);
            items.add(item);
        }
        return items;
    }

    /**
     * This Async Task is called when a user attempts to add a shopping list item
     */
    @SuppressLint("StaticFieldLeak")
    private class AddItemAsync extends AsyncTask<Void, Void, Item> {

        private ProgressDialog mDialog;
        private String mItemName;
        private String mItemQuantity;

        private AddItemAsync(Activity activity, String name, String quantity) {
            mDialog = new ProgressDialog(activity);
            mItemName = name;
            mItemQuantity = quantity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Please Wait");
            mDialog.show();
        }

        @Override
        protected Item doInBackground(Void... voids) {
            return new Item(mItemName, mItemQuantity);
        }

        @Override
        protected void onPostExecute(Item item) {
            super.onPostExecute(item);

            if (!(item == null)) {
                mReference.child("households").child(mHouseholdKey).child("shoppingList").push().setValue(item);
            }

            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    }
}
