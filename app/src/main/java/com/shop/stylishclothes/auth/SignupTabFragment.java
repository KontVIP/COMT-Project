package com.shop.stylishclothes.auth;

import android.os.Bundle;
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

import com.shop.stylishclothes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignupTabFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private TextView bannerTextView;
    private Button registerButton;
    private ProgressBar progressBar;
    private EditText fullNameEditText, phoneEditText, emailEditText, passwordEditText;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_signup_tab, container, false);

        mAuth = FirebaseAuth.getInstance();

        registerButton = (Button) rootView.findViewById(R.id.register_button);

        fullNameEditText = (EditText) rootView.findViewById(R.id.full_name_edit_text);
        phoneEditText = (EditText) rootView.findViewById(R.id.phone_edit_text);
        emailEditText = (EditText) rootView.findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) rootView.findViewById(R.id.password_edit_text);

        progressBar = (ProgressBar) rootView.findViewById(R.id.spin_kit);

        registerButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(fullName.isEmpty()) {
            fullNameEditText.setError("?????????????????? ?????????? ????'??!");
            fullNameEditText.requestFocus();
            return;
        }

        if(phone.isEmpty()) {
            phoneEditText.setError("???????????????????? ?????????? ????????????????!");
            phoneEditText.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            emailEditText.setError("???????????????????? Email!");
            emailEditText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("?????????????? ?????????????????? email!");
            emailEditText.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordEditText.setError("???????????????????? ????????????!");
            passwordEditText.requestFocus();
            return;
        }

        if(password.length() < 6) {
            passwordEditText.setError("???????????????????? ?????????????? 6 ????????????????!");
            passwordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    User user = new User(fullName, phone, email);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()) {
                                Toast.makeText(getContext(), "???????????????????? ?????????????? ????????????????????????????!", Toast.LENGTH_SHORT).show();
                                AuthActivity.viewPager.setCurrentItem(0);
                            } else {
                                Toast.makeText(getContext(), "??????????????! ?????????????????? ???? ??????!", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } else {
                    Toast.makeText(getContext(), "??????????????! ?????????????????? ???? ??????!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}
