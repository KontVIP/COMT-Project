package com.example.stylishclothes.catalog;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.stylishclothes.OptionsMenuProductActivity;
import com.example.stylishclothes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TrousersActivity extends OptionsMenuProductActivity {
    private Toolbar toolBar;
    private ArrayList<Product> products;
    private PopupMenu popup;
    Context context = this;
    DBHelper DB = new DBHelper(this);
    ListView listView;
    AdapterView<?> itemParent;
    ProductAdapter productAdapter;

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

        //List
        products = new ArrayList<Product>();
        loadData();
        listView = (ListView) findViewById(R.id.trousers_list);
        productAdapter = new ProductAdapter(this, products, new Intent(this, TrousersActivity.class));
        listView.setAdapter(productAdapter);

        //Refresh Data
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                products.clear();
                loadData();
                listView.invalidateViews();
                pullToRefresh.setRefreshing(false);
            }
        });


        //Floating add button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout frame = (FrameLayout) findViewById(R.id.trousers_fragment_container);
                frame.setVisibility(View.VISIBLE);

                getSupportFragmentManager().beginTransaction().replace(R.id.trousers_fragment_container, new AddCategoryFragment()).commit();
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
        Cursor result = DB.getCatalogs("SELECT * FROM Catalogs WHERE category = ?", "Штани");
        if (result.getCount() == 0) {
            Toast.makeText(this, "No Catalogs Exist", Toast.LENGTH_SHORT).show();
        } else {
            while (result.moveToNext()) {
                String title = result.getString(1);
                byte[] image = result.getBlob(2);
                products.add(new Product(title, image));
            }
        }
    }

}
