package com.example.stylishclothes.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.stylishclothes.InternetConnection;
import com.example.stylishclothes.MainActivity;
import com.example.stylishclothes.auth.AuthActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import io.paperdb.Paper;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Context context = this;
    private static int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (i == 0) {
            i++;
            mAuth = FirebaseAuth.getInstance();

            if (InternetConnection.checkConnection(context)) {

                if(mAuth.getCurrentUser() == null) {
                    startActivity(new Intent(context, AuthActivity.class));
                } else {
                    startActivity(new Intent(context, MainActivity.class));
                }

            } else {
                Toast.makeText(context, "No Internet connection!", Toast.LENGTH_LONG).show();
            }
        } else {
            finish();
        }
    }

}