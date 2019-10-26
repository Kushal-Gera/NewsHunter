package kushal.application.newshunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginAct extends AppCompatActivity {
    //    private static final String TAG = "LoginAct";
    private static String VERIFICATION_ID;

    FirebaseAuth auth;

    TextInputLayout phone, otp;
    Button login, getCode;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
        }

        phone = findViewById(R.id.phone);
        otp = findViewById(R.id.otp);
        getCode = findViewById(R.id.getCode);
        login = findViewById(R.id.login_btn);
        login.setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.INVISIBLE);

        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = phone.getEditText().getText().toString().trim();
                if (!TextUtils.isEmpty(number) && number.length() > 9) {

                    getVerificationCode('+' + "91" + number);

                    progressBar.setVisibility(View.VISIBLE);
                    login.setVisibility(View.VISIBLE);
                    getCode.setVisibility(View.INVISIBLE);
                }
                else {
                    phone.setError("Phone Number Required");
                    phone.requestFocus();
                }

//      even I don't how this coding is working, I just pasted it :)
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
//      but I am sure that after this 'keyboard is gone'

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userCode = otp.getEditText().getText().toString().trim();
                if (!TextUtils.isEmpty(userCode))
                    verifyCode(userCode);
            }
        });


    }

    private void verifyCode(String userCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VERIFICATION_ID, userCode);
        signInWith(credential);

    }

    private void signInWith(PhoneAuthCredential credential) {
        Toast.makeText(this, "Just There...", Toast.LENGTH_SHORT).show();

        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(PhoneLoginAct.this, MainActivity.class));
                            finish();
                        } else
                            Toast.makeText(PhoneLoginAct.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getVerificationCode(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            String userCode = credential.getSmsCode();
            if (userCode != null) {
                otp.getEditText().setText(userCode);
                verifyCode(userCode);
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneLoginAct.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            VERIFICATION_ID = s;

        }


    };


}