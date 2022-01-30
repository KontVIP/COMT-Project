package com.shop.stylishclothes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shop.stylishclothes.R;

public class OptionsMenuProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.product_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shopping_cart_icon:
                openShoppingCartFragment();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openShoppingCartFragment() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Fragment", R.id.nav_shopping_cart);
        startActivity(intent);
    }
}
