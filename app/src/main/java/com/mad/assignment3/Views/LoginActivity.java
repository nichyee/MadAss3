package com.mad.assignment3.Views;

import android.content.Intent;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mad.assignment3.Presenters.LoginActivityPresenter;
import com.mad.assignment3.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements LoginActivityPresenter.View{
    private LoginActivityPresenter mPresenter;
    private FirebaseAuth mAuth;

    @BindView(R.id.email_edit_text) EditText mEmailEditText;
    @BindView(R.id.password_edit_text) EditText mPasswordEditText;


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mPresenter = new LoginActivityPresenter(this, this);

        mAuth = FirebaseAuth.getInstance();

        Button loginBtn = findViewById(R.id.login_button);
        Button registerBtn = findViewById(R.id.register_button);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                if (!mPresenter.validateForm()) {
                    return;
                }
                mPresenter.signInProcess(email, password, mAuth);
            }
        });
    }

    /**
     * This method updates the UI to the list of a user's households
     * @param firebaseUser the currently authenticated user
     */
    public void updateUI(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, HouseholdActivity.class);
            startActivity(intent);
        }
    }
}
