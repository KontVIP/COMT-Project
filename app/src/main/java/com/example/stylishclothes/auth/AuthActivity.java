package com.example.stylishclothes.auth;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.stylishclothes.MainActivity;
import com.example.stylishclothes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEditText, passwordEditText;
    private TextView registerTextView, forgotPasswordTextView;
    private Button signInButton;
    
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        registerTextView = findViewById(R.id.register_text_view);
        registerTextView.setOnClickListener(this);

        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);

        signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        forgotPasswordTextView = findViewById(R.id.forgot_password_text_view);
        forgotPasswordTextView.setOnClickListener(this);

        Paper.init(this);

        String userEmailKey = Paper.book().read("Email");
        String userPasswordKey = Paper.book().read("Password");

        if(userEmailKey != "" && userPasswordKey != "") {
            if(!TextUtils.isEmpty(userEmailKey) && !TextUtils.isEmpty(userPasswordKey)) {
                userLogin(userEmailKey, userPasswordKey);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_text_view:
                startActivity(new Intent(this, RegisterUserActivity.class));
                break;
            case R.id.sign_in_button:
                userLogin();
                Paper.book().write("Email", emailEditText.getText().toString().trim());
                Paper.book().write("Password", passwordEditText.getText().toString().trim());
                break;
            case R.id.forgot_password_text_view:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }
    }

    private void userLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(email.isEmpty()) {
            emailEditText.setError("Необхідний Email!");
            emailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Введіть коректний email!");
            emailEditText.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordEditText.setError("Необхідний пароль!");
            passwordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    Toast.makeText(AuthActivity.this, "Вхід", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AuthActivity.this, "Невірний логінь або пароль!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void userLogin(String email, String password) {

        if(email.isEmpty()) {
            emailEditText.setError("Необхідний Email!");
            emailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Введіть коректний email!");
            emailEditText.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordEditText.setError("Необхідний пароль!");
            passwordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    Toast.makeText(AuthActivity.this, "Вхід", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AuthActivity.this, "Невірний логінь або пароль!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            this.finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Натисніть НАЗАД ще раз для виходу", Toast.LENGTH_SHORT).show();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                doubleBackToExitPressedOnce = false;
            }
        };
        thread.start();

    }

}





