package com.shop.stylishclothes.shoppingcart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.shop.stylishclothes.MainActivity;
import com.shop.stylishclothes.R;
import com.shop.stylishclothes.admin_panel.Offer;
import com.shop.stylishclothes.catalog.Product;
import com.shop.stylishclothes.catalog.ProductAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CheckoutActivity extends AppCompatActivity {

    Context context = this;

    ArrayList<Product> products;
    ListView listView;
    ProductAdapter productAdapter;
    ArrayList<String> productIds;
    ArrayList<Integer> productQuantities;
    ArrayList<String> productCurrentSizes;

    TextView orderTextView, kindOfDeliverTextView, branchTextView, totalPriceTextView;
    EditText dialogBranchNumberEditText;
    private View chooseDeliveryLayout, dialogLayout;
    private LayoutInflater dialogInflater;
    private Spinner deliverySpinner;

    private String selectedWayOfDelivery = "Нова пошта";
    private int totalPrice = 0;
    private int productQuantity = 0;

    private Offer offer;
    private int branchNum;


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
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productIds.clear();
                        productCurrentSizes.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/")
                                    .child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    productIds.add(snapshot.child("id").getValue(String.class));
                                    productQuantities.add(snapshot.child("quantity").getValue(Integer.class));
                                    productCurrentSizes.add(snapshot.child("size").getValue(String.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        FirebaseDatabase.getInstance()
                                .getReference("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                products.clear();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    for (int i = 0; i < productIds.size(); i++) {
                                        if (dataSnapshot.child("Products/" + productIds.get(i)).getValue(Product.class) != null) {
                                            Product product = dataSnapshot.child("Products/" + productIds.get(i)).getValue(Product.class);
                                            product.currentSize = productCurrentSizes.get(i);
                                            product.quantity = productQuantities.get(i);
                                            if (!product.getPrice().equals("")) {
                                                totalPrice += Integer.valueOf(product.getPrice()) * productQuantities.get(i);
                                                productQuantity += productQuantities.get(i);
                                            }
                                            products.add(product);
                                        }
                                    }
                                }
                                productAdapter.notifyDataSetChanged();
                                if (productQuantity == 1) {
                                    orderTextView.setText("Ваше замовлення - 1 товар");
                                } else if (productQuantity > 1 && productQuantity < 5) {
                                    orderTextView.setText("Ваше замовлення - " + productQuantity + " товари");
                                } else {
                                    orderTextView.setText("Ваше замовлення - " + productQuantity + " товарів");
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

                if (dialogLayout.getParent() != null) {
                    ((ViewGroup) dialogLayout.getParent()).removeView(dialogLayout); // <- fix
                }
                builder.setView(dialogLayout);

                builder.setPositiveButton(
                        "Створити",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                kindOfDeliverTextView.setText(selectedWayOfDelivery);
                                branchTextView.setText("Віділення №" + dialogBranchNumberEditText.getText().toString().trim());
                                branchNum = Integer.parseInt(dialogBranchNumberEditText.getText().toString().trim());
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


        //Confirm button
        Intent intent = getIntent();
        String fullName = intent.getStringExtra("fullName");
        String phone = intent.getStringExtra("phone");
        String email = intent.getStringExtra("email");
        String region = intent.getStringExtra("region");
        String city = intent.getStringExtra("city");

        Button confirmButton = findViewById(R.id.confirm_order_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!kindOfDeliverTextView.getText().toString().trim().isEmpty() && !branchTextView.getText().toString().trim().isEmpty()) {

                    FirebaseDatabase.getInstance().getReference("Offers/Waiting").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String offerName = System.currentTimeMillis() + "";
                            String deliveringBy = kindOfDeliverTextView.getText().toString().trim();
                            List<Offer.OfferProduct> offerProductList = new ArrayList<>();


                            //Firebase load quantity and size
                            DatabaseReference shoppingCartReference = FirebaseDatabase.getInstance()
                                    .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/");
                            shoppingCartReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot shoppingCartSnapshot) {
                                    for (DataSnapshot dataSnapshot : shoppingCartSnapshot.getChildren()) {
                                        offerProductList.add(new Offer.OfferProduct(
                                                Objects.requireNonNull(dataSnapshot.child("id").getValue(String.class)),
                                                Objects.requireNonNull(dataSnapshot.child("quantity").getValue(Integer.class)),
                                                Objects.requireNonNull(dataSnapshot.child("size").getValue(String.class))
                                        ));
                                    }

                                    offer = new Offer(
                                            offerName,
                                            fullName,
                                            email,
                                            phone,
                                            region,
                                            city,
                                            deliveringBy,
                                            branchNum,
                                            offerProductList);
                                    snapshot.child(offerName).getRef().setValue(offer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/").setValue(null);
                                            Toast.makeText(context, "Успіх!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(context, MainActivity.class));
                                        }
                                    });
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

                } else {
                    Toast.makeText(context, "Виберіть спосіб доставки та відділення!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void init() {
        productIds = new ArrayList<>();
        productQuantities = new ArrayList<>();
        productCurrentSizes = new ArrayList<>();
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
