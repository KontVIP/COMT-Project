package com.shop.stylishclothes.auth;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import com.shop.stylishclothes.InternetConnection;
import com.shop.stylishclothes.MainActivity;
import com.google.firebase.auth.FirebaseAuth;


public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Context context = this;
    private static int i = 0;

    private LayoutInflater dialogInflater;
    private View dialogLayout;

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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Немає Інтернет з'єднання.");
                builder.setMessage("Спрбуйте перезавантажити додаток.");
                builder.setNeutralButton(
                        "Перезавантажити",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                triggerRebirth(context);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } else {
            finish();
        }
    }

    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

}