package com.example.stylishclothes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.stylishclothes.catalog.Product;
import com.example.stylishclothes.catalog.ProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShoppingCartFragment extends Fragment {

    ArrayList<Product> products;
    ListView listView;
    ProductAdapter productAdapter;
    ArrayList<String> productIds;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopping_cart, container, false);
        getActivity().setTitle("Кошик");


        return rootView;
    }

    private void init(View rootView) {
        productIds = new ArrayList<>();
        products = new ArrayList<Product>();
        productAdapter = new ProductAdapter(getActivity(), products, false);
        listView = rootView.findViewById(R.id.product_list);
        listView.setAdapter(productAdapter);
    }

    private void loadData() {
        FirebaseDatabase.getInstance()
                .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Favorites/")
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
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    for (int i = 0; i < productIds.size(); i++) {
                                        if (dataSnapshot.child("Products/" + productIds.get(i)).getValue(Product.class) != null) {
                                            products.add(dataSnapshot.child("Products/" + productIds.get(i)).getValue(Product.class));
                                        }
                                    }
                                }
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
