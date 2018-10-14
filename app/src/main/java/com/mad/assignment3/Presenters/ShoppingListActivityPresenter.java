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

    public void addMemberDialog(Context context, ArrayList<User> users) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View addItemView = layoutInflater.inflate(R.layout.dialog_add_member, null);

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
        ArrayList<Integer> indexList = new ArrayList<>();
        for (User user : currentUsers) {
            for (int i = 0; i < users.size(); i++){
                if (user.getName().equals(users.get(i).getName())) {
                    users.remove(i);
                }
            }
        }
        return users;
    }

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

    public void nukeShoppingList() {
        DatabaseReference itemRef = mReference.child("households").child(mHouseholdKey).child("shoppingList");
        itemRef.removeValue();
        Toast toast = Toast.makeText(mContext, "shopping list cleared", Toast.LENGTH_LONG);
        toast.show();
    }

    public void showAddDialog(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View addItemView = layoutInflater.inflate(R.layout.dialog_add_new_item, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(addItemView);

        final EditText itemNameEditText = addItemView.findViewById(R.id.item_name_edit_text);
        final EditText itemQuantityEditText = addItemView.findViewById(R.id.item_quantity_edit_text);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Create",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = itemNameEditText.getText().toString();
                                String quantity = itemQuantityEditText.getText().toString();
                                if (!validateForm(itemNameEditText, itemQuantityEditText)) {
                                    Toast.makeText(mActivity, "Please make sure both name and quantity are filled out",
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }
                                new AddItemAsync(mActivity, name, quantity).execute();
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

    private boolean validateForm(EditText nameEditText, EditText amountEditText) {
        boolean valid = true;

        String name = nameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Required.");
            valid = false;
        } else {
            nameEditText.setError(null);
        }

        String amount = amountEditText.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            amountEditText.setError("Required");
            valid = false;
        } else {
            amountEditText.setError(null);
        }

        return valid;
    }

    public void setupRecyclerView(Context context, ArrayList<Item> mItems, RecyclerView recyclerView) {
        ItemAdapter adapter = new ItemAdapter(context, mItems);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public ArrayList<Item> setupItemList(DataSnapshot dataSnapshot) {
        ArrayList<Item> items = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Item item = ds.getValue(Item.class);
            items.add(item);
        }
        return items;
    }

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
