package com.example.newshunter;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;

    Toolbar toolbar;
    RecyclerView recyclerView;
    LottieAnimationView loading_anim;

    LinearLayout navBar;
    TextView nav_tv;
    Button home, wsj, business, tech, notes, show_notes;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public long time = 0;
    private boolean searchUsed = false;
    private boolean atHome = true;
    public static final String api = "cb9951ac79724fe7a06b2c30afb1d831";
    public static final String siteURL = "https://newsapi.org/v2/everything";
    public static final String homeURL = "https://newsapi.org/v2/top-headlines?country=us&apiKey=cb9951ac79724fe7a06b2c30afb1d831";
    public static final String businessURL = "https://newsapi.org/v2/top-headlines?country=us&category=business&apiKey=cb9951ac79724fe7a06b2c30afb1d831";
    public static final String wsjURL = "https://newsapi.org/v2/everything?domains=wsj.com&apiKey=cb9951ac79724fe7a06b2c30afb1d831";
    public static final String techURL = "https://newsapi.org/v2/top-headlines?sources=techcrunch&apiKey=cb9951ac79724fe7a06b2c30afb1d831";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SlidrInterface slidrInterface = Slidr.attach(this);
        slidrInterface.lock();

        loading_anim = findViewById(R.id.progressBar);

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        Toast.makeText(this, "Hello " + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        navBar = findViewById(R.id.navBar);
        nav_tv = findViewById(R.id.nav_tv);

        setUpToolBar();
        loadData(homeURL);
        atHome = true;

        ///////////////////////////////////////////////////////////////////////////////
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
                Toast.makeText(MainActivity.this, "    DATA OR INTERNET\nMAY NOT BE AVAILABLE", Toast.LENGTH_LONG).show();
                atHome = true;
                loading_anim.setVisibility(View.GONE);
            }
        });

        RequestQueue que = Volley.newRequestQueue(this);
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

    @Override
    public void onBackPressed() {

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
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));

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


}

