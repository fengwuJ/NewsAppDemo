package com.example.tangjie.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by tangjie on 2018/1/5.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<Contentmoudel> mNewsList;
    private Context mContext;

    private OnItemClickListener onItemClickListener = null;
    private OnItemLongClickListener onItemLongClickListener = null;

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView newsImage;
        TextView newsName;
        public ViewHolder(View view){
            super(view);
            cardView = (CardView)view;
            newsImage = (ImageView)view.findViewById(R.id.news_image);
            newsName = (TextView)view.findViewById(R.id.news_name);
        }
    }

    public NewsAdapter(List<Contentmoudel> newsList){
        mNewsList = newsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Contentmoudel news = mNewsList.get(position);
        holder.newsName.setText(news.getTitle());
        AsyncTaskImageLoad imageLoad  = new AsyncTaskImageLoad(holder.newsImage);
        imageLoad.execute(news.getImageUrl());

        if(onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }

        if(onItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    onItemLongClickListener.onItemLongClick(holder.itemView,position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}
