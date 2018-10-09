package com.mad.assignment3.Views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.mad.assignment3.Models.User;
import com.mad.assignment3.Presenters.RegisterActivityPresenter;
import com.mad.assignment3.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements RegisterActivityPresenter.View{

    private static final String TAG = "LOG";

    @BindView(R.id.email) EditText mEmailField;
    @BindView(R.id.password) EditText mPasswordField;
    @BindView(R.id.full_name) EditText mNameField;
    private RegisterActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mPresenter = new RegisterActivityPresenter(this, this, auth);
        Button submitBtn = findViewById(R.id.submit_btn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameField.getText().toString();
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
                new RegisterActivityPresenter.RegisterUserAsync(name, email, password, dialog).execute();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        RegisterActivityPresenter.RegisterUserAsync.mDialog.dismiss();
    }

    public void updateUI(User user) {
        if (user != null) {
            finish();
        }
    }
}
