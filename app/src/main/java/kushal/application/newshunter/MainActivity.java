package kushal.application.newshunter;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    DrawerLayout drawerLayout;

    Toolbar toolbar;
    RecyclerView recyclerView;
    LottieAnimationView loading_anim;
    LinearLayout main;
    My_adapter adapter;

    LinearLayout navBar;
    TextView nav_tv, home, wsj, business, tech, notes, show_notes, offline;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    LottieAnimationView network;
    SharedPreferences pref;
    SwipeRefreshLayout swipe;

    private boolean atHome = true;
    private boolean atWsj = false;
    private boolean atTech = false;
    private boolean atbusiness = false;

    public long time = 0;
    private boolean searchUsed = false;
    private RequestQueue que = null;
    public static final String api = "cb9951ac79724fe7a06b2c30afb1d831";
    public static final String siteURL = "https://newsapi.org/v2/everything";
    public static final String homeURL = "https://newsapi.org/v2/top-headlines?country=us&apiKey=cb9951ac79724fe7a06b2c30afb1d831";
    //    public static final String homeURL = "https://newsapi.org/v2/everything?q=bitcoin&from=2020-12-25&sortBy=publishedAt&apiKey=ea6bec1ed711479b9d970525c589ead2";
    public static final String businessURL = "https://newsapi.org/v2/top-headlines?country=us&category=business&apiKey=cb9951ac79724fe7a06b2c30afb1d831";
    public static final String wsjURL = "https://newsapi.org/v2/everything?domains=wsj.com&apiKey=cb9951ac79724fe7a06b2c30afb1d831";
    public static final String techURL = "https://newsapi.org/v2/top-headlines?sources=techcrunch&apiKey=cb9951ac79724fe7a06b2c30afb1d831";
    public static final String SHARED_PREF = "shared_pref";

    public static User CURRENT = null;
    public static String LINK;

    public static final String NOTIFICATION = "Notify";
    public static final String GENERAL = "general";

    boolean target = false;
    boolean IS_BLOCKED = false;
    boolean IN_SEQUENCE = false;
    boolean small = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlidrInterface slidrInterface = Slidr.attach(this);
        slidrInterface.lock();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, PhoneLoginAct.class));
            finish();
        }

        pref = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        loading_anim = findViewById(R.id.progressBar);
        network = findViewById(R.id.network);
        swipe = findViewById(R.id.swipe);
        offline = findViewById(R.id.offline);
        LINK = pref.getString("saved", "none");
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

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
                if (IS_BLOCKED)
                    return;
                pref.edit().putString("data", "none").apply();
                if (atHome) {
                    loadData(homeURL);
                } else if (atbusiness) {
                    loadData(businessURL);
                } else if (atWsj) {
                    loadData(wsjURL);
                } else if (atTech) {
                    loadData(techURL);
                }
            }
        });

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
                atHome = false;
                loadData(wsjURL);
                drawerLayout.closeDrawer(GravityCompat.START);
                nav_tv.setText(getString(R.string.wall_street_news));
            }
        });

        business = findViewById(R.id.business);
        business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atHome = false;
                loadData(businessURL);
                drawerLayout.closeDrawer(GravityCompat.START);
                nav_tv.setText(getString(R.string.business_news));
            }
        });

        tech = findViewById(R.id.tech);
        tech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atHome = false;
                loadData(techURL);
                drawerLayout.closeDrawer(GravityCompat.START);
                nav_tv.setText(getString(R.string.tech_news));
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
                                // first notification stuff
                                final NotificationManager manager = (NotificationManager)
                                        getSystemService(Context.NOTIFICATION_SERVICE);

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    manager.createNotificationChannel(new NotificationChannel(
                                            "channel", "New News", NotificationManager.IMPORTANCE_DEFAULT
                                    ));
                                }

                                final Bitmap bitmapLogo = BitmapFactory.decodeResource(
                                        getResources(), R.drawable.logo);

                                final NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(MainActivity.this, "channel")
                                        .setContentTitle("Welcome to News Hunter Family.")
                                        .setContentText("We'll Keep You Updated at Every Instance.")
                                        .setAutoCancel(true)
                                        .setColor(Color.WHITE)
                                        .setSmallIcon(R.drawable.news)
                                        .setLargeIcon(bitmapLogo)
                                        .setStyle(new NotificationCompat.BigPictureStyle()
                                                .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.banner))
                                                .bigLargeIcon(bitmapLogo)
                                        );
                                manager.notify(20, mbuilder.build());


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
                CheckPeriodic.class, 15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES
        ).build();
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

    private void loadData(final String url) {
        loading_anim.setVisibility(View.VISIBLE);

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    User users = gson.fromJson(response, User.class);
                    CURRENT = users;
                    LINK = response;

                    Log.e(TAG, "onResponse: " + users.toString());

                    small = pref.getBoolean("small", false);
                    adapter = new My_adapter(MainActivity.this, users, small);
                    recyclerView.setAdapter(adapter);
                    loading_anim.setVisibility(View.GONE);

                    if (atHome)
                        pref.edit().putString("data", response).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onResponse: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                atHome = true;
                Log.e(TAG, "onErrorResponse: " + error.getMessage());

                final String response = pref.getString("saved", "none");
                if (!"none".equals(response)) {
                    offline.setVisibility(View.VISIBLE);
                }

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

                offline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LINK = response;
                        IS_BLOCKED = true;
                        network.setVisibility(View.GONE);
                        offline.setVisibility(View.GONE);
                        loadOfflineData(response);
                    }
                });
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Access Token", "token");
                params.put("User-Agent", "Mozilla/5.0");
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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

    private void loadOfflineData(String response) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        final User users = gson.fromJson(response, User.class);
        CURRENT = users;

        small = pref.getBoolean("small", false);
        adapter = new My_adapter(MainActivity.this, users, small);
        recyclerView.setAdapter(adapter);
        loading_anim.setVisibility(View.GONE);
    }

    private void loadData_saved(User users) {
        small = pref.getBoolean("small", false);
        adapter = new My_adapter(MainActivity.this, users, small);
        recyclerView.setAdapter(adapter);
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

            case R.id.offline:
                if ("none".equals(LINK))
                    break;
                Toast.makeText(this, "Saved Page For Offline Reading", Toast.LENGTH_SHORT).show();
                getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit().putString("saved", LINK).apply();

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
                        //sign out user - and redirect to login screen
                        Toast.makeText(MainActivity.this, "Signing Out...", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        getSharedPreferences(SHARED_PREF, MODE_PRIVATE).edit().clear().apply();
                        finish();
                        startActivity(new Intent(MainActivity.this, PhoneLoginAct.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        do nothing - this to cancel dialog
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (CURRENT != null)
            loadData_saved(CURRENT);

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

