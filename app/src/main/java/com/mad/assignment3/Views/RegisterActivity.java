package com.mad.assignment3.Views;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import com.mad.assignment3.Presenters.RegisterActivityPresenter;
import com.mad.assignment3.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements RegisterActivityPresenter.View{

    @BindView(R.id.email) EditText mEmailField;
    @BindView(R.id.password) EditText mPasswordField;
    @BindView(R.id.full_name) EditText mNameField;
    @BindView(R.id.clear_all_btn) Button mClearAllButton;
    @BindView(R.id.submit_btn) Button mSubmitButton;
    private RegisterActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mPresenter = new RegisterActivityPresenter(this, this, auth);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mNameField.getText().toString();
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                ProgressDialog dialog = new ProgressDialog(RegisterActivity.this);
                if (!RegisterActivityPresenter.validateForm()) {
                    return;
                }
                new RegisterActivityPresenter.RegisterUserAsync(name, email, password).execute();
            }
        });

        mClearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clearAllFields();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (RegisterActivityPresenter.RegisterUserAsync.mDialog != null) {
            RegisterActivityPresenter.RegisterUserAsync.mDialog.dismiss();
        }
    }
}
