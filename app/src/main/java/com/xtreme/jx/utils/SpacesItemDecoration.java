package com.xtreme.jx.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.xtreme.jx.activities.HomeActivity;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private Context context;

    public SpacesItemDecoration(Context context, int space) {
        this.space = space;
        this.context = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (context instanceof HomeActivity) {
            outRect.top = space;
        } else {
            outRect.left = space;
        }
    }
}
