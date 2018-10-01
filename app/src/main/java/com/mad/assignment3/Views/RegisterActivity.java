package com.mad.assignment3.Views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.assignment3.Models.User;
import com.mad.assignment3.R;

import static com.google.firebase.internal.FirebaseAppHelper.getUid;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "LOG";
    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mNameField;

    private FirebaseUser mFirebaseUser;
    private DatabaseReference mUserRef;
    private FirebaseApp mFirebaseApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = firebaseDatabase.getReference();
        mFirebaseApp = FirebaseApp.getInstance();


        mNameField = findViewById(R.id.full_name);
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        Button submitBtn = findViewById(R.id.submit_btn);

        mAuth = FirebaseAuth.getInstance();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RegisterUserAsync(RegisterActivity.this).execute();
            }
        });

    }


    @SuppressLint("StaticFieldLeak")
    private class RegisterUserAsync extends AsyncTask<Void, Void, User> {

        private ProgressDialog dialog;

        private RegisterUserAsync(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            System.out.println("Help");
            super.onPreExecute();
            dialog.setMessage("Please Wait");
            dialog.show();
        }

        @Override
        protected User doInBackground(Void... voids) {
            registerUser();
            return generateUser();
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            String userID = null;
            try {
                userID = getUid(mFirebaseApp);
            } catch (FirebaseApiNotAvailableException e) {
                e.printStackTrace();
            }
            if (userID != null) {
                mUserRef.child("users").child(userID).setValue(user);
            }
        }
    }

    private void registerUser() {
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        try {
            Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                mFirebaseUser = mAuth.getCurrentUser();
                                User user = generateUser();
                                updateName(mFirebaseUser);
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                updateUI(null);
                            }
                        }
                    });
        } catch (IllegalArgumentException e) {
            Toast.makeText(RegisterActivity.this, "Email or Password field empty",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private User generateUser() {
        String name = mNameField.getText().toString();
        String email = mEmailField.getText().toString();
        return new User(name, email);
    }

    private void updateName(FirebaseUser firebaseUser) {
        String name = mNameField.getText().toString();
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        firebaseUser.updateProfile(profileChangeRequest);
    }

    private void updateUI(User user) {
        if (user != null) {
            Intent result = new Intent();
            setResult(Activity.RESULT_OK, result);
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
