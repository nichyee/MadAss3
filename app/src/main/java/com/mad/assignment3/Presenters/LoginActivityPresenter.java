package com.mad.assignment3.Presenters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mad.assignment3.R;

import java.util.concurrent.Executor;

public class LoginActivityPresenter {

    private View mView;
    private Activity mActivity;

    public LoginActivityPresenter(View view, Activity activity) {
        this.mView = view;
        this.mActivity = activity;
    }

    /**
     * Called when a user attempts to sign in using their credentials
     * @param email the email that has been input by the user
     * @param password the password that has been input by the user
     * @param auth an object containing the current authenticated user
     */
    public void signInProcess(String email, String password, final FirebaseAuth auth) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            mView.updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(mActivity, "Incorrect Username / Password",
                                    Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });

    }

    public interface View {
        void updateUI(FirebaseUser user);
    }

    /**
     * This method checks to see whether or not all data has been correctly input
     * @return a boolean value indicating whether the form is valid or not
     */
    public boolean validateForm() {
        boolean valid = true;

        EditText passwordEditText = mActivity.findViewById(R.id.password_edit_text);
        EditText emailEditText = mActivity.findViewById(R.id.email_edit_text);

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
            passwordEditText.setError("Required");
            valid = false;
        } else if (password.length() < 6) {
            passwordEditText.setError("Password must be longer than six characters");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }
}
