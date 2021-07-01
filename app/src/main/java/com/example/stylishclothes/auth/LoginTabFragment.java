package com.example.stylishclothes.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.stylishclothes.MainActivity;
import com.example.stylishclothes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.paperdb.Paper;

public class LoginTabFragment extends Fragment implements View.OnClickListener {

    EditText emailEditText, passwordEditText;
    TextView forgotPasswordTextView;
    Button loginButton;
    float v = 0;

    private Button signInButton;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    boolean doubleBackToExitPressedOnce = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_login_tab, container, false);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = rootView.findViewById(R.id.email_edit_text);
        passwordEditText = rootView.findViewById(R.id.password_edit_text);
        forgotPasswordTextView = rootView.findViewById(R.id.forgot_password_text_view);
        loginButton = rootView.findViewById(R.id.login_button);

        emailEditText.setTranslationY(300);
        passwordEditText.setTranslationY(300);
        forgotPasswordTextView.setTranslationY(300);
        loginButton.setTranslationY(300);

        emailEditText.setAlpha(v);
        passwordEditText.setAlpha(v);
        forgotPasswordTextView.setAlpha(v);
        loginButton.setAlpha(v);

        emailEditText.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(300).start();
        passwordEditText.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        forgotPasswordTextView.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(500).start();
        loginButton.animate().translationY(0).alpha(1).setDuration(800).setStartDelay(700).start();

        progressBar = (ProgressBar) rootView.findViewById(R.id.spin_kit);

        Paper.init(getContext());

        loginButton.setOnClickListener(this);
        forgotPasswordTextView.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                userLogin(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim());
                Paper.book().write("Email", emailEditText.getText().toString().trim());
                Paper.book().write("Password", passwordEditText.getText().toString().trim());
                break;
            case R.id.forgot_password_text_view:
                startActivity(new Intent(getContext(), ForgotPasswordActivity.class));
                break;
        }
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
                    startActivity(new Intent(getContext(), MainActivity.class));
                    Toast.makeText(getContext(), "Вхід", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Невірний логінь або пароль!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

}
