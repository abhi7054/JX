package com.xtreme.jx.activities;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.xtreme.jx.BuildConfig;
import com.xtreme.jx.R;
import com.xtreme.jx.adapters.HomeComicsAdapter;
import com.xtreme.jx.model.Comic;
import com.xtreme.jx.model.User;
import com.xtreme.jx.utils.AppPref;
import com.xtreme.jx.utils.Constant;
import com.xtreme.jx.utils.SpacesItemDecoration;
import com.xtreme.jx.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    @BindView(R.id.rv_first)
    RecyclerView firstRecyclerView;

    @BindView(R.id.rv_second)
    RecyclerView secondRecyclerView;

    @BindView(R.id.dl_drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.ll_login_container)
    LinearLayout loginButtonContainer;

    @BindView(R.id.ll_setting_container)
    LinearLayout settingsButtonContainer;

    @BindView(R.id.ll_profile)
    LinearLayout profile;

    @BindView(R.id.ll_comics_header)
    LinearLayout comicsHeader;

    @BindView(R.id.iv_drawer_app_icon)
    ImageView drawerAppIcon;

    @BindView(R.id.privacy)
    LinearLayout privacy;

    @BindView(R.id.rate)
            LinearLayout rate;

    @BindView(R.id.sendFeedback)
            LinearLayout sendFeedback;

    @BindView(R.id.termsOfUse)
            LinearLayout termsOfUse;

    @BindView(R.id.sign_out)
            LinearLayout signOut;

    HomeComicsAdapter firstComicsListAdapter;
    HomeComicsAdapter secondComicsListAdapter;

    static HomeActivity instance;

    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        ButterKnife.bind(this);
        initUI();
    }

    public static HomeActivity getInstance() {
        return instance;
    }

    void initUI() {
        instance = this;
        setLogInUser();
        getComicsList();
        getJapaneseComicsList();
        getUserDetail();
    }

    public void setLogInUser() {
        if (AppPref.isLoggedIn(this)) {
            loginButtonContainer.setVisibility(View.GONE);
            settingsButtonContainer.setVisibility(View.VISIBLE);
            drawerAppIcon.setVisibility(View.GONE);
            profile.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.VISIBLE);
        } else {
            loginButtonContainer.setVisibility(View.VISIBLE);
            settingsButtonContainer.setVisibility(View.VISIBLE);
            drawerAppIcon.setVisibility(View.VISIBLE);
            profile.setVisibility(View.GONE);
            signOut.setVisibility(View.GONE);
        }
    }

    void getComicsList() {
        setProgressBar(true);
        ArrayList<Comic> comicArrayList = new ArrayList<>();
        mDatabase.collection(Constant.COMIC_LIST_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                setProgressBar(false);
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG - - - ", document.getId() + " => " + document.getData());
                        Comic comic = document.toObject(Comic.class);
                        comic.setComicId(document.getId());
                        comicArrayList.add(comic);
                    }

                    if (comicArrayList.size() > 0) {
                        comicsHeader.setVisibility(View.VISIBLE);

                        Collections.reverse(comicArrayList);
                        if (AppPref.IsLanguageEnglish(HomeActivity.this)) {
                            firstComicsListAdapter = new HomeComicsAdapter(HomeActivity.this, comicArrayList);
                            firstRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, RecyclerView.VERTICAL, false));
                            firstRecyclerView.setAdapter(firstComicsListAdapter);
                        } else {
                            secondComicsListAdapter = new HomeComicsAdapter(HomeActivity.this, comicArrayList);
                            secondRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, RecyclerView.VERTICAL, false));
                            secondRecyclerView.setAdapter(secondComicsListAdapter);
                        }
                    }
                } else {
                    Log.d("TAG - - - ", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    void getJapaneseComicsList() {
        setProgressBar(true);
        ArrayList<Comic> comicArrayList = new ArrayList<>();
        mDatabase.collection(Constant.JAPANESE_COMIC_LIST_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                setProgressBar(false);
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG - - - ", document.getId() + " => " + document.getData());
                        Comic comic = document.toObject(Comic.class);
                        comic.setComicId(document.getId());
                        comicArrayList.add(comic);
                    }

                    if (comicArrayList.size() > 0) {
                        comicsHeader.setVisibility(View.VISIBLE);

                        Collections.reverse(comicArrayList);

                        if (AppPref.IsLanguageEnglish(HomeActivity.this)) {
                            secondComicsListAdapter = new HomeComicsAdapter(HomeActivity.this, comicArrayList);
                            secondRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, RecyclerView.VERTICAL, false));
                            secondRecyclerView.setAdapter(secondComicsListAdapter);
                        } else {
                            firstComicsListAdapter = new HomeComicsAdapter(HomeActivity.this, comicArrayList);
                            firstRecyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, RecyclerView.VERTICAL, false));
                            firstRecyclerView.setAdapter(firstComicsListAdapter);
                        }
                    }

                } else {
                    Log.d("TAG - - - ", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    void getUserDetail() {
        if (AppPref.isLoggedIn(this)) {
            mDatabase.collection(Constant.USER_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    setProgressBar(false);
                    if (task.isSuccessful()) {
                        user = AppPref.getUser(HomeActivity.this);
                        if (user == null) {
                            return;
                        }
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("TAG - - - ", document.getId() + " => " + document.getData());
                            User firebaseUser = document.toObject(User.class);
                            firebaseUser.setDocId(document.getId());
                            if (firebaseUser.getEmail().equals(user.getEmail())) {
                                AppPref.setUserData(HomeActivity.this, firebaseUser);
                                user = firebaseUser;
                            }
                        }
                        getMyPurchases();
                        getMyReviews();
                    } else {
                        Log.d("TAG - - - ", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    void getMyPurchases() {
        if (AppPref.isLoggedIn(this)) {
            mDatabase.collection(Constant.USER_COLLECTION).document(user.getDocId()).collection(Constant.PURCHASED_COMICS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<Comic> purchasedComic = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comic c = document.toObject(Comic.class);
                            purchasedComic.add(c);
                        }
                        AppPref.setPurchasedComics(HomeActivity.this, purchasedComic);
                    } else {
                        Log.d("TAG - - - ", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    void getMyReviews() {
        if (AppPref.isLoggedIn(this)) {

            signOut.setVisibility(View.VISIBLE);
            mDatabase.collection(Constant.USER_COLLECTION).document(user.getDocId()).collection(Constant.REVIEWED_COMICS).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<Comic> purchasedComic = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Comic c = document.toObject(Comic.class);
                            purchasedComic.add(c);
                        }
                        AppPref.setMyReviewComics(HomeActivity.this, purchasedComic);
                    } else {
                        Log.d("TAG - - - ", "Error getting documents: ", task.getException());
                    }
                }
            });
        }
    }

    void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @OnClick(R.id.iv_drawer)
    void onClickOpenDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.iv_search)
    void onClickSearch() {
        startActivity(new Intent(this, AllComicsListActivity.class));
    }

   /* @OnClick(R.id.tv_view_all)
    void onClickViewAll() {
        startActivity(new Intent(this, AllComicsListActivity.class));
    }*/

    @OnClick(R.id.rl_sign_in)
    void onClickSignIn() {
        closeDrawer();
        startActivity(new Intent(this, LogInActivity.class));
    }

    @OnClick(R.id.rl_sign_up)
    void onClickSignUp() {
        closeDrawer();
        startActivity(new Intent(this, SignUpActivity.class));
    }

    @OnClick(R.id.ll_profile)
    void onClickMyProfile() {
        closeDrawer();
        startActivity(new Intent(this, ProfileActivity.class));
    }

    @OnClick(R.id.ll_settings)
    void onClickSettings() {
        closeDrawer();
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @OnClick(R.id.ll_creator_profile)
    void onClickCreatorProfile() {
        closeDrawer();
        startActivity(new Intent(this, CreatorActivity.class));
    }

    @OnClick(R.id.iv_bottom_banner)
    void onClickBottomBanner() {
        closeDrawer();
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://xtremeteez.com")));
    }

    @OnClick(R.id.privacy)
    void onClickPrivacy(){
        TermsActivity.isPrivacy = true;
        startActivity(new Intent(this, TermsActivity.class));
    }
    @OnClick(R.id.rate)
    void onClickRateApp() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
    }

    @OnClick(R.id.sendFeedback)
    void onClickReview() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://xtremeteez.com/pages/contact")));
    }

    @OnClick(R.id.termsOfUse)
    void onClickTerms() {
        startActivity(new Intent(this, TermsActivity.class));
    }

    @OnClick(R.id.sign_out)
    void onClickSignOut() {
        mAuth.signOut();
        AppPref.setLoginStatus(this, false);
        HomeActivity homeActivity = HomeActivity.getInstance();
        homeActivity.setLogInUser();
        signOut.setVisibility(View.GONE);
    }

}
