package kushal.application.newshunter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;

public class SavedNotesAct extends AppCompatActivity {
    //    this is done to prevent un-necessary change and in file paths.
    public static final String USERS = "users";
    public static final String NOTEID = "noteId";
    public static final String NOTE = "note";
    public static final String TITLE = "title";
    private static final String TAG = "SavedNotesAct";
    ProgressDialog pd;
    RecyclerView note_recView;
    DatabaseReference ref;
    FirebaseAuth auth;
    FirebaseRecyclerOptions<NoteItems> options;
    FirebaseRecyclerAdapter<NoteItems, Note_viewholder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_notes);

        Slidr.attach(this);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading, PLease Wait");
        pd.show();

        final View content = findViewById(R.id.content);
        Snackbar.make(content, "Tap Any Note To Edit", Snackbar.LENGTH_LONG).show();

        //To sort it upside down
        note_recView = findViewById(R.id.note_recView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        note_recView.setLayoutManager(linearLayoutManager);
        //*****************************************************************************************

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null)
            ref = FirebaseDatabase.getInstance().getReference().child(USERS).child(auth.getCurrentUser().getUid());

        if (ref.getKey() == null) {
            pd.dismiss();
            Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
            return;
        }

        options = new FirebaseRecyclerOptions.Builder<NoteItems>()
                .setQuery(ref, NoteItems.class).build();

        adapter = new FirebaseRecyclerAdapter<NoteItems, Note_viewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final Note_viewholder holder, int i, @NonNull final NoteItems noteItems) {

                final String noteId = getRef(i).getKey();

                if (noteId != null) {
                    ref.child(noteId).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(TITLE) && dataSnapshot.hasChild(NOTE)) {
                                final String title = dataSnapshot.child(TITLE).getValue().toString();
                                final String note_text = dataSnapshot.child(NOTE).getValue().toString();

                                holder.title.setText(title);
                                holder.note_text.setText(note_text);
                                pd.dismiss();

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(SavedNotesAct.this, BIg_note_act.class);
                                        intent.putExtra(NOTEID, noteId);
                                        intent.putExtra(TITLE, title);
                                        intent.putExtra(NOTE, note_text);
                                        startActivity(intent);
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: dataBae Error received");
                            pd.dismiss();
                        }

                    });

                }

            }

            @NonNull
            @Override
            public Note_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.save_note_list, parent, false);
                return new Note_viewholder(view);

            }

        };

        adapter.startListening();
        note_recView.setAdapter(adapter);


    }


}
