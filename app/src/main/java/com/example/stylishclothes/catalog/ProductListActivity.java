package com.example.stylishclothes.catalog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.stylishclothes.OptionsMenuProductActivity;
import com.example.stylishclothes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductListActivity extends OptionsMenuProductActivity {

    private Toolbar toolBar;
    private ArrayList<Product> products;
    private ListView listView;
    private ProductAdapter productAdapter;
    private Activity activity = this;
    private Context context = this;
    private String title;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        toolBar = findViewById(R.id.toolbar);

        try {
            setSupportActionBar(toolBar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //get data from CategoryAdapter
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            title = arguments.getString("Title");
            setTitle(title);
        }


        init();

        //List
        products = new ArrayList<Product>();
        try {
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Refresh Data
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    products.clear();
                    refreshData();

                    pullToRefresh.setRefreshing(false);
                } catch (Exception e) {
                    pullToRefresh.setRefreshing(false);
                    e.printStackTrace();
                }
            }
        });

        //Floating add button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout frame = (FrameLayout) findViewById(R.id.fragment_container);
                frame.setVisibility(View.VISIBLE);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment fragment = new AddProductFragment();
                ft.add(R.id.fragment_container, fragment);
                Bundle bundle = new Bundle();
                bundle.putString("Title", title);
                fragment.setArguments(bundle);
                ft.show(fragment);
                ft.commit();
            }
        });

    }

    public void loadData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);

                    products.add(product);

                }
                listView = (ListView) findViewById(R.id.product_list);
                productAdapter = new ProductAdapter(activity, products, true);
                listView.setAdapter(productAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void refreshData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);

                    products.add(product);

                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Categories/" + title + "/Products");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.product_menu, menu);
        menu.findItem(R.id.shopping_cart_icon).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
}