package com.example.stylishclothes.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.stylishclothes.R;
import com.example.stylishclothes.catalog.Product;
import com.example.stylishclothes.catalog.ProductAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShoppingCartFragment extends Fragment implements View.OnClickListener {

    ArrayList<Product> products;
    ListView listView;
    ProductAdapter productAdapter;
    ArrayList<String> productIds;
    ArrayList<String> productCurrentSizes;
    ArrayList<Integer> productQuantities;
    int totalPrice = 0, tempPrice = 0;
    int productQuantity = 0;
    ExtendedFloatingActionButton checkoutFab;
    View shoppingImageLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        getActivity().setTitle("Кошик");

        init(rootView);
        listView.setDivider(null);

        checkoutFab.setOnClickListener(this);
        DatabaseReference shoppingCartReference = FirebaseDatabase.getInstance()
                .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/");
        shoppingCartReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productIds.clear();
                productCurrentSizes.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    shoppingCartReference.child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            productIds.add(snapshot.child("id").getValue(String.class));
                            productCurrentSizes.add(snapshot.child("size").getValue(String.class));
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
                        totalPrice = 0 + tempPrice;
                        productQuantity = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            for (int i = 0; i < productIds.size(); i++) {
                                if (dataSnapshot.child("Products/" + productIds.get(i)).getValue(Product.class) != null) {
                                    Product product = dataSnapshot.child("Products/" + productIds.get(i)).getValue(Product.class);
                                    product.currentSize = productCurrentSizes.get(i);
                                    Product newProduct = new Product(product);
                                    if (!product.getPrice().equals("")) {
                                        totalPrice += Integer.valueOf(product.getPrice()) * productQuantities.get(i);
                                    }
                                    productQuantity++;
                                    products.add(newProduct);
                                }
                            }
                        }
                        if (productQuantity == 0) {
                            shoppingImageLayout.setVisibility(View.VISIBLE);
                            checkoutFab.setVisibility(View.GONE);
                        } else {
                            checkoutFab.setVisibility(View.VISIBLE);
                            shoppingImageLayout.setVisibility(View.GONE);
                        }
                        checkoutFab.setText("Оформити замовлення • ₴" + totalPrice);
                        productAdapter.notifyDataSetChanged();
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

        return rootView;
    }


    private void init(View rootView) {
        shoppingImageLayout = rootView.findViewById(R.id.shopping_image_layout);
        checkoutFab = rootView.findViewById(R.id.checkout_fab);

        productIds = new ArrayList<>();
        productCurrentSizes = new ArrayList<>();
        productQuantities = new ArrayList<>();
        products = new ArrayList<Product>();
        productAdapter = new ProductAdapter(getActivity(), products, 2, ShoppingCartFragment.this);
        listView = rootView.findViewById(R.id.product_list);
        listView.setAdapter(productAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkout_fab:
                startActivity(new Intent(getContext(), ContactDetailsActivity.class));
                break;
        }
    }

    public void updateCheckoutFab(int price) {
        tempPrice += price;
    }

}
