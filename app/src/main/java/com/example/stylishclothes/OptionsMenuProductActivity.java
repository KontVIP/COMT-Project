package com.example.stylishclothes;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
            case R.id.more_option_1:
                Toast.makeText(this, "Selected option 1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.more_option_2:
                Toast.makeText(this, "Selected option 2", Toast.LENGTH_SHORT).show();
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
