package com.mad.assignment3.Presenters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
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

    private static void registerUser(String email, String password, final String name) {
            Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                mFirebaseUser = mAuth.getCurrentUser();
                                assert mFirebaseUser != null;
                                updateName(mFirebaseUser, name);
                                //mView.updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                mView.updateUI(null);
                            }
                        }
                    });
    }

    private static User generateUser(String name, String email) {
        return new User(name, email);
    }

    private static void updateName(FirebaseUser firebaseUser, String name) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        firebaseUser.updateProfile(profileChangeRequest);
    }

    public interface View {
        void updateUI(User user);
    }


    @SuppressLint("StaticFieldLeak")
    public static class RegisterUserAsync extends AsyncTask<Void, Void, User> {

        private String mName;
        private String mEmail;
        private String mPassword;
        public static ProgressDialog mDialog;

        public RegisterUserAsync(Activity activity, String name, String email, String password, ProgressDialog dialog) {
            dialog = new ProgressDialog(mActivity);
            mDialog = dialog;
            this.mName = name;
            this.mEmail = email;
            this.mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            System.out.println("Help");
            super.onPreExecute();
            mDialog.dismiss();
            mDialog.setMessage("Please Wait");
            mDialog.show();
        }

        @Override
        protected User doInBackground(Void... voids) {

            registerUser(mEmail, mPassword, mName);
            Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                mFirebaseUser = mAuth.getCurrentUser();
                                updateName(mFirebaseUser, mName);
                                User user = new User(mName, mEmail);

                                //mView.updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                //mView.updateUI(null);
                            }
                        }
                    });
            return generateUser(mName, mEmail);
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            mUserRef.child("users").push().setValue(user);

            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            mView.updateUI(user);
        }
    }
}
