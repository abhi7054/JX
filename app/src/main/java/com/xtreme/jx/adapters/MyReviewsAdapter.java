package com.xtreme.jx.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xtreme.jx.R;
import com.xtreme.jx.activities.ComicDetailActivity;
import com.xtreme.jx.activities.ProfileActivity;
import com.xtreme.jx.model.Comic;
import com.xtreme.jx.model.ComicReview;
import com.xtreme.jx.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.MyReviewsViewHolder> {

    private Context context;
    private ArrayList<Comic> comicArrayList = new ArrayList<>();

    public MyReviewsAdapter(Context context, ArrayList<Comic> comicArrayList) {
        this.context = context;
        this.comicArrayList = comicArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_comics_detail, parent, false);
        return new MyReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyReviewsViewHolder holder, int position) {
        holder.comicTitleTextView.setText(comicArrayList.get(position).getName());
        Glide.with(context).load(comicArrayList.get(position).getImage()).into(holder.comicImageView);
        holder.comicTitleTextView.setTextColor(context.getResources().getColor(R.color.colorBlack));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof ComicDetailActivity) {
                    context.startActivity(new Intent(context, ComicDetailActivity.class).putExtra("comic_details", comicArrayList.get(position)));
                    ((ComicDetailActivity) context).finish();
                } else if (context instanceof ProfileActivity) {
                    getComicDetail(comicArrayList.get(position).getComicId());
                }
            }
        });
    }

    private void getComicDetail(String comicId) {
        ((ProfileActivity) context).setProgressBar(true);
        DocumentReference docRef =  ((ProfileActivity) context).mDatabase.collection(Constant.COMIC_LIST_COLLECTION).document(comicId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ((ProfileActivity) context).setProgressBar(false);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Comic comic = document.toObject(Comic.class);
                        context.startActivity(new Intent(context, ComicDetailActivity.class).putExtra("comic_details", comic));
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return comicArrayList.size();
    }

    class MyReviewsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_comic_image)
        ImageView comicImageView;

        @BindView(R.id.tv_comic_title)
        TextView comicTitleTextView;

        public MyReviewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
