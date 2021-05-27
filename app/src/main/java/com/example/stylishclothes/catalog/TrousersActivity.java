package com.example.stylishclothes.catalog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.stylishclothes.OptionsMenuProductActivity;
import com.example.stylishclothes.R;
import com.example.stylishclothes.auth.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TrousersActivity extends OptionsMenuProductActivity {
    private Toolbar toolBar;
    private ArrayList<Product> products;
    private PopupMenu popup;
    Context context = this;
    Activity activity = this;
    ListView listView;
    ProductAdapter productAdapter;
    private DatabaseReference mDatabase;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Штани");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trousers);
        toolBar = findViewById(R.id.toolbar);
        try {
            setSupportActionBar(toolBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();

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
                    loadData();
                    listView.invalidateViews();
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
                FrameLayout frame = (FrameLayout) findViewById(R.id.trousers_fragment_container);
                frame.setVisibility(View.VISIBLE);

                getSupportFragmentManager().beginTransaction().replace(R.id.trousers_fragment_container, new AddProductFragment()).commit();
            }
        });

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

    public void loadData() {
        mDatabase.child("Product").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    products.add(product);
                }
                listView = (ListView) findViewById(R.id.trousers_list);
                productAdapter = new ProductAdapter(activity, products, new Intent(context, TrousersActivity.class));
                listView.setAdapter(productAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
