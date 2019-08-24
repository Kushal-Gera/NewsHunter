package com.example.newshunter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class My_adapter extends RecyclerView.Adapter<My_viewHolder>{
    private static final String TAG = "My_adapter";

    private Context myContext;
    private User data;
    private Boolean flag = false;

    My_adapter(Context context, User data) {
        myContext = context;
        this.data = data;
        if (data.getTotalResults() <= 1) flag = true;
    }

    @NonNull
    @Override
    public My_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View view = inflater.inflate((R.layout.news_list), parent, false);
        return new My_viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final My_viewHolder holder, final int position) {

        if (flag){
            holder.title.setText("No Result Found...");
            holder.description.setText("Please Search Something Else");
            holder.author.setVisibility(View.GONE);
            holder.dateView.setVisibility(View.GONE);
        }else {
            Article[] articles = data.getArticles();
            final Article model = articles[position];

            holder.title.setText(model.getTitle());
            holder.description.setText(model.getDescription());
            holder.source.setText(model.getSource().getName());
            holder.author.setText(model.getAuthor());
            holder.date.setText(getDate(model.getPublishedAt()));
            Glide.with(myContext).load(model.getUrlToImage()).placeholder(R.drawable.placeholder).centerCrop().into(holder.img);



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(myContext, big_image_act.class);
                    intent.putExtra("image_url", model.getUrlToImage());
                    intent.putExtra("big_title", model.getTitle());
                    intent.putExtra("big_cont", model.getContent());
                    intent.putExtra("big_link", model.getUrl());

                    myContext.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return data.getArticles().length<1 ? 1 : data.getArticles().length;
    }

    private String getDate(String oldDate){
        String newDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("india"));
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(oldDate);
            newDate = dateFormat.format(date);
        }catch (Exception e){
            Log.d(TAG, "getDate: date error" + e.getMessage());
            newDate = oldDate;
        }
        return newDate;
    }

}

