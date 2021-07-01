package com.example.stylishclothes;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.stylishclothes.catalog.Category;
import com.example.stylishclothes.catalog.CategoryAdapter;
import com.example.stylishclothes.catalog.Product;
import com.example.stylishclothes.catalog.ProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    ArrayList<Product> products;
    ListView listView;
    ProductAdapter productAdapter;
    ArrayList<String> productIds;
    int productQuantity = 0;
    View heartImageLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        getActivity().setTitle("Список бажань");

        return rootView;
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
                                productQuantity = 0;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    for (int i = 0; i < productIds.size(); i++) {
                                        if (dataSnapshot.child("Products/" + productIds.get(i)).getValue(Product.class) != null) {
                                            products.add(dataSnapshot.child("Products/" + productIds.get(i)).getValue(Product.class));
                                        }
                                        productQuantity++;
                                    }
                                }
                                if (productQuantity == 0) {
                                    heartImageLayout.setVisibility(View.VISIBLE);
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

    @Override
    public void onResume() {
        super.onResume();
        init(getView());
        loadData();
    }

    private void init(View rootView) {
        heartImageLayout = rootView.findViewById(R.id.heat_image_layout);
        productIds = new ArrayList<>();
        products = new ArrayList<Product>();
        productAdapter = new ProductAdapter(getActivity(), products, 1);
        listView = rootView.findViewById(R.id.product_list);
        listView.setAdapter(productAdapter);
    }
}
