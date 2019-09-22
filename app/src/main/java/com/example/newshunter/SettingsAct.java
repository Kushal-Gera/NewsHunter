package com.example.newshunter;

import androidx.appcompat.app.AppCompatActivity;

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

    LinearLayout developer, suggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Slidr.attach(this);

        developer = findViewById(R.id.developer);
        suggest = findViewById(R.id.suggest);

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


    }
}
