package com.example.stylishclothes.catalog;

import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;

import com.example.stylishclothes.OptionsMenuProductActivity;
import com.example.stylishclothes.R;


public class OneProductActivity extends OptionsMenuProductActivity {

    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_product);
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //get title from CatalogFragment
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            String title = arguments.getString("Title");
            setTitle(title);
        }

        //Radiobuttons
        RadioButton SRadiobutton = (RadioButton) findViewById(R.id.S_radiobutton);
        animRadiobutton(SRadiobutton);

        RadioButton MRadiobutton = (RadioButton) findViewById(R.id.M_radiobutton);
        animRadiobutton(MRadiobutton);

        RadioButton LRadiobutton = (RadioButton) findViewById(R.id.L_radiobutton);
        animRadiobutton(LRadiobutton);

        RadioButton XLRadiobutton = (RadioButton) findViewById(R.id.XL_radiobutton);
        animRadiobutton(XLRadiobutton);

        RadioButton XXLRadiobutton = (RadioButton) findViewById(R.id.XXL_radiobutton);
        animRadiobutton(XXLRadiobutton);


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
        return super.onCreateOptionsMenu(menu);
    }

    public void animRadiobutton(RadioButton radioButton) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton.startAnimation(animation);
            }
        });
    }
}