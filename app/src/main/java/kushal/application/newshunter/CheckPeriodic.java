package kushal.application.newshunter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

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

public class CheckPeriodic extends Worker {
    private static final String TAG = "CheckPeriodic";
    private static final String homeURL = "https://newsapi.org/v2/top-headlines?country=us&apiKey=cb9951ac79724fe7a06b2c30afb1d831";

    public CheckPeriodic(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
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
//                Toast.makeText(MainActivity.this, "Internet May Not be Available", Toast.LENGTH_LONG).show();
                return;
            }

        });

        Volley.newRequestQueue(getApplicationContext()).add(request);

        return Result.success();


    }

    private void checkStuff(User users) {
        if (users.getTotalResults() <= 1) {
            return;
        } else {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
            String early = pref.getString("EARLY", "nill");
            Article[] art = users.getArticles();

            String text = art[0].getTitle().substring(0, 10);

            if (text.equals(early)) {
                //do nothing
                Toast.makeText(getApplicationContext(), "Data not changed", Toast.LENGTH_SHORT).show();
            } else {
                //notify
                NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    manager.createNotificationChannel(new NotificationChannel(
                            "channel", "data_change", NotificationManager.IMPORTANCE_HIGH
                    ));
                }

                Bitmap bitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(), R.drawable.logo);

                Notification notification = new NotificationCompat.Builder(getApplicationContext(), "channel")
                        .setContentTitle("Check Out Latest News")
                        .setContentText(art[0].getTitle())
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.logo)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap))
                        .setLargeIcon(bitmap)
                        .build();

                manager.notify((int) System.currentTimeMillis(), notification);

                Log.i(TAG, "checkStuff: data changed: hahaha");
                pref.edit().putString("EARLY", text).apply();
                Toast.makeText(getApplicationContext(), "Data changed", Toast.LENGTH_SHORT).show();
            }

        }
    }


}
