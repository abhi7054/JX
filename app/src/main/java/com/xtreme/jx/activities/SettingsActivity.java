package com.xtreme.jx.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.xtreme.jx.BuildConfig;
import com.xtreme.jx.R;
import com.xtreme.jx.utils.AppPref;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.iv_back)
    void onClickBack() {
        finish();
    }

    @OnClick(R.id.ll_sign_out)
    void onClickSignOut() {
        mAuth.signOut();
        AppPref.setLoginStatus(this, false);


        HomeActivity homeActivity = HomeActivity.getInstance();
        homeActivity.setLogInUser();
        finish();
    }

    @OnClick(R.id.ll_rate_app)
    void onClickRateApp() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
    }

    @OnClick(R.id.ll_review)
    void onClickReview() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
    }

    @OnClick(R.id.ll_terms)
    void onClickTerms() {
        startActivity(new Intent(this, TermsActivity.class));
    }

    @OnClick(R.id.ll_privacy)
    void onClickPrivacy() {
        TermsActivity.isPrivacy = true;
        startActivity(new Intent(this, TermsActivity.class));
    }
}
