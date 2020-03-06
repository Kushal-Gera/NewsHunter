package kushal.application.newshunter;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.r0adkll.slidr.Slidr;

public class SettingsAct extends AppCompatActivity {
    private static final String TAG = "SettingsAct";

    public static final String GMAIL_LINK = "kushalgera1212@gmail.com";
    public static final String GITHUB_LINK = "https://github.com/Kushal-Gera";
    public static final String API_LINK = "https://newsapi.org/";
    public final String WEB_APP_LINK = "http://play.google.com/store/apps/details?id=" + "kushal.application.newshunter";

    LinearLayout developer, suggest, share, rate, small_btn;
    TextView api_link;
    Boolean small;
    ImageView checked, unchecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.d(TAG, "onCreate: started");

        Slidr.attach(this);

        final SharedPreferences pref = getSharedPreferences("shared_pref", MODE_PRIVATE);
        small = pref.getBoolean("small", false);

        developer = findViewById(R.id.developer);
        suggest = findViewById(R.id.suggest);
        share = findViewById(R.id.Share_app);
        rate = findViewById(R.id.rate);
        api_link = findViewById(R.id.api_link);
        small_btn = findViewById(R.id.small_btn);

        unchecked = findViewById(R.id.un_checked_);
        checked = findViewById(R.id.checked);

        if (!small) {
            checked.setVisibility(View.GONE);
            unchecked.setVisibility(View.VISIBLE);
        } else {
            unchecked.setVisibility(View.GONE);
            checked.setVisibility(View.VISIBLE);
        }

        small_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                small = !small;
                pref.edit().putBoolean("small", small).apply();

                if (!small) {
                    checked.setVisibility(View.GONE);
                    unchecked.setVisibility(View.VISIBLE);
                } else {
                    unchecked.setVisibility(View.GONE);
                    checked.setVisibility(View.VISIBLE);
                }

            }
        });

        developer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "App by Kushal Gera\nTo Know More -->", Snackbar.LENGTH_LONG)
                        .setAction("GIT HUB", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_LINK)));
                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.colorPrimary)).show();
            }
        });

        suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("mailto:" + GMAIL_LINK));
                i.putExtra(Intent.EXTRA_SUBJECT, "Suggestion for the App 'News Hunter'");
                i.putExtra(Intent.EXTRA_TEXT, "I have a suggestion that: ");

                startActivity(i);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareIT();
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateUs();
            }
        });

        api_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(API_LINK)));
            }
        });


//      ad stuff......
        AdView adView;

        MobileAds.initialize(this, getResources().getString(R.string.BANNER_ID));
        adView = findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());


    }

    private void shareIT() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "News Hunter\n\n" + WEB_APP_LINK);

        startActivity(Intent.createChooser(i, "Share Via"));
    }

    private void rateUs() {
//        open playStore if present otherwise go to chrome
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(WEB_APP_LINK)));
        }

    }

}
