package com.mad.assignment3.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.assignment3.Adapters.HouseholdAdapter;
import com.mad.assignment3.Models.Household;
import com.mad.assignment3.Models.User;
import com.mad.assignment3.R;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.internal.FirebaseAppHelper.getUid;

public class HouseholdActivity extends AppCompatActivity {

    private DatabaseReference mReference;
    private ProgressDialog mDialog;
    private String mHouseholdName;
    private FirebaseAuth mAuth;
    private ArrayList<User> mUsers;
    private DatabaseReference mUserReference;
    private ArrayList<Household> mHouseholds;
    private RecyclerView mRecyclerView;
    private HouseholdAdapter mAdapter;

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("users");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    Log.d("TAG", user.toString());
                    mUsers.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        DatabaseReference householdRef = rootRef.child("households");
        ValueEventListener houseListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mHouseholds = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Household household = ds.getValue(Household.class);
                    Log.d("TAG", household.toString());
                    mHouseholds.add(household);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        householdRef.addValueEventListener(houseListener);
        usersRef.addListenerForSingleValueEvent(eventListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mHouseholdName = mAuth.getCurrentUser().getDisplayName();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog(HouseholdActivity.this);
            }
        });


        mRecyclerView = findViewById(R.id.household_recycler_view);
        mAdapter = new HouseholdAdapter(this, mHouseholds);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

    }

    private void showAddDialog(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View addHouseholdView = layoutInflater.inflate(R.layout.dialog_create_household, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(addHouseholdView);

        final EditText householdNameEt = addHouseholdView.findViewById(R.id.household_name_edit_text);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Create",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mHouseholdName = householdNameEt.getText().toString();
                                new AddHouseholdAsync(HouseholdActivity.this).execute();
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


    @Override
    protected void onPause() {
        super.onPause();

        if (mDialog != null ) {
            mDialog.dismiss();
            mDialog = null;
        }
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
                mReference.child("households").child(mHouseholdName).setValue(household);

            }

            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    }

    private Household generateHousehold() {
        User currentUser = null;
        for (User user : mUsers) {
            if (user.getName().equals(mAuth.getCurrentUser().getDisplayName())){
                currentUser = user;
            }
        }
        return new Household(mHouseholdName, currentUser);
    }


}

