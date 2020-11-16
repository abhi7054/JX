package com.xtreme.jx.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.xtreme.jx.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatorActivity extends BaseActivity {

    @BindView(R.id.tv_pon_desc)
    TextView ponDesc;

    @BindView(R.id.tv_mushy_desc)
    TextView mushyDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator);
        ButterKnife.bind(this);

        SpannableString ss = new SpannableString(getResources().getString(R.string.pon_chan_desc));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://xtremeteez.com")));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(getResources().getColor(R.color.colorWhite));
            }
        };
        int startIndex = getResources().getString(R.string.pon_chan_desc).indexOf("https://xtremeteez.com");
        ss.setSpan(clickableSpan, startIndex, startIndex + "https://xtremeteez.com".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ponDesc.setText(ss);
        ponDesc.setMovementMethod(LinkMovementMethod.getInstance());

        SpannableString ss2 = new SpannableString(getResources().getString(R.string.mushy_desc));
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://xtremeteez.com")));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(getResources().getColor(R.color.colorWhite));
            }
        };
        int startIndex2 = getResources().getString(R.string.mushy_desc).indexOf("https://xtremeteez.com");
        ss2.setSpan(clickableSpan2, startIndex2, startIndex2 + "https://xtremeteez.com".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mushyDesc.setText(ss2);
        mushyDesc.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @OnClick(R.id.iv_back)
    void onClickBack() {
        finish();
    }
}