package com.example.whatsapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout email_Register, password_Register;
    private TextView already_have_Account;
    private Button btn_Register_Now;

    private String email, password;

    private ProgressDialog loading;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        initializeViews();

        already_have_Account.setOnClickListener(this);
        btn_Register_Now.setOnClickListener(this);

    }

    private void createNewAccount() {

        if (!validationEmail()) {
            email_Register.requestFocus();
        } else if (!validationPassword()) {
            password_Register.requestFocus();
        } else {

            loading.setTitle("Creating New Account");
            loading.setMessage("Please Wait, while we are creating new account for you...");
            loading.setCanceledOnTouchOutside(true);
            loading.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sendUserToMainActivity();
                        loading.dismiss();
                    }else {
                        String message = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error Occurred " + message, Toast.LENGTH_LONG).show();
                        loading.dismiss();
                    }
                }
            });

        }

    }

    private void initializeViews() {

        email_Register = findViewById(R.id.email_register);
        password_Register = findViewById(R.id.password_register);
        already_have_Account = findViewById(R.id.already_have_account);
        btn_Register_Now = findViewById(R.id.btn_register_now);

        loading = new ProgressDialog(this);

    }

    private void sendUserToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void sendUserToMainActivity() {
        Intent main = new Intent(this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        finish();
    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean validationEmail() {
        email = email_Register.getEditText().getText().toString();
        if (email.isEmpty()) {
            email_Register.setError("Enter Your Email");
            return false;
        } else if (!isEmailValid(email)) {
            email_Register.setError("Enter Correct Email example@example.com");
            return false;
        } else {
            email_Register.setError(null);
            return true;
        }
    }

    public boolean validationPassword() {
        password = password_Register.getEditText().getText().toString();
        if (password.isEmpty()) {
            password_Register.setError("Please Enter Your Password");
            return false;
        } else if (password.length() < 6) {
            password_Register.setError("The Password Should Be More Than 6 Digits");
            return false;
        } else {
            password_Register.setError(null);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.already_have_account :
                sendUserToLoginActivity();
                break;
            case R.id.btn_register_now :
                createNewAccount();
                break;
        }
    }
}
