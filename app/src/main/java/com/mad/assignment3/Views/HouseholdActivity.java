package com.mad.assignment3.Views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.assignment3.Models.Household;
import com.mad.assignment3.Models.Item;
import com.mad.assignment3.Models.User;
import com.mad.assignment3.Presenters.HouseholdPresenter;
import com.mad.assignment3.R;
import com.mad.assignment3.RecyclerViewAdapters.HouseholdAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class HouseholdActivity extends AppCompatActivity {

    private DatabaseReference mReference;
    private ProgressDialog mDialog;
    private String mHouseholdName;
    private FirebaseAuth mAuth;
    private HouseholdPresenter mPresenter;


    private ArrayList<User> mUsers = new ArrayList<>();
    private ArrayList<Household> mHouseholds = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mHouseholdName = mAuth.getCurrentUser().getDisplayName();
        final RecyclerView recyclerView = findViewById(R.id.household_recycler_view);

        mPresenter = new HouseholdPresenter(this, this, mAuth, mReference);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.showAddDialog();
            }
        });

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference householdRef = rootRef.child("households");


        ValueEventListener houseListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mHouseholds = new ArrayList<>();

                Log.d("QWERTY", dataSnapshot.toString());

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    HashMap hashMap = (HashMap) data.getValue();

                    ArrayList<User> users = new ArrayList<>();
                    ArrayList<Item> items = new ArrayList<>();

                    HashMap userHashMap = (HashMap) hashMap.get("users");
                    if (userHashMap != null) {
                        for (Object object : userHashMap.values()) {
                            HashMap temp = (HashMap) object;
                            String name = temp.get("name").toString();
                            String email = temp.get("email").toString();
                            User user = new User(name, email);
                            users.add(user);
                        }
                    }

                    HashMap shoppingListHashMap = (HashMap) hashMap.get("shoppingList");
                    if (shoppingListHashMap != null) {
                        for (Object object : shoppingListHashMap.values()) {
                            HashMap temp = (HashMap) object;
                            String name = temp.get("name").toString();
                            String amount = temp.get("amount").toString();
                            Item item = new Item(name, amount);
                            items.add(item);
                        }
                    }

                    String householdName = hashMap.get("name").toString();
                    Household household;

                    if (hashMap.get("firebaseKey") != null) {
                        String firebaseKey = hashMap.get("firebaseKey").toString();
                        household = new Household(householdName, users, items, firebaseKey);
                    } else {
                        household = new Household(householdName, users, items);
                    }

                    for (User user : users) {
                        if (user.getEmail().equals(mAuth.getCurrentUser().getEmail())) {
                            mHouseholds.add(household);
                        }
                    }

                }
                for (Household house : mHouseholds) {
                    Log.d("QWERTY", house.getName());
                }
                //mPresenter.setupRecyclerView(mHouseholds, recyclerView);

                HouseholdAdapter adapter = new HouseholdAdapter(HouseholdActivity.this, mHouseholds);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HouseholdActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        DatabaseReference userRef = rootRef.child("users");
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    mUsers.add(user);
                }
                mPresenter.setUserList(mUsers);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        userRef.addValueEventListener(userListener);
        householdRef.addValueEventListener(houseListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mDialog != null ) {
            mDialog.dismiss();
            mDialog = null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.household_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout_menu_item) {
            FirebaseAuth.getInstance().signOut();
            mPresenter.signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

