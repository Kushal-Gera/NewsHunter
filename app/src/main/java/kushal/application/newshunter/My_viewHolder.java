package kushal.application.newshunter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

class My_viewHolder extends RecyclerView.ViewHolder {

    ImageView img, share;
    TextView title, description, source, author, date;
    View dateView;
    LottieAnimationView animationView;

    My_viewHolder(@NonNull View itemView) {
        super(itemView);

        img = itemView.findViewById(R.id.image_news);
        title = itemView.findViewById(R.id.title);
        date = itemView.findViewById(R.id.date);
        description = itemView.findViewById(R.id.description);
        source = itemView.findViewById(R.id.source);
        author = itemView.findViewById(R.id.author);
        dateView = itemView.findViewById(R.id.dateView);

        share = itemView.findViewById(R.id.share);
        animationView = itemView.findViewById(R.id.animation_view);

    }
}
