package com.narwal.whatsappclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;

public class OtpActivity extends AppCompatActivity implements View.OnClickListener {

    static final String MOBILE_NUMBER = "mobileNumber";
    String mobileNumber;
    Button btnResend, btnVerify;
    TextView tvCounter;
    EditText etOtp;
    String mVerificationId;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    private String TAG = "OtpActivity";
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        initViews();
        Verify();
    }

    private void Verify() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallBack);        // OnVerificationStateChangedCallbacks
        showTimer(60000L);
    }

    private void showTimer(Long milliSecInFuture) {
        btnResend.setEnabled(false);
        countDownTimer = new CountDownTimer(milliSecInFuture, 1000) {

            @Override
            public void onTick(long l) {
                tvCounter.setVisibility(View.VISIBLE);
                tvCounter.setText(getString(R.string.secs_remaining, l / 1000));
            }

            @Override
            public void onFinish() {
                btnResend.setEnabled(true);
                tvCounter.setVisibility(GONE);
            }
        }.start();
    }

    private void initViews() {
        btnVerify = findViewById(R.id.verificationBtn);
        btnResend = findViewById(R.id.resendBtn);
        tvCounter = findViewById(R.id.counterTv);
        etOtp = findViewById(R.id.sentcodeEt);
        btnVerify.setOnClickListener(this);
        btnResend.setOnClickListener(this);
        mobileNumber = getIntent().getStringExtra(MOBILE_NUMBER);
        TextView tvVerify = findViewById(R.id.verifyTv);
        tvVerify.setText(getString(R.string.verify_num, mobileNumber));
        setSpannableString();
        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                String smsCode = credential.getSmsCode();
                if (smsCode != null && !smsCode.isEmpty()) {
                    etOtp.setText(smsCode);
                }
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
                notifyUser("Verification failed.\nTry Again!");
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                resendingToken = token;

                // ...
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(OtpActivity.this, SignUpActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException());
                            notifyUser("Verification failed.\nTry Again!");
                        }

                    }
                });
    }

    private void notifyUser(String message) {
        new MaterialAlertDialogBuilder(this)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startLoginActivity();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void setSpannableString() {
        TextView tvWaiting = findViewById(R.id.waitingTv);
        SpannableString spannableString = new SpannableString(getString(R.string.waiting_text, mobileNumber));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startLoginActivity();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(ds.linkColor);
            }
        };
        spannableString.setSpan(clickableSpan, spannableString.length() - 13, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvWaiting.setText(spannableString);
        tvWaiting.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void startLoginActivity() {
        Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
//        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.verificationBtn:
                String code = etOtp.getText().toString();
                PhoneAuthCredential credential = null;
                if (!code.isEmpty() && mVerificationId != null && !mVerificationId.isEmpty()) {
                    credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                }
                signInWithPhoneAuthCredential(credential);
                break;
            case R.id.resendBtn:
                if (resendingToken != null) {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            mobileNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            this,               // Activity (for callback binding)
                            mCallBack,        // OnVerificationStateChangedCallbacks
                            resendingToken);
                    Toast.makeText(this, "Resending OTP", Toast.LENGTH_SHORT).show();
                    showTimer(60000L);
                }

                break;
        }
    }
}