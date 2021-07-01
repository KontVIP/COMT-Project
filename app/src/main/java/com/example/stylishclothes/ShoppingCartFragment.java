package com.example.stylishclothes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.stylishclothes.catalog.Product;
import com.example.stylishclothes.catalog.ProductAdapter;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShoppingCartFragment extends Fragment implements View.OnClickListener {

    ArrayList<Product> products;
    ListView listView;
    ProductAdapter productAdapter;
    ArrayList<String> productIds;
    int totalPrice = 0;
    int productQuantity = 0;
    ExtendedFloatingActionButton checkoutFab;
    View shoppingImageLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        getActivity().setTitle("Кошик");

        init(rootView);

        checkoutFab.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        init(getView());
        loadData();
    }

    private void init(View rootView) {
        shoppingImageLayout = rootView.findViewById(R.id.shopping_image_layout);
        checkoutFab = rootView.findViewById(R.id.checkout_fab);

        productIds = new ArrayList<>();
        products = new ArrayList<Product>();
        productAdapter = new ProductAdapter(getActivity(), products, 2);
        listView = rootView.findViewById(R.id.product_list);
        listView.setAdapter(productAdapter);
    }

    //TODO
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkout_fab:
                break;
        }


    }

    private void loadData() {
        FirebaseDatabase.getInstance()
                .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productIds.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            productIds.add(dataSnapshot.getKey());
                        }
                        FirebaseDatabase.getInstance()
                                .getReference("Categories").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                products.clear();
                                totalPrice = 0;
                                productQuantity = 0;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    for (int i = 0; i < productIds.size(); i++) {
                                        if (dataSnapshot.child("Products/" + productIds.get(i)).getValue(Product.class) != null) {
                                            Product product = dataSnapshot.child("Products/" + productIds.get(i)).getValue(Product.class);
                                            if (!product.getPrice().equals("")) {
                                                totalPrice += Integer.valueOf(product.getPrice());
                                            }
                                            productQuantity++;
                                            products.add(product);
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
    }


}
