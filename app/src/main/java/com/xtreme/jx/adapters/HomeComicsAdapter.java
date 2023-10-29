package com.xtreme.jx.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xtreme.jx.R;
import com.xtreme.jx.activities.ComicDetailActivity;
import com.xtreme.jx.activities.ComicPreviewActivity;
import com.xtreme.jx.activities.HomeActivity;
import com.xtreme.jx.activities.ProfileActivity;
import com.xtreme.jx.model.Comic;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeComicsAdapter extends RecyclerView.Adapter<HomeComicsAdapter.HomeComicsViewHolder> {

    private Context context;
    private ArrayList<Comic> comicArrayList= new ArrayList<>();

    public HomeComicsAdapter(Context context, ArrayList<Comic> comicArrayList) {
        this.context = context;
        this.comicArrayList = comicArrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeComicsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (context instanceof ComicDetailActivity) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_comics_detail, parent, false);
        } else if (context instanceof HomeActivity) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_home_comics, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_home_comics, parent, false);
        }
        return new HomeComicsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeComicsViewHolder holder, int position) {
        holder.comicTitleTextView.setText(comicArrayList.get(position).getName());
        holder.issueTextView.setText(comicArrayList.get(position).getIssueName());
        holder.priceTV.setText("Price: $"+ String.valueOf(comicArrayList.get(position).getPrice()));

        Glide.with(context).load(comicArrayList.get(position).getImage()).into(holder.comicImageView);

        if (context instanceof ComicDetailActivity) {
            holder.comicTitleTextView.setTextColor(context.getResources().getColor(R.color.colorWhite));
        } else if (context instanceof HomeActivity) {
            holder.comicTitleTextView.setTextColor(context.getResources().getColor(R.color.colorWhite));
        } else {
            holder.comicTitleTextView.setTextColor(context.getResources().getColor(R.color.colorWhite));
            holder.comicTitleTextView.setVisibility(View.GONE);
            holder.issueTextView.setVisibility(View.GONE);
            holder.priceTV.setVisibility(View.GONE);
            holder.previewButton.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(context instanceof ProfileActivity){
                    context.startActivity(new Intent(context, ComicPreviewActivity.class).putExtra("comic_details", comicArrayList.get(position)));
                }else {

                    context.startActivity(new Intent(context, ComicDetailActivity.class).putExtra("comic_details", comicArrayList.get(position)));
                    if (context instanceof ComicDetailActivity) {
                        ((ComicDetailActivity) context).finish();
                    }
                }
            }
        });

        holder.previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ComicPreviewActivity.class).putExtra("comic_details", comicArrayList.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return comicArrayList.size();
    }

    class HomeComicsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_comic_image)
        ImageView comicImageView;

        @BindView(R.id.tv_comic_title)
        TextView comicTitleTextView;

        @BindView(R.id.tvIssue)
        TextView issueTextView;

        @BindView(R.id.tvPrice)
        TextView priceTV;

        Button previewButton = itemView.findViewById(R.id.previewButton);

        public HomeComicsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
