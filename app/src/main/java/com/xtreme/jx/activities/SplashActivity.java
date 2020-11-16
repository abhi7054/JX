package com.xtreme.jx.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.xtreme.jx.R;
import com.xtreme.jx.utils.AppPref;
import com.xtreme.jx.utils.Util;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!AppPref.isLoggedIn(SplashActivity.this)) {
                    mAuth.signInAnonymously()
                            .addOnCompleteListener(SplashActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "signInAnonymously:success");
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "signInAnonymously:failure", task.getException());
                                    }
                                }
                            });
                }
                if (AppPref.IsLanguageJapanese(SplashActivity.this)) {
                    Util.setLanguage(SplashActivity.this, "ja");
                    startActivity(new Intent(SplashActivity.this, LanguageSelectionActivity.class));
                    finish();
                    return;
                }
                if (AppPref.IsLanguageEnglish(SplashActivity.this)) {
                    Util.setLanguage(SplashActivity.this, "en");
                    startActivity(new Intent(SplashActivity.this, LanguageSelectionActivity.class));
                    finish();
                    return;
                }
                startActivity(new Intent(SplashActivity.this, LanguageSelectionActivity.class));
                finish();
            }
        }, 2000);
    }
}
