package kushal.application.newshunter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.util.LogTime;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    EditText email, password;
    TextView forgot;
    Button login, signup;
    FirebaseAuth firebaseAuth;
    ImageButton eye;
    ProgressDialog pd;
    Boolean hidden = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        forgot = findViewById(R.id.forgot);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        eye = findViewById(R.id.eye);

        pd = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        //if the user is already logged in..
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUser();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hidden) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    hidden = true;
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    hidden = false;
                }
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotAct.class));
            }
        });


    }

    private void newUser() {
        String email_txt = email.getText().toString().trim();
        String pass_txt = password.getText().toString().trim();

        if (TextUtils.isEmpty(email_txt)) {
            email.setError("Email can't be empty");
            return;
        }
        if (TextUtils.isEmpty(pass_txt)) {
            password.setError("Password can't be empty");
            return;
        }

        //if we have reached till here then the email is correct, so lets create account
        pd.setMessage("Loading please wait");
        pd.show();
        firebaseAuth.createUserWithEmailAndPassword(email_txt, pass_txt)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Registered New User", Toast.LENGTH_SHORT).show();
                            finish();
                            pd.dismiss();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void loginUser() {
        String email_txt = email.getText().toString().trim();
        String pass_txt = password.getText().toString().trim();

        if (TextUtils.isEmpty(email_txt)) {
            email.setError("Email can't be empty");
            return;
        }
        if (TextUtils.isEmpty(pass_txt)) {
            password.setError("Password can't be empty");
            return;
        }

        //if we have reached till here then the email is correct, so lets create account
        pd.setMessage("Logging in please wait");
        pd.show();
        firebaseAuth.signInWithEmailAndPassword(email_txt, pass_txt)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            finish();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "No Such User Exists", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
