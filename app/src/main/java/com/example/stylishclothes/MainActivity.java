package com.example.stylishclothes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;

import com.example.stylishclothes.catalog.CatalogFragment;
import com.example.stylishclothes.catalog.OneProductActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ToolBar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Stylish Clothes"));

        //Fragments
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        //get value from shopping cart icon
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            if (arguments.getInt("Fragment") == R.id.nav_shopping_cart) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShoppingCartFragment()).commit();
                bottomNav.setSelectedItemId(R.id.nav_shopping_cart);
            }
        }


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment;
                    selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_catalog:
                            //openProductActivity();
                            selectedFragment = new CatalogFragment();
                            break;
                        case R.id.nav_shopping_cart:
                            selectedFragment = new ShoppingCartFragment();
                            break;
                        case R.id.nav_favorites:
                            selectedFragment = new FavoritesFragment();
                            break;
                        case R.id.nav_menu:
                            selectedFragment = new MenuFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };

    public void openProductActivity() {
        Intent intent = new Intent(this, OneProductActivity.class);
        startActivity(intent);
    }


}