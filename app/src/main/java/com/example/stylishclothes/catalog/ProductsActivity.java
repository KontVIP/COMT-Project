package com.example.stylishclothes.catalog;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;

import com.example.stylishclothes.OptionsMenuProductActivity;
import com.example.stylishclothes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ProductsActivity extends OptionsMenuProductActivity {
    private Toolbar toolBar;
    private ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTitle("Some category");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        //List
//        products = new ArrayList<Product>();
//        //get data from database
//        DBHelper DB = new DBHelper(this);
//        Cursor result = DB.getCatalogs("SELECT * FROM Catalogs");
//        if (result.getCount() == 0) {
//            Toast.makeText(this, "No Catalogs Exist", Toast.LENGTH_SHORT).show();
//        } else {
//            while (result.moveToNext()) {
//                String title = result.getString(0);
//                byte[] image = result.getBlob(1);
//
//                products.add(new Product(title, image));
//            }
//        }
//        ListView listView = (ListView) findViewById(R.id.product_list);
//        ProductAdapter productAdapter = new ProductAdapter(this, products);
//        listView.setAdapter(productAdapter);

        //Floating add button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout frame = (FrameLayout) findViewById(R.id.fragment_container);
                frame.setVisibility(View.VISIBLE);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddProductFragment()).commit();
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
}