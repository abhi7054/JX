package com.xtreme.jx.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.xtreme.jx.model.Comic;
import com.xtreme.jx.model.User;
import com.xtreme.jx.utils.AppPref;
import com.xtreme.jx.utils.Constant;
import com.xtreme.jx.utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    public FirebaseFirestore mDatabase;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        if(AppPref.IsLanguageEnglish(this))
            mAuth.setLanguageCode("en");
        else
            mAuth.setLanguageCode("ja");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (AppPref.IsLanguageJapanese(this)) {
            Util.setLanguage(this, "ja");
            startActivity(new Intent(this, BaseActivity.class));
        }
        if (AppPref.IsLanguageEnglish(this)) {
            Util.setLanguage(this, "en");
            startActivity(new Intent(this, BaseActivity.class));
        }
    }

    public void setProgressBar(boolean show) {
        if (show) {
            if (progressDialog.isShowing()) {
                return;
            }
            progressDialog.show();
        } else {
            progressDialog.hide();
        }
    }
}
