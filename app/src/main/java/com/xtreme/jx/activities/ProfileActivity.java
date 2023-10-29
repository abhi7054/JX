package com.xtreme.jx.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.xtreme.jx.R;
import com.xtreme.jx.adapters.HomeComicsAdapter;
import com.xtreme.jx.adapters.MyReviewsAdapter;
import com.xtreme.jx.model.Comic;
import com.xtreme.jx.model.ComicReview;
import com.xtreme.jx.model.User;
import com.xtreme.jx.utils.AppPref;
import com.xtreme.jx.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity {

    @BindView(R.id.rv_purchase_comics)
    RecyclerView purchasedComicsRecyclerView;

    @BindView(R.id.tv_user_name)
    TextView userName;

    @BindView(R.id.iv_user)
    ImageView userImageView;

    @BindView(R.id.rv_reviews)
    RecyclerView reviewsRecyclerView;

    @BindView(R.id.tv_review_count)
    TextView reviewCount;

    @BindView(R.id.tv_purchase_count)
    TextView purchaseCountTextView;

    @BindView(R.id.tv_my_reviews_count)
    TextView myReviewCount;

    @BindView(R.id.tv_my_purchase)
    TextView myPurchaseCount;

    HomeComicsAdapter purchasedcomicsAdapter;
    MyReviewsAdapter reviewsAdapter;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  user = AppPref.getUser(this);

//        userName.setText(user.getUsername());
     //   Glide.with(this).load(user.getImage()).placeholder(R.drawable.user_placeholder).into(userImageView);
        getMyPurchases();
    }

    void setPurchasedComicsRecyclerView(ArrayList<Comic> comicArrayList) {
        purchasedcomicsAdapter = new HomeComicsAdapter(this, comicArrayList);
        purchasedComicsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        purchasedComicsRecyclerView.setAdapter(purchasedcomicsAdapter);
    }

    void setReviewRecyclerView(ArrayList<Comic> comicArrayList) {
        reviewsAdapter = new MyReviewsAdapter(this, comicArrayList);
        reviewsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        reviewsRecyclerView.setAdapter(reviewsAdapter);
    }

    void getMyPurchases() {

        setPurchasedComicsRecyclerView(AppPref.getPurchasedComics(this));

    }

  /*  void getMyReviewsList() {
        mDatabase.collection(Constant.USER_COLLECTION).document(user.getDocId()).collection(Constant.REVIEWED_COMICS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Comic> comicList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Comic comicReview = document.toObject(Comic.class);
                        comicList.add(comicReview);
                    }
                    reviewCount.setText(String.valueOf(comicList.size()));
                    myReviewCount.setText(getString(R.string.reviews) + " (" + comicList.size() + ")");
                    setReviewRecyclerView(comicList);
                } else {
                    Log.d("TAG - - - ", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @OnClick(R.id.iv_back)
    void onClickBack() {
        finish();
    }

    @OnClick(R.id.rl_edit_profile)
    void onClickEditProfile() {
        startActivity(new Intent(this, EditProfileActivity.class));
    }*/
}
