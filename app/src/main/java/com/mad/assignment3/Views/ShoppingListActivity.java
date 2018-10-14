package com.mad.assignment3.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.assignment3.Models.Item;
import com.mad.assignment3.Models.User;
import com.mad.assignment3.Presenters.ShoppingListActivityPresenter;
import com.mad.assignment3.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingListActivity extends AppCompatActivity {

    public static final String HOUSEHOLD_KEY = "";

    private ProgressDialog mDialog;
    private DatabaseReference mReference;
    private String mItemName;
    private String mItemQuantity;
    private FirebaseAuth mAuth;
    private String mHouseholdName;
    private ShoppingListActivityPresenter mPresenter;
    private ArrayList<User> mCurrentUsers;

    @BindView(R.id.shopping_list_recycler_view) RecyclerView mRecyclerView;

    private ArrayList<Item> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        mHouseholdName = intent.getStringExtra(HOUSEHOLD_KEY);

        mPresenter = new ShoppingListActivityPresenter(this, this, mAuth, mHouseholdName);

        mReference = FirebaseDatabase.getInstance().getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.showAddDialog(ShoppingListActivity.this);
            }
        });

        DatabaseReference itemRef = mReference.child("households").child(mHouseholdName).child("shoppingList");
        ValueEventListener itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mItems = mPresenter.setupItemList(dataSnapshot);
                mPresenter.setupRecyclerView(ShoppingListActivity.this, mItems, mRecyclerView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        itemRef.addValueEventListener(itemListener);
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
            final DatabaseReference currentUsers = mReference.child("households").child(mHouseholdName).child("users");

            currentUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mCurrentUsers = new ArrayList<>();
                    mCurrentUsers = mPresenter.setupUserList(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference databaseReference = mReference.child("users");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<User> users = mPresenter.setupNewUserList(dataSnapshot, mCurrentUsers);
                    mPresenter.addMemberDialog(ShoppingListActivity.this, users);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if (id == R.id.remove_all_item) {
            mPresenter.nukeShoppingList();
        } else if (id == R.id.search_menu_item){
            Intent intent = new Intent(ShoppingListActivity.this, SearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


}
