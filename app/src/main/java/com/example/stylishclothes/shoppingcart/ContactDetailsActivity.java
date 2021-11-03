package com.example.stylishclothes.shoppingcart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.stylishclothes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ContactDetailsActivity extends AppCompatActivity {

    private Context context = this;
    private EditText fullNameEditText, phoneNumberEditText, dialogCityEditText;
    private TextView fullNameTextView, phoneNumberTextView, cityTextView, regionTextView;
    private Button proceedButton;
    private View chooseCityLayout, dialogLayout;
    private LayoutInflater dialogInflater;
    private Spinner regionSpinner;
    private String selectedRegion = "АР Крим";
    Toast toast;

    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Контактні дані");

        init();

        userReference.child("phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phoneNum = snapshot.getValue(String.class);
                if (!phoneNum.equals("None")) {
                    phoneNumberEditText.setText(phoneNum);
                } else {
                    phoneNumberEditText.setText("+380");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userReference.child("fullName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullName = snapshot.getValue(String.class);
                fullNameEditText.setText(fullName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        chooseCityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Ваше місто");
                builder.setMessage("Введіть назву населенного пункту та виберіть область.");
                builder.setCancelable(true);

                if(dialogLayout.getParent()!=null) {
                    ((ViewGroup) dialogLayout.getParent()).removeView(dialogLayout); // <- fix
                }
                builder.setView(dialogLayout);

                builder.setPositiveButton(
                        "Створити",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                cityTextView.setText(dialogCityEditText.getText().toString().trim());
                                regionTextView.setText(selectedRegion);
                            }
                        });

                builder.setNegativeButton(
                        "Скасувати",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] choose = getResources().getStringArray(R.array.regions);
                selectedRegion = choose[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        fullNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    fullNameEditText.setBackgroundResource(R.drawable.border_checkout_green);
                    fullNameTextView.setTextColor(Color.parseColor("#4c9a2a"));
                    phoneNumberEditText.setBackgroundResource(R.drawable.border_checkout);
                    phoneNumberTextView.setTextColor(Color.parseColor("#C0C0C0"));
                }
            }
        });

        phoneNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    phoneNumberEditText.setBackgroundResource(R.drawable.border_checkout_green);
                    phoneNumberTextView.setTextColor(Color.parseColor("#4c9a2a"));
                    fullNameEditText.setBackgroundResource(R.drawable.border_checkout);
                    fullNameTextView.setTextColor(Color.parseColor("#C0C0C0"));
                }
            }
        });

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cityTextView.getText().toString().trim().isEmpty()) {
                    try {
                        toast.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    toast = Toast.makeText(context, "Виберіть місто!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if (fullNameEditText.getText().toString().trim().isEmpty()) {
                    fullNameEditText.requestFocus();
                    return;
                }
                if (phoneNumberEditText.getText().length() < 10) {
                    phoneNumberEditText.requestFocus();
                    return;
                }
                startActivity(new Intent(context, CheckoutActivity.class));
            }
        });

    }

    private void init() {
        userReference = FirebaseDatabase.getInstance().
                getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        dialogInflater = getLayoutInflater();
        dialogLayout = dialogInflater.inflate(R.layout.dialog_choose_city, null);
        dialogCityEditText = dialogLayout.findViewById(R.id.dialog_city_edit_text);
        regionSpinner = dialogLayout.findViewById(R.id.dialog_region_spinner);

        chooseCityLayout = findViewById(R.id.choose_city_layout);
        cityTextView = findViewById(R.id.city_text_view);
        regionTextView = findViewById(R.id.region_text_view);
        proceedButton = findViewById(R.id.proceed_button);
        fullNameEditText = findViewById(R.id.full_name_edit_text);
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text);
        fullNameTextView = findViewById(R.id.full_name_text_view);
        phoneNumberTextView = findViewById(R.id.phone_number_text_view);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
