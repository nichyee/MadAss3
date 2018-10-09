package com.mad.assignment3.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.assignment3.Models.Household;
import com.mad.assignment3.Models.Item;
import com.mad.assignment3.Models.User;
import com.mad.assignment3.R;
import com.mad.assignment3.RecyclerViewAdapters.HouseholdAdapter;
import com.mad.assignment3.RecyclerViewAdapters.ItemAdapter;
import com.mad.assignment3.RecyclerViewAdapters.UserAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ShoppingListActivity extends AppCompatActivity {

    public static final String HOUSEHOLD_KEY = "";

    private ProgressDialog mDialog;
    private DatabaseReference mReference;
    private String mItemName;
    private String mItemQuantity;
    private FirebaseAuth mAuth;
    private String mHouseholdName;

    private ArrayList<Item> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mHouseholdName = intent.getStringExtra(HOUSEHOLD_KEY);
        Log.d("ASDASDSADSADASD", mHouseholdName);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog(ShoppingListActivity.this);
            }
        });


        mReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference itemRef = mReference.child("households").child(mHouseholdName).child("shoppingList");
        ValueEventListener itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mItems = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Item item = ds.getValue(Item.class);
                    mItems.add(item);
                }

                for (Item item : mItems) {
                    Log.d("TAG", item.getName());
                }

                RecyclerView recyclerView = findViewById(R.id.shopping_list_recycler_view);

                ItemAdapter adapter = new ItemAdapter(ShoppingListActivity.this, mItems);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        itemRef.addValueEventListener(itemListener);
    }

    private void showAddDialog(Context context) {
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
                                mItemName = itemNameEditText.getText().toString();
                                mItemQuantity = itemQuantityEditText.getText().toString();
                                new AddItemAsync(ShoppingListActivity.this).execute();
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

    @SuppressLint("StaticFieldLeak")
    private class AddItemAsync extends AsyncTask<Void, Void, Item> {

        private AddItemAsync(Activity activity) {
            mDialog = new ProgressDialog(activity);
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
                mReference.child("households").child(mHouseholdName).child("shoppingList").push().setValue(item);
            }

            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shopping_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_member_item) {
            ArrayList<User> users = new ArrayList<>();
            DatabaseReference databaseReference = mReference.child("users");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

                    addMemberDialog(ShoppingListActivity.this, users);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            return true;
        } else if (id == R.id.remove_all_item) {
            Log.d("AOSDOASIJD", "ok");
            DatabaseReference itemRef = mReference.child("households").child(mHouseholdName).child("shoppingList");
            itemRef.removeValue();
            Toast toast = Toast.makeText(this, "shopping list cleared", Toast.LENGTH_LONG);
            toast.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addMemberDialog(Context context, ArrayList<User> users) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View addItemView = layoutInflater.inflate(R.layout.dialog_add_member, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(addItemView);

        RecyclerView recyclerView = addItemView.findViewById(R.id.add_member_recycler);

        UserAdapter adapter = new UserAdapter(ShoppingListActivity.this, users, mHouseholdName);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ShoppingListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        alertDialogBuilder.setCancelable(true);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
