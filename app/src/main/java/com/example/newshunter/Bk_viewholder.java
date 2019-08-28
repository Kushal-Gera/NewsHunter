package com.example.newshunter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Bk_viewholder extends RecyclerView.ViewHolder {

    public ImageView cross, bm_image;
    public TextView link;


    public Bk_viewholder(@NonNull View itemView) {
        super(itemView);

        link = itemView.findViewById(R.id.link);
        cross = itemView.findViewById(R.id.cross);
        bm_image = itemView.findViewById(R.id.bm_image);

    }
}
