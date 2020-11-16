package com.xtreme.jx.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xtreme.jx.R;
import com.xtreme.jx.model.SearchHistory;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchedTextAdapter extends RecyclerView.Adapter<SearchedTextAdapter.SearchedTextViewHolder> {

    private ArrayList<SearchHistory> searchHistories = new ArrayList<>();
    private Context context;

    public SearchedTextAdapter(Context context, ArrayList<SearchHistory> searchHistories) {
        this.context = context;
        this.searchHistories = searchHistories;
    }

    @NonNull
    @Override
    public SearchedTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_searched_text, parent, false);
        return new SearchedTextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchedTextViewHolder holder, int position) {
        holder.searchedTextView.setText(searchHistories.get(position).getSearchedText());
        holder.timeTextView.setText(searchHistories.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return searchHistories.size();
    }

    class SearchedTextViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_searched_text)
        TextView searchedTextView;

        @BindView(R.id.tv_time)
        TextView timeTextView;

        public SearchedTextViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
