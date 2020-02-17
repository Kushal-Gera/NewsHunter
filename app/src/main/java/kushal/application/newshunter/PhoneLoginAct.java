package kushal.application.newshunter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
    public static final String GITHUB_LINK = "https://github.com/Kushal-Gera";

    FirebaseAuth auth;
    ImageView logo_img;

    TextInputLayout phone, otp;
    Button login, getCode;
    LottieAnimationView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        phone = findViewById(R.id.phone);
        logo_img = findViewById(R.id.logo_img);
        otp = findViewById(R.id.otp);
        getCode = findViewById(R.id.getCode);
        login = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.INVISIBLE);

        getCode.setAlpha(0f);
        getCode.setTranslationY(50f);
        login.setAlpha(0f);
        login.setTranslationY(50f);
        getCode.animate().alpha(1f).translationY(0f).setDuration(800);

        logo_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_LINK)));
            }
        });

        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = phone.getEditText().getText().toString().trim();
                if (!TextUtils.isEmpty(number) && number.length() > 9) {

                    getVerificationCode('+' + "91" + number);

                    progressBar.setVisibility(View.VISIBLE);
                    login.animate().alpha(1f).translationY(0f).setDuration(800);
                    getCode.setVisibility(View.INVISIBLE);
                } else {
                    phone.getEditText().setError("Phone Number Required");
                    phone.requestFocus();
                }

//              even I don't how this coding is working, I just pasted it :p
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
//              but I am sure that after this 'keyboard is gone'
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
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VERIFICATION_ID, userCode);
            signInWith(credential);
        } catch (Exception e) {
            Toast.makeText(this, "Please Try Again\nOTP Might be Incorrect", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, PhoneLoginAct.class));
            finish();
            e.printStackTrace();
        }

    }

    private void signInWith(PhoneAuthCredential credential) {
        Toast.makeText(this, "Hold on, Just There...", Toast.LENGTH_SHORT).show();

        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(PhoneLoginAct.this, MainActivity.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PhoneLoginAct.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getVerificationCode(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,
                120,
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