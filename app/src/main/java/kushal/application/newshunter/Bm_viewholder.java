package kushal.application.newshunter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Bm_viewholder extends RecyclerView.ViewHolder {

    public ImageView cross, bm_image, share;
    public TextView title;


    public Bm_viewholder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.bm_title);
        cross = itemView.findViewById(R.id.cross);
        bm_image = itemView.findViewById(R.id.bm_image);
        share = itemView.findViewById(R.id.share_link);

    }
}
