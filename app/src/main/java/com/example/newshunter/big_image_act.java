package com.example.newshunter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class big_image_act extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image_act);

        getIncomingIntent();
    }

    @SuppressLint("SetTextI18n")
    private void getIncomingIntent(){
        if ( getIntent().hasExtra("image_url") && getIntent().hasExtra("big_title") ){

            ImageView big_img = findViewById(R.id.big_img);
            TextView big_name = findViewById(R.id.big_name);
            TextView big_cont = findViewById(R.id.big_content);
            final ProgressBar progressBar = findViewById(R.id.progressBar);
            final Button btn = findViewById(R.id.btn);
            final WebView webView = findViewById(R.id.webView);

//          getting url from the intent
            final String imageUrl = getIntent().getStringExtra("image_url");
            final String name = getIntent().getStringExtra("big_title");
            final String cont = getIntent().getStringExtra("big_cont");
            final String link = getIntent().getStringExtra("big_link");

//          setting the content received
            big_name.setText(name);
            big_cont.setText(cont);
            Glide.with(big_img.getContext()).load(imageUrl).placeholder(R.drawable.placeholder).centerCrop().into(big_img);

            webView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.GONE);
//            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient());    // set chrome client
            webView.setWebViewClient(new WebViewClient(){         // set web view client
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    progressBar.setVisibility(View.GONE);
                }
            } );

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webView.loadUrl(link);                        // now load url
                    progressBar.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.GONE);
                }
            });

        }
    }



}
