package kushal.application.newshunter;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    DrawerLayout drawerLayout;

    Toolbar toolbar;
    RecyclerView recyclerView;
    LottieAnimationView loading_anim;
    LinearLayout main;

    LinearLayout navBar;
    TextView nav_tv;
    Button home, wsj, business, tech, notes, show_notes;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    LottieAnimationView network;

    public long time = 0;
    private boolean searchUsed = false;
    private boolean atHome = true;
    private RequestQueue que = null;
    public static final String api = "cb9951ac79724fe7a06b2c30afb1d831";
    public static final String siteURL = "https://newsapi.org/v2/everything";
    public static final String homeURL = "https://newsapi.org/v2/top-headlines?country=us&apiKey=cb9951ac79724fe7a06b2c30afb1d831";
    public static final String businessURL = "https://newsapi.org/v2/top-headlines?country=us&category=business&apiKey=cb9951ac79724fe7a06b2c30afb1d831";
    public static final String wsjURL = "https://newsapi.org/v2/everything?domains=wsj.com&apiKey=cb9951ac79724fe7a06b2c30afb1d831";
    public static final String techURL = "https://newsapi.org/v2/top-headlines?sources=techcrunch&apiKey=cb9951ac79724fe7a06b2c30afb1d831";

    //ad stuff....
    private InterstitialAd interstitialAd;
    public static final String INTERSTITIAL_ID = "ca-app-pub-5073642246912223/8824671181";

    public static final String NOTIFICATION = "Notify";
    public static final String GENERAL = "general";

    boolean target = false;
    boolean IN_SEQUENCE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlidrInterface slidrInterface = Slidr.attach(this);
        slidrInterface.lock();

        loading_anim = findViewById(R.id.progressBar);
        network = findViewById(R.id.network);
        ////////////////////////////////////////////////////////////////////////////////////////////

        main = findViewById(R.id.main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        navBar = findViewById(R.id.navBar);
        nav_tv = findViewById(R.id.nav_tv);

        setUpToolBar();
        loadData(homeURL);
        atHome = true;
        ////////////////////////////////////////////////////////////////////////////////////////////
        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(homeURL);
                drawerLayout.closeDrawer(GravityCompat.START);
                nav_tv.setText(getString(R.string.home_news));
                atHome = true;
            }
        });

        wsj = findViewById(R.id.wsj);
        wsj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(wsjURL);
                drawerLayout.closeDrawer(GravityCompat.START);
                nav_tv.setText(getString(R.string.wall_street_news));
                atHome = false;
            }
        });

        business = findViewById(R.id.business);
        business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(businessURL);
                drawerLayout.closeDrawer(GravityCompat.START);
                nav_tv.setText(getString(R.string.business_news));
                atHome = false;
            }
        });

        tech = findViewById(R.id.tech);
        tech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(techURL);
                drawerLayout.closeDrawer(GravityCompat.START);
                nav_tv.setText(getString(R.string.tech_news));
                atHome = false;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////
        notes = findViewById(R.id.notes);
        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotesAct.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                atHome = true;
            }
        });

        show_notes = findViewById(R.id.show_notes);
        show_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SavedNotesAct.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });


//      ad stuff here
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(INTERSTITIAL_ID);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        interstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                }, 2 * 60 * 1000);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitialAd.show();
            }
        });


        //Notification stuff starts here
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION,
                    NOTIFICATION,
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        FirebaseMessaging.getInstance().subscribeToTopic(GENERAL)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "success";
                        if (!task.isSuccessful()) {
                            msg = "failed";
                        }
                        Log.d(TAG, msg);
                    }
                });


        //first guide
        final SharedPreferences pref = getSharedPreferences("shared_pref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = pref.edit();

        if (pref.getBoolean("IS_FIRST", true)) {
            IN_SEQUENCE = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (target) {
                        new TapTargetSequence(MainActivity.this)
                                .targets(
                                        TapTarget.forToolbarMenuItem(toolbar, R.id.app_bar_search, "Search for News\nby Title, Tag or Publisher")
                                                .cancelable(false)
                                                .outerCircleColor(R.color.colorPrimary)
                                                .outerCircleAlpha(0.6f)
                                                .targetCircleColor(R.color.white)
                                                .targetRadius(30)
                                                .dimColor(R.color.colorBlack),
                                        TapTarget.forToolbarMenuItem(toolbar, R.id.bookmarks, "Tap to See\nSaved Bookmarks")
                                                .cancelable(false)
                                                .outerCircleColor(R.color.colorPrimary)
                                                .outerCircleAlpha(0.6f)
                                                .targetCircleColor(R.color.white)
                                                .targetRadius(30)
                                                .dimColor(R.color.colorBlack)
                                ).listener(new TapTargetSequence.Listener() {
                            @Override
                            public void onSequenceFinish() {
                                editor.putBoolean("IS_FIRST", false).apply();
                                IN_SEQUENCE = false;
                            }

                            @Override
                            public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                            }

                            @Override
                            public void onSequenceCanceled(TapTarget lastTarget) {
                            }
                        }).start();
                    }
                }
            }, 1500);
        }


        PeriodicWorkRequest PWrequest = new PeriodicWorkRequest.Builder(
                CheckPeriodic.class, 16, TimeUnit.MINUTES).
                build();

        WorkManager.getInstance(this).enqueue(PWrequest);


    }

    public void setUpToolBar() {
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    public String generateURL(String baseUrl, String searchCriteria) {
        return Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("q", searchCriteria)
                .appendQueryParameter("apiKey", api)
                .build().toString();
    }

    private void loadData(String url) {
        loading_anim.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                User users = gson.fromJson(response, User.class);
                recyclerView.setAdapter(new My_adapter(MainActivity.this, users));
                loading_anim.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(MainActivity.this, "Internet May Not be Available", Toast.LENGTH_LONG).show();
                atHome = true;
                loading_anim.setVisibility(View.GONE);
                network.setVisibility(View.VISIBLE);
                Snackbar.make(network, "Internet May Not be Available", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                finish();
                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.colorPrimaryDark))
                        .show();
            }
        });

        if (que == null)
            que = Volley.newRequestQueue(this);
        que.add(request);

//      even I don't how this coding is working, I just pasted it :)
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
//      but I am sure that after this 'keyboard is gone'
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        target = true;
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

        searchView.setQueryHint("Search Latest News...");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 1) {
                    String newUrl = generateURL(siteURL, query);
                    loadData(newUrl);
                    searchUsed = true;
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsAct.class));
                break;

            case R.id.bookmarks:
                startActivity(new Intent(this, Bookmark.class));
                break;

            case R.id.logout:
                signOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void signOut() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do You Really Want To Sign Out ?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //sign out user
                        // and redirect to login screen
                        firebaseAuth.signOut();
                        Toast.makeText(MainActivity.this, "Signing Out...", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(MainActivity.this, PhoneLoginAct.class));

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        do nothing
//                        this to cancel dialog
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onBackPressed() {

        if (IN_SEQUENCE)
            return;
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START))
            this.drawerLayout.closeDrawer(GravityCompat.START);
        else if (searchUsed) {
            loadData(homeURL);
            nav_tv.setText(getString(R.string.home_news));
            searchUsed = false;
        } else if (!atHome) {
            loadData(homeURL);
            nav_tv.setText(getString(R.string.home_news));
            atHome = true;
        } else if (time + 2500 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press Back Again To Exit", Toast.LENGTH_SHORT).show();
            time = System.currentTimeMillis();
        }

    }


}

