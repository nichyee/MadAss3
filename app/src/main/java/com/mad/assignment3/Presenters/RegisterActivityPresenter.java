package com.mad.assignment3.Presenters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.assignment3.Models.User;
import com.mad.assignment3.R;

public class RegisterActivityPresenter {

    private static final String USERS_CONSTANT = "users";

    private static FirebaseAuth mAuth;
    @SuppressLint("StaticFieldLeak")
    private static Activity mActivity;
    private static DatabaseReference mUserRef;

    public RegisterActivityPresenter(View view, Activity activity, FirebaseAuth auth) {
        mActivity = activity;
        mAuth = auth;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mUserRef = firebaseDatabase.getReference();
    }

    /**
     * This method is called to add a display name to the current authenticated user
     * @param firebaseUser the currently authenticated user
     * @param name the name input by the user
     */
    private static void updateName(FirebaseUser firebaseUser, String name) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        firebaseUser.updateProfile(profileChangeRequest);
    }

    /**
     * This method clears all data that has been input by the user
     */
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

    /**
     * This Async Task attempts to register a user to the Firebase System
     */
    @SuppressLint("StaticFieldLeak")
    public static class RegisterUserAsync extends AsyncTask<Void, Void, User> {

        private String mName;
        private String mEmail;
        private String mPassword;
        public static ProgressDialog mDialog;

        public RegisterUserAsync(String name, String email, String password) {
            mDialog = new ProgressDialog(mActivity);
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
            //noinspection StatementWithEmptyBody
            do {

            } while (!authResultTask.isComplete());
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            assert firebaseUser != null;
            updateName(firebaseUser, mName);
            User user = new User(mName, mEmail);
            mUserRef.child(USERS_CONSTANT).push().setValue(user);
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

    /**
     * This method attempts to close the current view if there is a generated user
     * @param user a user that may or may not have been generated
     */
    private static void updateUI(User user) {
        if (user != null) {
            mActivity.finish();
        }

    }

    /**
     * This method is called to check if the inputs into the form are all valid
     * @return a boolean value, indicating whether or not the form is valid
     */
    public static boolean validateForm() {
        boolean valid = true;

        EditText nameEditText = mActivity.findViewById(R.id.full_name);
        EditText emailEditText = mActivity.findViewById(R.id.email);
        EditText passwordEditText = mActivity.findViewById(R.id.password);

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(mActivity.getString(R.string.required));
            valid = false;
        } else if (!email.contains("@") || !email.contains(".")) {
            emailEditText.setError(mActivity.getString(R.string.valid_email_address));
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(mActivity.getString(R.string.required));
            valid = false;
        } else if (password.length() < 6) {
            passwordEditText.setError(mActivity.getString(R.string.longer_than_six));
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        String name = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError(mActivity.getString(R.string.required));
            valid = false;
        } else {
            nameEditText.setError(null);
        }

        return valid;
    }

}
