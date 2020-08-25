package com.narwal.whatsappclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";
    CountryCodePicker countryCodePicker;
    private String mobileNumber;
    private String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO : Add hint request to the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        countryCodePicker = findViewById(R.id.ccp);
        final EditText etPhone = findViewById(R.id.phoneNumberEt);
        final MaterialButton btnNext = findViewById(R.id.nextBtn);
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int before, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                btnNext.setEnabled(!(charSequence.length() < 10));
                Log.d(TAG, "onTextChanged: " + !(count < 10) + charSequence.length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();
                mobileNumber = countryCode + etPhone.getText().toString();
                notifyUser();
            }
        });
    }

    private void notifyUser() {
        new MaterialAlertDialogBuilder(this)
                .setMessage(String.format("We will be verifying the phone number : %s\nWould you like to edit the number?", mobileNumber))
                .setPositiveButton("NEXT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startOtpActivity();
                    }
                })
                .setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void startOtpActivity() {
        Intent intent = new Intent(this, OtpActivity.class);
        intent.putExtra(OtpActivity.MOBILE_NUMBER, mobileNumber);
        startActivity(intent);
        finish();
    }
}