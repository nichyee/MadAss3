package com.mad.assignment3.Presenters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.mad.assignment3.Views.RegisterActivity;

import java.util.UUID;
import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;
import static com.google.firebase.internal.FirebaseAppHelper.getUid;

public class RegisterActivityPresenter {

    private static View mView;
    private static FirebaseUser mFirebaseUser;
    private static FirebaseAuth mAuth;
    private static Activity mActivity;
    private static DatabaseReference mUserRef;

    public RegisterActivityPresenter(View view, Activity activity, FirebaseAuth auth) {
        mView = view;
        mActivity = activity;
        mAuth = auth;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = firebaseDatabase.getReference();
    }

    private static void updateName(FirebaseUser firebaseUser, String name) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        firebaseUser.updateProfile(profileChangeRequest);
    }

    public void clearAllFields() {
        EditText nameEditText = mActivity.findViewById(R.id.full_name);
        EditText emailEditText = mActivity.findViewById(R.id.email);
        EditText passwordEditText = mActivity.findViewById(R.id.password);

        nameEditText.setText("");
        emailEditText.setText("");
        passwordEditText.setText("");
    }

    public interface View {
    }

    @SuppressLint("StaticFieldLeak")
    public static class RegisterUserAsync extends AsyncTask<Void, Void, User> {

        private String mName;
        private String mEmail;
        private String mPassword;
        public static ProgressDialog mDialog;

        public RegisterUserAsync(String name, String email, String password, ProgressDialog dialog) {
            dialog = new ProgressDialog(mActivity);
            mDialog = dialog;
            this.mName = name;
            this.mEmail = email;
            this.mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected User doInBackground(Void... voids) {

            Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(mEmail, mPassword);
            do {

            } while (!authResultTask.isComplete());
            mFirebaseUser = mAuth.getCurrentUser();
            updateName(mFirebaseUser, mName);
            User user = new User(mName, mEmail);
            mUserRef.child("users").push().setValue(user);
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            updateUI(user);
        }
    }

    private static void updateUI(User user) {
        if (user != null) {
            mActivity.finish();
        }

    }

    public static boolean validateForm() {
        boolean valid = true;

        EditText nameEditText = mActivity.findViewById(R.id.full_name);
        EditText emailEditText = mActivity.findViewById(R.id.email);
        EditText passwordEditText = mActivity.findViewById(R.id.password);

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            valid = false;
        } else if (!email.contains("@") || !email.contains(".")) {
            emailEditText.setError("Please input a valid email address");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Required.");
            valid = false;
        } else if (password.length() < 6) {
            passwordEditText.setError("Password must be longer than six characters");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        String name = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Required.");
            valid = false;
        } else {
            nameEditText.setError(null);
        }

        return valid;
    }

}
