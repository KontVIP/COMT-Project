package com.example.stylishclothes.catalog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.stylishclothes.OptionsMenuProductActivity;
import com.example.stylishclothes.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class OneProductActivity extends OptionsMenuProductActivity {

    Context context = this;
    ViewPager mViewPager;
    ViewPagerAdapter mViewPagerAdapter;
    TextView priceTextView, availableTextView, descriptionTextView, titleDescriptionTextView, codeTextView;
    RadioButton SRadiobutton, MRadiobutton, LRadiobutton, XLRadiobutton, XXLRadiobutton;
    ImageButton instagramButton;
    Product product;
    String productId, title;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_product);
        Toolbar toolBar = findViewById(R.id.toolbar);
        try {
            setSupportActionBar(toolBar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        init();

        //get data from ProductAdapter
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            title = arguments.getString("Title");
            setTitle(title);
            productId = arguments.getString("ProductId");
        }

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Product");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                product = snapshot.child(productId).getValue(Product.class);

                setViewPager();
                checkRadioButtons();
                checkAvailable();
                checkPrice();
                checkDescription();
                checkProductCode();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Instagram ImageButton
        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/staylish_clotches");
                Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                insta.setPackage("com.instagram.android");

                if (isIntentAvailable(context, insta)) {
                    startActivity(insta);
                } else {
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


    private void init() {
        descriptionTextView = (TextView) findViewById(R.id.description_text_view);
        titleDescriptionTextView = (TextView) findViewById(R.id.title_description_text_view);
        codeTextView = (TextView) findViewById(R.id.code_text_view);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        priceTextView = (TextView) findViewById(R.id.price);
        availableTextView = (TextView) findViewById(R.id.available_text_view);
        SRadiobutton = (RadioButton) findViewById(R.id.S_radiobutton);
        MRadiobutton = (RadioButton) findViewById(R.id.M_radiobutton);
        LRadiobutton = (RadioButton) findViewById(R.id.L_radiobutton);
        XLRadiobutton = (RadioButton) findViewById(R.id.XL_radiobutton);
        XXLRadiobutton = (RadioButton) findViewById(R.id.XXL_radiobutton);
        instagramButton = (ImageButton) findViewById(R.id.instagram_button);
    }

    private void setViewPager() {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                OneProductActivity.this,
                product.getFirstImagePath(),
                product.getSecondImagePath(),
                product.getThirdImagePath(),
                product.getFourthImagePath(),
                product.getFifthImagePath(),
                productId);
        mViewPager.setAdapter(mViewPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);
    }

    private void checkAvailable() {
        if (product.isAvailable()) {
            availableTextView.setText("У наявності");
            availableTextView.setTextColor(Color.parseColor("#008000"));
        } else {
            availableTextView.setText("Немає\nу наявності");
            availableTextView.setTextColor(Color.RED);
        }
    }

    private void checkRadioButtons() {
        animRadiobutton(SRadiobutton);
        if (!product.isCheckedSizeS()) {
            SRadiobutton.setVisibility(View.GONE);
        }

        animRadiobutton(MRadiobutton);
        if (!product.isCheckedSizeM()) {
            MRadiobutton.setVisibility(View.GONE);
        }

        animRadiobutton(LRadiobutton);
        if (!product.isCheckedSizeL()) {
            LRadiobutton.setVisibility(View.GONE);
        }

        animRadiobutton(XLRadiobutton);
        if (!product.isCheckedSizeXL()) {
            XLRadiobutton.setVisibility(View.GONE);
        }

        animRadiobutton(XXLRadiobutton);
        if (!product.isCheckedSizeXXL()) {
            XXLRadiobutton.setVisibility(View.GONE);
        }
    }

    private void checkPrice() {
        priceTextView.setText("₴" + product.getPrice());
    }

    private void checkProductCode() {
        codeTextView.setText("Код: " + product.getProductCode());
    }

    private void checkDescription() {
        titleDescriptionTextView.setText(product.getTitleDescription());
        descriptionTextView.setText(product.getDescription());
    }

    private boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


}