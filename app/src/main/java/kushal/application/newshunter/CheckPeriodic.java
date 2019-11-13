package kushal.application.newshunter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class CheckPeriodic extends Worker {
    private static final String TAG = "CheckPeriodic";
    private static final String homeURL = "https://newsapi.org/v2/top-headlines?country=us&apiKey=cb9951ac79724fe7a06b2c30afb1d831";

    private Context mContext;

    public CheckPeriodic(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        StringRequest request = new StringRequest(homeURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                User users = gson.fromJson(response, User.class);

                checkStuff(users);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: Error in volley");
                return;
            }

        });

        Volley.newRequestQueue(mContext).add(request);

        return Result.success();


    }

    private void checkStuff(User users) {
        if (users.getTotalResults() >= 1) {
            SharedPreferences pref = mContext.getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
            String early = pref.getString("EARLY", "nill");
            final Article[] art = users.getArticles();

            String text = art[0].getTitle().substring(0, 10);

            if (text.equals(early)) {
                //do nothing
                Log.i(TAG, "checkStuff: Data not changed");
            } else {
                //notify
                Intent intent = new Intent(mContext, MainActivity.class);
                PendingIntent pi = PendingIntent.getActivity(mContext,
                        101,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                final NotificationManager manager = (NotificationManager)
                        mContext.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    manager.createNotificationChannel(new NotificationChannel(
                            "channel", "New News", NotificationManager.IMPORTANCE_DEFAULT
                    ));
                }

                final Bitmap bitmapLogo = BitmapFactory.decodeResource(
                        mContext.getResources(), R.drawable.logo);

                final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "channel")
                        .setContentTitle("Check Out Latest News Here")
                        .setContentText(art[0].getTitle())
                        .setAutoCancel(true)
                        .setContentIntent(pi)
                        .setLargeIcon(bitmapLogo)
                        .setSmallIcon(R.drawable.logo);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bitmap = Picasso.get().load(art[0].getUrlToImage()).get();
                            builder.setLargeIcon(bitmap)
                                    .setStyle(new NotificationCompat.BigPictureStyle()
                                            .bigPicture(bitmap)
                                            .bigLargeIcon(bitmapLogo)
                                    );
                            manager.notify(20, builder.build());
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                Log.i(TAG, "checkStuff: data changed: hahaha");
                pref.edit().putString("EARLY", text).apply();
            }

        }
    }


}
