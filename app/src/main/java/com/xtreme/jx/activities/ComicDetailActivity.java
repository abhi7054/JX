package com.xtreme.jx.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.xtreme.jx.R;
import com.xtreme.jx.adapters.HomeComicsAdapter;
import com.xtreme.jx.model.Comic;
import com.xtreme.jx.model.User;
import com.xtreme.jx.utils.AppPref;
import com.xtreme.jx.utils.Constant;
import com.xtreme.jx.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComicDetailActivity extends BaseActivity implements PurchasesUpdatedListener {

    private static final String TAG = ComicDetailActivity.class.getName();

    @BindView(R.id.iv_comic_image)
    ImageView comicImageView;

    @BindView(R.id.tv_comic_title)
    TextView comicTitle;

    @BindView(R.id.tv_author)
    TextView authorName;

    @BindView(R.id.tv_review_count)
    TextView reviewCount;

    @BindView(R.id.tv_date)
    TextView date;

    @BindView(R.id.tv_price)
    TextView price;

    @BindView(R.id.ll_star_container)
    LinearLayout starContainer;

    @BindView(R.id.rv_comics)
    RecyclerView comicRecyclerView;

    @BindView(R.id.tv_book)
    ImageView bookImageView;

    User user;
    Comic comic;
    HomeComicsAdapter comicsAdapter;

    private BillingClient billingClient;
    private boolean isComicPurchased = false;

    int rate = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            isComicPurchased = true;
            mDatabase.collection(Constant.USER_COLLECTION).document(user.getDocId()).collection(Constant.PURCHASED_COMICS).add(comic);
            bookImageView.setVisibility(View.GONE);
            price.setText(getResources().getString(R.string.post_review));
        }
    }

    void initView() {
        user = AppPref.getUser(this);
        comic = (Comic) getIntent().getSerializableExtra("comic_details");
        if (comic == null) {
            return;
        }
        Glide.with(this).load(comic.getImage()).into(comicImageView);
        comicTitle.setText(comic.getName());
        authorName.setText(comic.getAuthor());
        price.setText("$" + comic.getPrice());
        reviewCount.setText(comic.getReviews() + " " + getString(R.string.reviews));

        if (AppPref.isLoggedIn(this)) {
            for (Comic c : AppPref.getPurchasedComics(this)) {
                if (c.getProductId().equals(comic.getProductId())) {
                    isComicPurchased = true;
                    bookImageView.setVisibility(View.GONE);
                    price.setText(getResources().getString(R.string.post_review));
                }
            }
        }

        setRatings();
        getComicsList();

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

    void setComicsRecyclerView(ArrayList<Comic> comicArrayList) {
        comicsAdapter = new HomeComicsAdapter(this, comicArrayList);
        comicRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        comicRecyclerView.setAdapter(comicsAdapter);
    }

    void setRatings() {
        for (int i = 1; i <= 5; i++) {
            ImageView imgView = new ImageView(this);
            imgView.setPadding(Util.convertToPx(this, 2), 0, 0, 0);
            if (i <= comic.getRatings()) {
                imgView.setImageResource(R.drawable.ic_star_selected);
            } else {
                imgView.setImageResource(R.drawable.ic_star);
            }
            imgView.setLayoutParams(new LinearLayout.LayoutParams(Util.convertToPx(this, 21), Util.convertToPx(this, 21)));
            starContainer.addView(imgView);
        }
    }

    void getComicsList() {
        ArrayList<Comic> comicArrayList = new ArrayList<>();
        mDatabase.collection(Constant.COMIC_LIST_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Comic comic = document.toObject(Comic.class);
                        comic.setComicId(document.getId());
                        if (!ComicDetailActivity.this.comic.getName().equals(comic.getName())) {
                            comicArrayList.add(comic);
                        }
                    }

                } else {
                    Log.d("TAG - - - ", "Error getting documents: ", task.getException());
                }
            }
        });

        mDatabase.collection(Constant.JAPANESE_COMIC_LIST_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Comic comic = document.toObject(Comic.class);
                        comic.setComicId(document.getId());
                        if (!ComicDetailActivity.this.comic.getName().equals(comic.getName())) {
                            comicArrayList.add(comic);
                        }
                    }
                    setComicsRecyclerView(comicArrayList);
                } else {
                    Log.d("TAG - - - ", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    void showReviewDialog() {
        Dialog reviewDialog = new Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen);
        reviewDialog.setContentView(R.layout.dialog_submit_review);
        reviewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorWhiteWithAlpha)));

        ImageView firstImage = reviewDialog.findViewById(R.id.tv_star_1);
        ImageView secondImage = reviewDialog.findViewById(R.id.tv_star_2);
        ImageView thirdImage = reviewDialog.findViewById(R.id.tv_star_3);
        ImageView fourthImage = reviewDialog.findViewById(R.id.tv_star_4);
        ImageView fifthImage = reviewDialog.findViewById(R.id.tv_star_5);

        firstImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = 1;
                firstImage.setImageResource(R.drawable.ic_star_selected);
                secondImage.setImageResource(R.drawable.ic_star_border);
                thirdImage.setImageResource(R.drawable.ic_star_border);
                fourthImage.setImageResource(R.drawable.ic_star_border);
                fifthImage.setImageResource(R.drawable.ic_star_border);
            }
        });
        secondImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = 2;
                firstImage.setImageResource(R.drawable.ic_star_selected);
                secondImage.setImageResource(R.drawable.ic_star_selected);
                thirdImage.setImageResource(R.drawable.ic_star_border);
                fourthImage.setImageResource(R.drawable.ic_star_border);
                fifthImage.setImageResource(R.drawable.ic_star_border);
            }
        });
        thirdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = 3;
                firstImage.setImageResource(R.drawable.ic_star_selected);
                secondImage.setImageResource(R.drawable.ic_star_selected);
                thirdImage.setImageResource(R.drawable.ic_star_selected);
                fourthImage.setImageResource(R.drawable.ic_star_border);
                fifthImage.setImageResource(R.drawable.ic_star_border);
            }
        });
        fourthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = 4;
                firstImage.setImageResource(R.drawable.ic_star_selected);
                secondImage.setImageResource(R.drawable.ic_star_selected);
                thirdImage.setImageResource(R.drawable.ic_star_selected);
                fourthImage.setImageResource(R.drawable.ic_star_selected);
                fifthImage.setImageResource(R.drawable.ic_star_border);
            }
        });
        fifthImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = 5;
                firstImage.setImageResource(R.drawable.ic_star_selected);
                secondImage.setImageResource(R.drawable.ic_star_selected);
                thirdImage.setImageResource(R.drawable.ic_star_selected);
                fourthImage.setImageResource(R.drawable.ic_star_selected);
                fifthImage.setImageResource(R.drawable.ic_star_selected);
            }
        });

        reviewDialog.findViewById(R.id.rl_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewDialog.dismiss();
                setProgressBar(true);
                User user = AppPref.getUser(ComicDetailActivity.this);
                Comic ratedComic = comic;
                ratedComic.setRatings(rate);
                mDatabase.collection(Constant.USER_COLLECTION).document(user.getDocId()).collection(Constant.REVIEWED_COMICS).add(ratedComic);
                setProgressBar(false);
            }
        });
        reviewDialog.show();
    }

    private void purchaseComic() {

            SkuDetailsParams params = SkuDetailsParams.newBuilder().setSkusList(Arrays.asList(comic.getProductId())).setType(BillingClient.SkuType.INAPP).build();
            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        if (list == null || list.size() == 0) {
                            Toast.makeText(ComicDetailActivity.this, "No Products found", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(list.get(0)).build();
                        billingClient.launchBillingFlow(ComicDetailActivity.this, billingFlowParams);
                    } else {
                        Toast.makeText(ComicDetailActivity.this, "failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }

    @OnClick(R.id.iv_back)
    void onClickBack() {

       // Toast.makeText(this, "Back Pressed", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @OnClick(R.id.rl_preview)
    void onClickPurchase() {

            if (!isComicPurchased) {
                purchaseComic();
                return;
            }
            showReviewDialog();
        }

    @OnClick(R.id.rl_read)
    void onClickRead() {
        startActivity(new Intent(this, ComicPreviewActivity.class).putExtra("comic_details", comic));
    }
}
