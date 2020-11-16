package com.xtreme.jx.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xtreme.jx.R;
import com.xtreme.jx.activities.ComicDetailActivity;
import com.xtreme.jx.model.Comic;
import com.xtreme.jx.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllComicsAdapter extends RecyclerView.Adapter<AllComicsAdapter.AllComicsViewHolder> {

    private Context context;
    private ArrayList<Comic> comicsList = new ArrayList<>();

    public AllComicsAdapter(Context context, ArrayList<Comic> comicsList) {
        this.context = context;
        this.comicsList = comicsList;
    }

    @NonNull
    @Override
    public AllComicsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_all_comics, parent, false);
        return new AllComicsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllComicsViewHolder holder, int position) {
        Comic comic = comicsList.get(position);
        Glide.with(context).load(comic.getImage()).into(holder.comicImageView);
        holder.comicTitle.setText(comic.getName());
        holder.authorName.setText(comic.getAuthor().trim());
        holder.reviewCount.setText(comic.getReviews() + " " + context.getString(R.string.reviews));
        holder.price.setText("$" + comic.getPrice());

        SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy");
        sfd.format((comic.getTimestamp()));
        String date = sfd.format((comic.getTimestamp()));
        holder.date.setText(date);

        if (holder.starContainer.getChildCount() == 0) {
            for (int i = 1; i <= 5; i++) {
                ImageView imgView = new ImageView(context);
                imgView.setPadding(Util.convertToPx(context, 2), 0, 0, 0);
                if (i <= comic.getReviews()) {
                    imgView.setImageResource(R.drawable.ic_star_selected);
                } else {
                    imgView.setImageResource(R.drawable.ic_star);
                }
                imgView.setLayoutParams(new LinearLayout.LayoutParams(Util.convertToPx(context, 21), Util.convertToPx(context, 21)));
                holder.starContainer.addView(imgView);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ComicDetailActivity.class).putExtra("comic_details", comic));
            }
        });
    }

    @Override
    public int getItemCount() {
        return comicsList.size();
    }

    class AllComicsViewHolder extends RecyclerView.ViewHolder {

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

        public AllComicsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
