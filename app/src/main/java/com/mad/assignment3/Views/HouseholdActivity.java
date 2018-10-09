package com.mad.assignment3.Views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import com.mad.assignment3.Models.Household;
import com.mad.assignment3.Models.User;
import com.mad.assignment3.Presenters.HouseholdActivityPresenter;
import com.mad.assignment3.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HouseholdActivity extends AppCompatActivity {

    private DatabaseReference mReference;
    private ProgressDialog mDialog;
    private String mHouseholdName;
    private FirebaseAuth mAuth;
    private HouseholdActivityPresenter mPresenter;
    @BindView(R.id.household_recycler_view) RecyclerView mRecyclerView;


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
        ButterKnife.bind(this);

        mReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mPresenter = new HouseholdActivityPresenter(this, this, mAuth, mReference);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.showAddDialog();
            }
        });

        DatabaseReference householdRef = FirebaseDatabase.getInstance().getReference().child("households");
        ValueEventListener houseListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mHouseholds = mPresenter.setupLists(dataSnapshot);
                mPresenter.setupRecycler(HouseholdActivity.this, mHouseholds, mRecyclerView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
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

        if (id == R.id.logout_menu_item) {
            mPresenter.signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

