package com.example.stylishclothes.catalog;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stylishclothes.MainActivity;
import com.example.stylishclothes.OptionsMenuProductActivity;
import com.example.stylishclothes.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import java.util.List;


public class OneProductActivity extends OptionsMenuProductActivity {

    Context context = this;
    private Toolbar toolBar;
    ViewPager mViewPager;
    ViewPagerAdapter mViewPagerAdapter;
    DBHelper DB = new DBHelper(this);
    String title, code, titleDescription, description, price;
    byte[] productImage_1, productImage_2, productImage_3, productImage_4, productImage_5;
    int availability, size_S, size_M, size_L, size_XL, size_XXL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_product);
        toolBar = findViewById(R.id.toolbar);
        try {
            setSupportActionBar(toolBar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //get title from ProductAdapter
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            title = arguments.getString("Title");
            setTitle(title);
        }

        //Load data
        Cursor result = DB.getProduct("SELECT * FROM Catalogs WHERE title = ?", title);
        if (result.getCount() == 0) {
            Toast.makeText(this, "No Catalogs Exist", Toast.LENGTH_SHORT).show();
        } else {
                result.moveToFirst();
                productImage_1 = result.getBlob(3);
                productImage_2 = result.getBlob(4);
                productImage_3 = result.getBlob(5);
                productImage_4 = result.getBlob(6);
                productImage_5 = result.getBlob(7);
                availability = result.getInt(8);
                code = result.getString(9);
                size_S = result.getInt(10);
                size_M = result.getInt(11);
                size_L = result.getInt(12);
                size_XL = result.getInt(13);
                size_XXL = result.getInt(14);
                titleDescription = result.getString(15);
                description = result.getString(16);
                price = result.getString(17);
        }

        //ViewPager & Images
        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                OneProductActivity.this,
                productImage_1,
                productImage_2,
                productImage_3,
                productImage_4,
                productImage_5);
        mViewPager.setAdapter(mViewPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);

        //Radio buttons
        RadioButton SRadiobutton = (RadioButton) findViewById(R.id.S_radiobutton);
        animRadiobutton(SRadiobutton);
        if(size_S == 0) SRadiobutton.setVisibility(View.GONE);

        RadioButton MRadiobutton = (RadioButton) findViewById(R.id.M_radiobutton);
        animRadiobutton(MRadiobutton);
        if(size_M == 0) MRadiobutton.setVisibility(View.GONE);

        RadioButton LRadiobutton = (RadioButton) findViewById(R.id.L_radiobutton);
        animRadiobutton(LRadiobutton);
        if(size_L == 0) LRadiobutton.setVisibility(View.GONE);

        RadioButton XLRadiobutton = (RadioButton) findViewById(R.id.XL_radiobutton);
        animRadiobutton(XLRadiobutton);
        if(size_XL == 0) XLRadiobutton.setVisibility(View.GONE);

        RadioButton XXLRadiobutton = (RadioButton) findViewById(R.id.XXL_radiobutton);
        animRadiobutton(XXLRadiobutton);
        if(size_XXL == 0) XXLRadiobutton.setVisibility(View.GONE);

        //Instagram ImageButton
        ImageButton instagramButton = (ImageButton) findViewById(R.id.instagram_button);
        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/staylish_clotches");
                Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                insta.setPackage("com.instagram.android");

                if (isIntentAvailable(context, insta)){
                    startActivity(insta);
                } else{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/staylish_clotches")));
                }
            }
        });

        //Code
        TextView codeTextView = (TextView) findViewById(R.id.code_text_view);
        codeTextView.setText("Код: " + code);

        //Description
        TextView titleDescriptionTextView = (TextView) findViewById(R.id.title_description_text_view);
        titleDescriptionTextView.setText(titleDescription);
        TextView descriptionTextView = (TextView) findViewById(R.id.description_text_view);
        descriptionTextView.setText(description);

        //Availability of product
        TextView availableTextView = (TextView) findViewById(R.id.available_text_view);
        if(availability == 0) {
            availableTextView.setText("Немає\nу наявності");
            availableTextView.setTextColor(Color.RED);
        } else {
            availableTextView.setText("У наявності");
            availableTextView.setTextColor(Color.parseColor("#008000"));
        }

        //Price
        TextView priceTextView = findViewById(R.id.price);
        priceTextView.setText("₴" + price);

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

    private boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


}