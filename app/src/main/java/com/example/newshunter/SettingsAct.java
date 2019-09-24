package com.example.newshunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.r0adkll.slidr.Slidr;

public class SettingsAct extends AppCompatActivity {
    private static final String TAG = "SettingsAct";
    public static final String GMAIL_LINK= "kushalgera1212@gmail.com";
    public static final String APP_LINK= "http://play.google.com/store/apps/details?id=" + "com.android.chrome";
    //                            KUri.parse("http://play.google.com/store/apps/details?id=" + getPackageName() )));   to be used

    LinearLayout developer, suggest, share, rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Slidr.attach(this);

        developer = findViewById(R.id.developer);
        suggest = findViewById(R.id.suggest);
        share = findViewById(R.id.Share_app);
        rate = findViewById(R.id.rate);

        developer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "App by Kushal Gera", Snackbar.LENGTH_LONG).show();
            }
        });

        suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("mailto:" + GMAIL_LINK));
                i.putExtra(Intent.EXTRA_SUBJECT, "Suggestion for the App 'News Hunter'");

                startActivity(i);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, APP_LINK);

                startActivity(Intent.createChooser(i, "Share via"));
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open my app in playStore
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("market://details?id=" + getPackageName() )));    to be used...
                            Uri.parse("market://details?id=" + "com.android.chrome" )));

                }
                catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName() )));   to be used
                            Uri.parse(APP_LINK)));
                }

            }
        });



    }
}
