package kushal.application.newshunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotAct extends AppCompatActivity {
    private static final String TAG = "ForgotAct";

    EditText editText;
    Button send;
    ProgressBar progressBar;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        editText = findViewById(R.id.edit_text);
        send = findViewById(R.id.send);
        progressBar = findViewById(R.id.progress_circular);
        linearLayout = findViewById(R.id.linearLayout2);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    sendEmail();
                    return true;
                }
                return false;
            }
        });


    }

    private void sendEmail() {

        String email = editText.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            editText.setError("Email can't be empty !");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(ForgotAct.this, "Email Sent", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                });

    }
}
