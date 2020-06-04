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
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout email_Login, password_Login;
    private TextView forgot_Password;
    private Button btn_Login, btn_Phone, btn_Register;

    private String email, password;

    private ProgressDialog loading;

    // Firebase
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        initializeViews();

        btn_Register.setOnClickListener(this);
        forgot_Password.setOnClickListener(this);
        btn_Login.setOnClickListener(this);
        btn_Phone.setOnClickListener(this);

    }

    private void login() {

        if (!validationEmail()) {
            email_Login.requestFocus();
        } else if (!validationPassword()) {
            password_Login.requestFocus();
        } else {

            loading.setTitle("Sign In");
            loading.setMessage("Please Wait...");
            loading.setCanceledOnTouchOutside(true);
            loading.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        sendUserToMainActivity();
                        loading.dismiss();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error Occurred " + message, Toast.LENGTH_LONG).show();
                        loading.dismiss();
                    }

                }
            });

        }

    }

    private void initializeViews() {

        email_Login = findViewById(R.id.email_login);
        password_Login = findViewById(R.id.password_login);
        forgot_Password = findViewById(R.id.forgot_password);
        btn_Login = findViewById(R.id.btn_login);
        btn_Phone = findViewById(R.id.btn_phone);
        btn_Register = findViewById(R.id.btn_register);

        loading = new ProgressDialog(this);

    }

    private void sendUserToMainActivity() {
        Intent main = new Intent(this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        finish();
    }

    private void sendUserToRegisterActivity() {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void sendUserToLoginPhoneActivity() {
        startActivity(new Intent(this, PhoneLoginActivity.class));
    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean validationEmail() {
        email = email_Login.getEditText().getText().toString();
        if (email.isEmpty()) {
            email_Login.setError("Enter Your Email");
            return false;
        } else if (!isEmailValid(email)) {
            email_Login.setError("Enter Correct Email example@example.com");
            return false;
        } else {
            email_Login.setError(null);
            return true;
        }
    }

    public boolean validationPassword() {
        password = password_Login.getEditText().getText().toString();
        if (password.isEmpty()) {
            password_Login.setError("Please Enter Your Password");
            return false;
        } else if (password.length() < 6) {
            password_Login.setError("The Password Should Be More Than 6 Digits");
            return false;
        } else {
            password_Login.setError(null);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                sendUserToRegisterActivity();
                break;
            case R.id.forgot_password:
                Toast.makeText(this, "Forgot Password ?", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_phone:
                sendUserToLoginPhoneActivity();
                break;
        }
    }

}
