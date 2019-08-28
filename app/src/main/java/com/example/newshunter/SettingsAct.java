package com.example.newshunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

public class SettingsAct extends AppCompatActivity {
    private static final String TAG = "SettingsAct";
    public static final String GMAIL_LINK= "kushalgera1212@gmail.com";

    TextView developer, suggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        developer = findViewById(R.id.developer);
        suggest = findViewById(R.id.suggest);

        developer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "App by Bounce Productions", Snackbar.LENGTH_LONG).show();
            }
        });

        suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + GMAIL_LINK ))
                        .putExtra(Intent.EXTRA_SUBJECT, "Suggestion for the App: News Hunter");

                startActivity(i);
            }
        });


    }
}
