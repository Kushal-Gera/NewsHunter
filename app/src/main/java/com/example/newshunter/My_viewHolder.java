package com.example.newshunter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class My_viewHolder extends RecyclerView.ViewHolder {

     ImageView img, saveStar, UnSaveStar;
     LinearLayout main_ll;
     TextView title, description, source, author, date;
     View dateView;

     My_viewHolder(@NonNull View itemView) {
         super(itemView);

         main_ll = itemView.findViewById(R.id.main_ll);
         img = itemView.findViewById(R.id.image_news);
         title = itemView.findViewById(R.id.title);
         date = itemView.findViewById(R.id.date);
         description = itemView.findViewById(R.id.description);
         source = itemView.findViewById(R.id.source);
         author = itemView.findViewById(R.id.author);
         dateView = itemView.findViewById(R.id.dateView);

         saveStar = itemView.findViewById(R.id.saveStar);
         UnSaveStar = itemView.findViewById(R.id.UnSaveStar);


     }
 }
