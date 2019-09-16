package com.example.newshunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Bookmark extends AppCompatActivity {
    private static final String TAG = "Bookmark";
    private static final String BOOK_MARK = "bookmarks";
    private static final String LINK = "link";
    private static final String TITLE = "title";
    private static final String BM_IMAGE = "bm_image";

    RecyclerView recyclerView;
    FirebaseAuth auth;
    DatabaseReference my_ref;
    FirebaseRecyclerOptions<BMarkItems> options;
    FirebaseRecyclerAdapter<BMarkItems, Bk_viewholder> adapter;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        recyclerView = findViewById(R.id.bk_recView);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading, PLease Wait");
        pd.show();

        auth = FirebaseAuth.getInstance();
        my_ref = FirebaseDatabase.getInstance().getReference().child(BOOK_MARK).child(auth.getCurrentUser().getUid());

        options = new FirebaseRecyclerOptions.Builder<BMarkItems>()
                .setQuery(my_ref, BMarkItems.class).build();

        adapter = new FirebaseRecyclerAdapter<BMarkItems, Bk_viewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final Bk_viewholder holder, int i, @NonNull final BMarkItems items) {

                        final String node_id = getRef(i).getKey();

                        if (node_id != null ) {

                            my_ref.child(node_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final String link_url = String.valueOf(dataSnapshot.child(LINK).getValue());
                                    final String image_url = String.valueOf(dataSnapshot.child(BM_IMAGE).getValue());
                                    final String title = String.valueOf(dataSnapshot.child(TITLE).getValue());

                                    holder.title.setText(title);
                                    pd.dismiss();
                                    Glide.with(holder.bm_image.getContext()).load(image_url)
                                            .centerCrop().placeholder(R.drawable.placeholder).into(holder.bm_image);

                                    holder.cross.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            my_ref.child(node_id).removeValue();

                                        }
                                    });

                                    holder.bm_image.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link_url)));
                                        }
                                    });

                                    holder.title.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link_url)));
                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(TAG, "onCancelled: Error" );
                                }
                            });


                        }


                    }

                    @NonNull
                    @Override
                    public Bk_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bk_mark_list, parent, false);
                        return new Bk_viewholder(view);
                    }


                };



        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.startListening();
        recyclerView.setAdapter(adapter);

        }

    }
