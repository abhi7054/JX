package com.xtreme.jx.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.xtreme.jx.R;
import com.xtreme.jx.utils.AppPref;
import com.xtreme.jx.utils.Util;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LanguageSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.cv_japanese)
    void onClickJapanese() {
        Util.setLanguage(this, "ja");
        AppPref.setIsLanguageJapanese(this, true);
        AppPref.setIsLanguageEnglish(this, false);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @OnClick(R.id.cv_english)
    void onClickEnglish() {
        Util.setLanguage(this, "en");
        AppPref.setIsLanguageEnglish(this, true);
        AppPref.setIsLanguageJapanese(this, false);
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
