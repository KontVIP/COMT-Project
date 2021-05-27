package com.example.stylishclothes.auth;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.stylishclothes.R;
import com.example.stylishclothes.auth.AuthActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private TextView bannerTextView;
    private Button registerButton;
    private ProgressBar progressBar;
    private EditText fullNameEditText, ageEditText, emailEditText, passwordEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);

        bannerTextView = (TextView) findViewById(R.id.banner_text_view);
        bannerTextView.setOnClickListener(this);

        fullNameEditText = (EditText) findViewById(R.id.full_name_edit_text);
        ageEditText = (EditText) findViewById(R.id.age_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner_text_view:
                startActivity(new Intent(this, AuthActivity.class));
                break;
            case R.id.register_button:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String fullName = fullNameEditText.getText().toString().trim();
        String age = ageEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(fullName.isEmpty()) {
            fullNameEditText.setError("Необхідне повне ім'я!");
            fullNameEditText.requestFocus();
            return;
        }

        if(age.isEmpty()) {
            ageEditText.setError("Необхідний вік!");
            ageEditText.requestFocus();
            return;
        }

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

        if(password.length() < 6) {
            passwordEditText.setError("Мінімальна довжина 6 символів!");
            passwordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    User user = new User(fullName, age, email);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()) {
                                Toast.makeText(RegisterUserActivity.this, "Користувач успішно зареєстрований!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterUserActivity.this, AuthActivity.class));
                            } else {
                                Toast.makeText(RegisterUserActivity.this, "Помилка! Спробуйте ще раз!", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } else {
                    Toast.makeText(RegisterUserActivity.this, "Помилка! Спробуйте ще раз!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}







