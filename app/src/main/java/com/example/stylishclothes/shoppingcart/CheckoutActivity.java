package com.example.stylishclothes.shoppingcart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.stylishclothes.R;
import com.example.stylishclothes.catalog.Product;
import com.example.stylishclothes.catalog.ProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    Context context = this;

    ArrayList<Product> products;
    ListView listView;
    ProductAdapter productAdapter;
    ArrayList<String> productIds;
    ArrayList<Integer> productQuantities;

    TextView orderTextView, kindOfDeliverTextView, branchTextView, totalPriceTextView;
    EditText dialogBranchNumberEditText;
    private View chooseDeliveryLayout, dialogLayout;
    private LayoutInflater dialogInflater;
    private Spinner deliverySpinner;

    private String selectedWayOfDelivery = "Нова пошта";
    private int totalPrice = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Оформлення замовлення");

        init();

        listView.setDivider(null);

        FirebaseDatabase.getInstance()
                .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productIds.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/")
                                    .child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    productIds.add(snapshot.child("id").getValue(String.class));
                                    productQuantities.add(snapshot.child("quantity").getValue(Integer.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        FirebaseDatabase.getInstance()
                                .getReference("Categories").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                products.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    for (int i = 0; i < productIds.size(); i++) {
                                        if (dataSnapshot.child("Products/" + productIds.get(i)).getValue(Product.class) != null) {
                                            Product product = dataSnapshot.child("Products/" + productIds.get(i)).getValue(Product.class);
                                            if (!product.getPrice().equals("")) {
                                                totalPrice += Integer.valueOf(product.getPrice()) * productQuantities.get(i);
                                            }
                                            products.add(product);
                                        }
                                    }
                                }
                                productAdapter.notifyDataSetChanged();
                                if (products.size() == 1) {
                                    orderTextView.setText("Ваше замовлення - 1 товар");
                                } else if (products.size() > 1 && products.size() < 5) {
                                    orderTextView.setText("Ваше замовлення - " + products.size() + " товари");
                                } else {
                                    orderTextView.setText("Ваше замовлення - " + products.size() + " товарів");
                                }

                                totalPriceTextView.setText("₴" + totalPrice);

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        chooseDeliveryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Спосіб доставки");
                builder.setMessage("Виберіть способ доставки та введіть номер відділення.");
                builder.setCancelable(true);

                if(dialogLayout.getParent()!=null) {
                    ((ViewGroup) dialogLayout.getParent()).removeView(dialogLayout); // <- fix
                }
                builder.setView(dialogLayout);

                builder.setPositiveButton(
                        "Створити",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                kindOfDeliverTextView.setText(selectedWayOfDelivery);
                                branchTextView.setText("Віділення №" + dialogBranchNumberEditText.getText().toString().trim());
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

        deliverySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] choose = getResources().getStringArray(R.array.deliveries);
                selectedWayOfDelivery = choose[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void init() {
        productIds = new ArrayList<>();
        productQuantities = new ArrayList<>();
        products = new ArrayList<Product>();
        productAdapter = new ProductAdapter(this, products, 3);
        listView = findViewById(R.id.product_list);
        listView.setAdapter(productAdapter);

        dialogInflater = getLayoutInflater();
        dialogLayout = dialogInflater.inflate(R.layout.dialog_choose_delivery, null);
        dialogBranchNumberEditText = dialogLayout.findViewById(R.id.branch_number_edit_text);
        deliverySpinner = dialogLayout.findViewById(R.id.dialog_way_of_delivery_spinner);

        totalPriceTextView = findViewById(R.id.total_price_text_view);
        kindOfDeliverTextView = findViewById(R.id.kind_of_delivery_text_view);
        branchTextView = findViewById(R.id.branch_text_view);
        chooseDeliveryLayout = findViewById(R.id.choose_delivery_layout);
        orderTextView = findViewById(R.id.order_text_view);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
