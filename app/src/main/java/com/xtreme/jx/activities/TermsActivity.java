package com.xtreme.jx.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.xtreme.jx.R;
import com.xtreme.jx.utils.AppPref;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TermsActivity extends AppCompatActivity {


    @BindView(R.id.termWebview)
    WebView termWebView;

    @BindView(R.id.titleTerms)
    TextView termsTextView;

    public static boolean isPrivacy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        ButterKnife.bind(this);
        getTextDataOfTerms();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPrivacy = false;
    }

    void getTextDataOfTerms() {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;

        if (AppPref.IsLanguageEnglish(this)) {
            if (isPrivacy) {
                termsTextView.setText(R.string.privacy_policy);
                termWebView.loadUrl("file:///android_asset/PrivacyPolicy.html");
            } else {
                termsTextView.setText(R.string.terms_of_use);
                termWebView.loadUrl("file:///android_asset/TermsOfUse.html");
            }
        } else {
            if (isPrivacy) {
                termsTextView.setText("プライバシーポリシー");
                termWebView.loadUrl("file:///android_asset/PrivacyPolicy_ja.html");
            } else {
                termsTextView.setText("ご利用規約");
                termWebView.loadUrl("file:///android_asset/TermsOfUse_ja.html");
            }
        }


    }

            @OnClick(R.id.iv_back)
            void onClickBack() {
                finish();

    }

}


