package com.mad.assignment3.Views;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.mad.assignment3.R;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "LOG";
    private FirebaseAuth mAuth;

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mNameField;

    public FirebaseUser mFirebaseUser;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        mNameField = findViewById(R.id.full_name);
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        EditText phoneField = findViewById(R.id.phone_number);
        Spinner phoneSpinner = findViewById(R.id.phone_spinner);
        Button submitBtn = findViewById(R.id.submit_btn);

        mAuth = FirebaseAuth.getInstance();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

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
                                updateName(mFirebaseUser);
                                updateUI(mFirebaseUser);
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

    private void updateName(FirebaseUser firebaseUser) {
        String name = mNameField.getText().toString();
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        firebaseUser.updateProfile(profileChangeRequest);
    }

    private void updateUI(FirebaseUser user) {
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
