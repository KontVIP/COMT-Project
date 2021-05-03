package com.example.stylishclothes.catalog;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.example.stylishclothes.MainActivity;
import com.example.stylishclothes.OptionsMenuProductActivity;
import com.example.stylishclothes.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;


public class OneProductActivity extends OptionsMenuProductActivity {

    Context context = this;
    private Toolbar toolBar;
    ViewPager mViewPager;
    int[] images = {R.drawable.model_leather_coat, R.drawable.grapefruit, R.drawable.hoody, R.drawable.trousers};
    ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_product);
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //ViewPager
        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPagerAdapter = new ViewPagerAdapter(OneProductActivity.this, images);
        mViewPager.setAdapter(mViewPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);

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