package com.xtreme.jx.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.xtreme.jx.R;
import com.xtreme.jx.model.Comic;
import com.xtreme.jx.model.User;
import com.xtreme.jx.utils.AppPref;
import com.xtreme.jx.utils.Constant;
import com.xtreme.jx.utils.Util;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComicPreviewActivity extends BaseActivity implements PurchasesUpdatedListener {

    private static final String TAG = "ComicPreviewActivity";

    @BindView(R.id.pdfView)
    PDFView pdfView;

    @BindView(R.id.rl_purchase)
    LinearLayout readNowLayout;

    @BindView(R.id.tv_comic_title)
    TextView comicTitleTextView;

    User user;
    Comic comic;
    AlertDialog alertDialog;
    private BillingClient billingClient;
    private boolean isComicPurchased = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_preview);
        ButterKnife.bind(this);
        if (AppPref.IsLanguageJapanese(this)) {
            Util.setLanguage(this, "ja");

        }
        if (AppPref.IsLanguageEnglish(this)) {
            Util.setLanguage(this, "en");

        }
        initUI();
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            isComicPurchased = true;
            mDatabase.collection(Constant.USER_COLLECTION).document(user.getDocId()).collection(Constant.PURCHASED_COMICS).add(comic);
            new RetrivePDFStream().execute(comic.getPDFurl());
        }
    }

    void initUI() {
        user = AppPref.getUser(ComicPreviewActivity.this);
        comic = (Comic) getIntent().getSerializableExtra("comic_details");
        comicTitleTextView.setText(comic.getName());
       // Toast.makeText(this, comic.getProductId(), Toast.LENGTH_SHORT).show();
        if (Util.isNetworkConnected(this)) {
            if (AppPref.getPurchasedComics(this) != null) {
                for (Comic c : AppPref.getPurchasedComics(this)) {
                    if (c.getProductId().equals(comic.getProductId())) {
                        isComicPurchased = true;
                    }
                }
            }
            new RetrivePDFStream().execute(comic.getPDFurl());
        } else {
            Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show();
        }
        initPurchaseDialog();

        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Success to connect billing");
                } else {
                    Log.e(TAG, "Failed to connect billing");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    void initPurchaseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ComicPreviewActivity.this, Color.TRANSPARENT);
        builder.setCancelable(false);
        alertDialog = builder.create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_purchase, null);
        alertDialog.setView(view);
        view.findViewById(R.id.bt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickBack();
                alertDialog.dismiss();

            }
        });

        view.findViewById(R.id.bt_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                purchaseComic();
            }
        });
    }

    class RetrivePDFStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL uri = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            ProgressDialog progressDialog = new ProgressDialog(ComicPreviewActivity.this, R.style.AppCompatAlertDialogStyle);
            progressDialog.setTitle(getString(R.string.loading_comic));
            progressDialog.setCancelable(false);
            if (ComicPreviewActivity.this.isDestroyed()) {
                return;
            }
            progressDialog.show();



            if (isComicPurchased && AppPref.isLoggedIn(ComicPreviewActivity.this)) {
                pdfView.fromStream(inputStream).pageFitPolicy(FitPolicy.BOTH).onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        progressDialog.dismiss();
                    }
                }).swipeHorizontal(true).spacing(8).enableSwipe(true).pageFling(true).load();
            } else {
                pdfView.fromStream(inputStream).pageFitPolicy(FitPolicy.BOTH).onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        progressDialog.dismiss();
                    }
                }).onPageScroll(new OnPageScrollListener() {
                    @Override
                    public void onPageScrolled(int page, float positionOffset) {
                        if (page == 4) {
                            if (alertDialog.isShowing()) {
                                return;
                            }
                            alertDialog.show();
                        }
                    }
                }).pages(0, 1, 2, 3, 4).swipeHorizontal(true).enableSwipe(true).pageFling(true).spacing(8).fitEachPage(true).autoSpacing(false).load();
            }
        }
    }

    private void purchaseComic() {
        if (AppPref.isLoggedIn(this)) {
            pdfView.jumpTo(0);
            SkuDetailsParams params = SkuDetailsParams.newBuilder().setSkusList(Arrays.asList(comic.getProductId())).setType(BillingClient.SkuType.INAPP).build();
            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        if (list == null || list.size() == 0) {
                            Toast.makeText(ComicPreviewActivity.this, "No Products found", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(list.get(0)).build();
                        billingClient.launchBillingFlow(ComicPreviewActivity.this, billingFlowParams);
                    } else {
                        Toast.makeText(ComicPreviewActivity.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.login_first);
        builder.setNegativeButton(R.string.sign_up, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                startActivity(new Intent(ComicPreviewActivity.this, SignUpActivity.class));

            }
        });
        builder.setPositiveButton(R.string.sign_in, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                startActivity(new Intent(ComicPreviewActivity.this, LogInActivity.class));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick(R.id.iv_back)
    void onClickBack() {
        finish();
    }

    @OnClick(R.id.rl_purchase)
    void onClickPurchase() {
        purchaseComic();
    }
}
