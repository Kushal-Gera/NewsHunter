package com.example.newshunter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class Note_viewholder extends RecyclerView.ViewHolder {

    TextView note_text;
    TextView title;

    Note_viewholder(@NonNull View itemView) {

        super(itemView);

        note_text = itemView.findViewById(R.id.note_text);
        title = itemView.findViewById(R.id.title);

    }

}
