package com.example.newshunter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.r0adkll.slidr.Slidr;

import java.util.HashMap;
import java.util.Map;

public class NotesAct extends AppCompatActivity {

    EditText notes, title_EdTxt;
    Button save;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ProgressDialog pd;

//    this is done to prevent un-neccesary change and in file paths.
    public static final String USERS = "users";
    public static final String NOTE = "note";
    public static final String TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        Slidr.attach(this);

        //firebase Stuff
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String f_id = (user != null ? user.getUid() : null);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child(USERS).child(f_id);
        //done

        pd = new ProgressDialog(this);

        notes = findViewById(R.id.notes);
        title_EdTxt = findViewById(R.id.title);
        save = findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                check for null strings(below)
                String note_to_string = notes.getText().toString();
                String title_to_string = title_EdTxt.getText().toString();

                if (TextUtils.isEmpty(title_to_string) ){
                    pd.setMessage("Fill All Required Fields...");
                    pd.show();
                    return;
                }

//                if reached here then there is a string available to save
                pd.setMessage("Saving Data...");
                pd.show();

//                call function to save data
                saveData(note_to_string, title_to_string);
                finish();

            }

        });


    }

    public void saveData(String data, String title) {
//        create a new random id for the text
        DatabaseReference newRef = myRef.push();

//        create a new map for the note and title
        Map nodeMap = new HashMap();
        nodeMap.put(TITLE, title);
        nodeMap.put(NOTE, data);

        newRef.setValue(nodeMap);
    }


}
