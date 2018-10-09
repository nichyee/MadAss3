package com.mad.assignment3.Presenters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class LoginActivityPresenter {

    private View mView;
    private Activity mActivity;

    public LoginActivityPresenter(View view, Activity activity) {
        this.mView = view;
        this.mActivity = activity;
    }

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
                            //Toast.makeText(LoginActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                            mView.updateUI(null);
                        }

                        // ...
                    }
                });

    }

    public interface View {
        void updateUI(FirebaseUser user);
    }
}
