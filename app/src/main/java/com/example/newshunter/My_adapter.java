package com.example.newshunter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class My_adapter extends RecyclerView.Adapter<My_viewHolder>{
    private static final String TAG = "My_adapter";
    private static final String BOOK_MARK = "bookmarks";
    private static final String LINK = "link";
    private static final String TITLE = "title";
    private static final String BM_IMAGE = "bm_image";

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference myRef;

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
            holder.saveStar.setVisibility(View.GONE);
            holder.UnSaveStar.setVisibility(View.GONE);
        }else {
            Article[] articles = data.getArticles();
            final Article model = articles[position];

            holder.title.setText(model.getTitle());
            holder.description.setText(model.getDescription());
            holder.source.setText(model.getSource().getName());
            holder.author.setText(model.getAuthor());
            holder.date.setText(getDate(model.getPublishedAt()));
            Glide.with(myContext).load(model.getUrlToImage()).placeholder(R.drawable.placeholder).centerCrop().into(holder.img);

                //set On click listeners
            holder.UnSaveStar.setVisibility(View.GONE);
            holder.saveStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    save the book-mark and send it to fireBase storage
                    bookMark(model.getUrl(), model.getUrlToImage(), model.getTitle());
                    Snackbar.make(v, "Bookmarked", Snackbar.LENGTH_SHORT).show();

                    holder.UnSaveStar.setVisibility(View.VISIBLE);
                    holder.saveStar.setVisibility(View.GONE);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(myContext, Big_image_act.class);
                    intent.putExtra("image_url", model.getUrlToImage());
                    intent.putExtra("big_title", model.getTitle());
                    intent.putExtra("big_cont", model.getContent());
                    intent.putExtra("big_link", model.getUrl());

                    myContext.startActivity(intent);
                }
            });

            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareURL(model.getUrl());
                }
            });
        }

    }

    private void shareURL(String url) {

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, url);
        myContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    @Override
    public int getItemCount() {
        return data.getArticles().length<1 ? 1 : data.getArticles().length;
    }

    private String getDate(String oldDate){
        String newDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("india"));
        try {
            @SuppressLint("SimpleDateFormat")
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(oldDate);
            newDate = dateFormat.format(date);
        }catch (Exception e){
            Log.d(TAG, "getDate: date error" + e.getMessage());
            newDate = oldDate;
        }
        return newDate;
    }

    private void bookMark(String url, String imageUrl, String title) {

        myRef = FirebaseDatabase.getInstance().getReference()
                .child(BOOK_MARK).child(auth.getCurrentUser().getUid()).push();

        myRef.child(LINK).setValue(url);
        myRef.child(BM_IMAGE).setValue(imageUrl);
        myRef.child(TITLE).setValue(title);

    }

}

