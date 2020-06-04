package com.example.whatsapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private TextInputLayout phone_Number, verify_Code;
    private Button btn_Send_Verification_Code, btn_Verify;
    private String phoneNumber, verificationCode, mVerificationId;

    private ProgressDialog loading;

    // Firebase
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        initializeViews();


        btn_Send_Verification_Code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validationPhoneNum()) {
                    phone_Number.requestFocus();
                } else {

                    loading.setTitle("Phone Verification");
                    loading.setMessage("Please wait, while we are authentication using your phone...");
                    loading.setCanceledOnTouchOutside(false);
                    loading.setCancelable(false);
                    loading.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks

                }
            }
        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                loading.dismiss();

                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number, Please enter correct phone number with your country code...", Toast.LENGTH_LONG).show();

                btn_Send_Verification_Code.setVisibility(View.VISIBLE);
                phone_Number.setVisibility(View.VISIBLE);

                btn_Verify.setVisibility(View.INVISIBLE);
                verify_Code.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loading.dismiss();

                Toast.makeText(PhoneLoginActivity.this, "Code has been sent, Please check and verify...", Toast.LENGTH_SHORT).show();

                btn_Send_Verification_Code.setVisibility(View.INVISIBLE);
                phone_Number.setVisibility(View.INVISIBLE);

                btn_Verify.setVisibility(View.VISIBLE);
                verify_Code.setVisibility(View.VISIBLE);

            }

        };


        btn_Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_Send_Verification_Code.setVisibility(View.INVISIBLE);
                phone_Number.setVisibility(View.INVISIBLE);

                if (!validationVerificationCode()) {
                    verify_Code.requestFocus();
                } else {

                    loading.setTitle("Verification Code");
                    loading.setMessage("Please wait, while we are verify verification code...");
                    loading.setCanceledOnTouchOutside(false);
                    loading.setCancelable(false);
                    loading.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);

                }

            }
        });

    }

    private void sendVerificationCode() {


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loading.dismiss();
                            sendUserToMainActivity();

                        } else {
                            // Sign in failed, display a message and update the UI
                            String message = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error Occurred " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initializeViews() {

        mAuth = FirebaseAuth.getInstance();

        phone_Number = findViewById(R.id.phone_number);
        verify_Code = findViewById(R.id.verify_code);
        btn_Send_Verification_Code = findViewById(R.id.btn_send_code);
        btn_Verify = findViewById(R.id.btn_verify);

        loading = new ProgressDialog(this);

    }

    private boolean validationPhoneNum() {

        phoneNumber = phone_Number.getEditText().getText().toString();
        if (phoneNumber.isEmpty()) {
            phone_Number.setError("Please enter your phone number first...");
            return false;
        } else {
            phone_Number.setError(null);
            return true;
        }

    }

    private boolean validationVerificationCode() {

        verificationCode = verify_Code.getEditText().getText().toString();
        if (verificationCode.isEmpty()) {
            verify_Code.setError("Phone number is required");
            return false;
        } else {
            verify_Code.setError(null);
            return true;
        }

    }

    private void sendUserToMainActivity() {
        Intent main = new Intent(this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        finish();
    }

}
