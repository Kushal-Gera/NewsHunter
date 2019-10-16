package kushal.application.newshunter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;

public class BIg_note_act extends AppCompatActivity {

    //firebase stuf
    ProgressDialog pd;
    FirebaseAuth auth;
    DatabaseReference ref;

    EditText big_title, big_notes;
    Button update, delete;

    //    this is done to prevent un-neccesary change and in file paths.
    public static final String USERS = "users";
    public static final String NOTEID = "noteId";
    public static final String NOTE = "note";
    public static final String TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_note_act);

        Slidr.attach(this);

        pd = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child(USERS).child(auth.getCurrentUser().getUid());

        big_title = findViewById(R.id.big_title);
        big_notes = findViewById(R.id.big_notes);
        update = findViewById(R.id.update);
        delete = findViewById(R.id.delete);

        setEditText();

        final String newRef = getIntent().getStringExtra(NOTEID);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_title = big_title.getText().toString();
                String new_note = big_notes.getText().toString();

                if (!TextUtils.isEmpty(new_title)) {
                    ref.child(newRef).child(TITLE).setValue(new_title);
                    ref.child(newRef).child(NOTE).setValue(new_note);

                    Toast.makeText(BIg_note_act.this, "Updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    pd.setMessage("Please Fill ALL Required Fields");
                    pd.show();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteIt(newRef);
            }
        });


//      ad stuff......
        AdView adView;

        MobileAds.initialize(this, getResources().getString(R.string.BANNER_ID));
        adView = findViewById(R.id.adView3);
        adView.loadAd(new AdRequest.Builder().build());

    }

    public void setEditText() {
        String title = getIntent().getStringExtra(TITLE);
        String note = getIntent().getStringExtra(NOTE);

        big_title.setText(title);
        big_notes.setText(note);

    }

    public void deleteIt(final String newRef) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do You Want To Delete It Permanently ?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ref.child(newRef).removeValue();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing to do to cancel it
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


}
