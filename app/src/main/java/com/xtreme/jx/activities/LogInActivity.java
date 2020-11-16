package com.xtreme.jx.activities;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.xtreme.jx.R;
import com.xtreme.jx.model.User;
import com.xtreme.jx.utils.AppPref;
import com.xtreme.jx.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends BaseActivity {

    private static final String TAG = "LogInActivity";

    @BindView(R.id.et_email)
    EditText emailEditText;

    @BindView(R.id.et_password)
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (AppPref.IsLanguageJapanese(this)) {
            Util.setLanguage(this, "ja");
            startActivity(new Intent(this, LogInActivity.class));
        }
        if (AppPref.IsLanguageEnglish(this)) {
            Util.setLanguage(this, "en");
            startActivity(new Intent(this, LogInActivity.class));
        }
    }

    void logIn() {
        setProgressBar(true);
        FirebaseAuth.getInstance().setLanguageCode("fr");
        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        setProgressBar(false);
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                                User user = new User("", firebaseUser.getEmail(), "", "", new Date());
                                AppPref.setUserData(LogInActivity.this, user);
                                AppPref.setLoginStatus(LogInActivity.this, true);
                                finish();
                                startActivity(new Intent(LogInActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            } else {
                                Toast.makeText(LogInActivity.this, "Please check your email address and verify your email", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @OnClick(R.id.tv_forgot_password)
    void onClickForgotPassword() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    @OnClick(R.id.rl_sign_in)
    void onClickSignIn() {
        Util.hideKeyboard(this);
        if (emailEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_email), Toast.LENGTH_SHORT).show();
            return;
        } else if (!Util.isValidEmail(emailEditText.getText().toString())) {
            Toast.makeText(this, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
            return;
        } else if (passwordEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_password), Toast.LENGTH_SHORT).show();
            return;
        } else if (passwordEditText.getText().toString().length() < 6) {
            Toast.makeText(this, getString(R.string.enter_valid_password), Toast.LENGTH_SHORT).show();
            return;
        }
        logIn();
    }

    @OnClick(R.id.tv_sign_up)
    void onClickSignUp() {
        Util.hideKeyboard(this);
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }

    @OnClick(R.id.iv_back)
    void onClickBack() {
        finish();
    }
}
