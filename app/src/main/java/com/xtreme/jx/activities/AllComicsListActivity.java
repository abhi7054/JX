package com.xtreme.jx.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtreme.jx.R;
import com.xtreme.jx.adapters.AllComicsAdapter;
import com.xtreme.jx.adapters.SearchedTextAdapter;
import com.xtreme.jx.database.ORMLiteHelper;
import com.xtreme.jx.model.Comic;
import com.xtreme.jx.model.SearchHistory;
import com.xtreme.jx.utils.Constant;
import com.xtreme.jx.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AllComicsListActivity extends BaseActivity {

    @BindView(R.id.rv_search_list)
    RecyclerView searchRecyclerView;

    @BindView(R.id.tv_cancel)
    TextView cancelText;

    @BindView(R.id.iv_clear)
    ImageView clearImageView;

    @BindView(R.id.et_search)
    EditText searchEditText;

    @BindView(R.id.tv_search_content_text)
    TextView searchResultText;

    SearchedTextAdapter searchedTextAdapter;
    AllComicsAdapter allComicsAdapter;

    ORMLiteHelper dbHelper;

    ArrayList<Comic> comicsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comics_list);
        ButterKnife.bind(this);
        initUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Util.hideKeyboard(this);
    }

    void initUI() {
        dbHelper = new ORMLiteHelper(this);
        getComics();
        setSearchRecyclerView();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    setSearchRecyclerView();
                } else {
                    if (clearImageView.getVisibility() == View.INVISIBLE)
                        clearImageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Date date = new Date();
                    DateFormat formatter = new SimpleDateFormat("hh:mm dd-MMM-yyyy");
                    String dateString = formatter.format(date);
                    SearchHistory searchData = new SearchHistory(v.getText().toString().trim(), dateString);
                    try {
                        dbHelper.createOrUpdate(searchData);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    filterSearchedComics(v.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
    }

    void setSearchRecyclerView() {
        ArrayList<SearchHistory> searchHistories = new ArrayList<>();
        try {
            searchHistories = (ArrayList<SearchHistory>) dbHelper.getAll(SearchHistory.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        searchedTextAdapter = new SearchedTextAdapter(this, searchHistories);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(searchedTextAdapter);
        searchResultText.setText("Recent Searches");
        clearImageView.setVisibility(View.INVISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            searchRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY != oldScrollY) {
                        Util.hideKeyboard(AllComicsListActivity.this);
                    }
                }
            });
        }
    }

    void getComics() {
        setProgressBar(true);
        mDatabase.collection(Constant.COMIC_LIST_COLLECTION).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                setProgressBar(false);
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Comic comic = document.toObject(Comic.class);
                        comic.setComicId(document.getId());

                        comicsList.add(comic);
                    }
                }
            }
        });
    }

    void filterSearchedComics(String searchedText) {
        ArrayList<Comic> resultsList = new ArrayList<>();
        for (Comic comic : comicsList) {
            if (comic.getName().contains(searchedText)) {
                resultsList.add(comic);
            }
        }
        setComicsRecyclerView(resultsList);
    }

    void setComicsRecyclerView(ArrayList<Comic> comics) {
        allComicsAdapter = new AllComicsAdapter(this, comics);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(allComicsAdapter);
        searchResultText.setText("Over " + comics.size() + " results for All Comics");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            searchRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY != oldScrollY) {
                        Util.hideKeyboard(AllComicsListActivity.this);
                    }
                }
            });
        }
    }

    @OnClick(R.id.iv_clear)
    void onClickClearSearch() {
        searchEditText.setText("");
        Util.hideKeyboard(this);
    }

    @OnClick(R.id.tv_view_all)
    void onClickViewAll() {
        searchEditText.setText("");
        Util.hideKeyboard(this);
        setComicsRecyclerView(comicsList);
    }

    @OnClick(R.id.tv_cancel)
    void onClickCancel() {
        searchEditText.setText("");
        Util.hideKeyboard(this);
        finish();
    }
}
