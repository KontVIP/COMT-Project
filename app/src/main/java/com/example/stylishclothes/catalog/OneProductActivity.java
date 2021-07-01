package com.example.stylishclothes.catalog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.stylishclothes.OptionsMenuProductActivity;
import com.example.stylishclothes.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class OneProductActivity extends OptionsMenuProductActivity {

    Context context = this;
    public static ViewPager viewPager = null;
    ViewPagerAdapter viewPagerAdapter;
    TextView priceTextView, availableTextView, descriptionTextView, titleDescriptionTextView, codeTextView;
    RadioButton SRadiobutton, MRadiobutton, LRadiobutton, XLRadiobutton, XXLRadiobutton;
    ImageButton instagramButton;
    Product product;
    String productId, title, category;
    DatabaseReference databaseReference;

    private ImageView shoppingCartImageView, favoriteImageView;
    int shoppingCartState = 0;
    int favoriteState = 0;

    RecyclerView recyclerView;
    private List<Product> recommendedList = new ArrayList<>();
    private RecommendedListAdapter mAdapter;

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
            category = arguments.getString("Category");
        }

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Categories/" + category + "/Products");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                product = snapshot.child(productId).getValue(Product.class);

                try {
                    setViewPager();
                    checkRadioButtons();
                    checkAvailable();
                    checkPrice();
                    checkDescription();
                    checkProductCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler view / Recommended list
        mAdapter = new RecommendedListAdapter(recommendedList, context);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        prepareRecommendedListData();

        //Shopping cart button (ImageView)
        FirebaseDatabase.getInstance()
                .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/" + productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            shoppingCartImageView.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
                            shoppingCartState = 0;
                        } else {
                            shoppingCartImageView.setImageResource(R.drawable.ic_baseline_shopping_cart_24);
                            shoppingCartState = 1;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


        shoppingCartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shoppingCartState == 1) {
                    shoppingCartImageView.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
                    shoppingCartState = 0;
                    FirebaseDatabase.getInstance()
                            .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/" + productId).removeValue();
                    Toast.makeText(context, "Видалено з Кошику!", Toast.LENGTH_SHORT).show();
                } else if (availableTextView.getText().equals("У наявності")) {
                    if (SRadiobutton.isChecked() || MRadiobutton.isChecked() || LRadiobutton.isChecked() || XLRadiobutton.isChecked() || XXLRadiobutton.isChecked()) {
                        if (shoppingCartState == 0) {
                            shoppingCartImageView.setImageResource(R.drawable.ic_baseline_shopping_cart_24);
                            shoppingCartState = 1;
                            String size = "None";
                            if (SRadiobutton.isChecked()) {
                                size = "S";
                            } else if (MRadiobutton.isChecked()) {
                                size = "M";
                            } else if (LRadiobutton.isChecked()) {
                                size = "L";
                            } else if (XLRadiobutton.isChecked()) {
                                size = "XL";
                            } else if (XXLRadiobutton.isChecked()) {
                                size = "XXL";
                            }
                            FirebaseDatabase.getInstance()
                                    .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/" + productId).child("Size").setValue(size);
                            Toast.makeText(context, "Додано до Кошику!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Виберіть розмір!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Товару немає в наявності!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Favorite button (ImageView)
        FirebaseDatabase.getInstance()
                .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Favorites/" + productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                            favoriteState = 0;
                        } else {
                            favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_24);
                            favoriteState = 1;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favoriteState == 0) {
                    favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_24);
                    favoriteState = 1;
                    FirebaseDatabase.getInstance()
                            .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Favorites/").child(productId).setValue(1);
                    Toast.makeText(context, "Додано до Списку бажань!", Toast.LENGTH_SHORT).show();
                } else {
                    favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    favoriteState = 0;
                    FirebaseDatabase.getInstance()
                            .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Favorites/" + productId).removeValue();
                    Toast.makeText(context, "Видалено зі Списку бажань!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Instagram ImageButton
        instagramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/shop.stylish.clothes");
                Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                insta.setPackage("com.instagram.android");

                if (isIntentAvailable(context, insta)) {
                    startActivity(insta);
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/staylish_clotches")));
                }
            }
        });


        //ViewPager page listener
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ImageFragment.viewPagerPosition = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

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
        shoppingCartImageView = (ImageView) findViewById(R.id.add_shopping_cart_button);
        favoriteImageView = (ImageView) findViewById(R.id.add_favorite_button);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_recommended);
        descriptionTextView = (TextView) findViewById(R.id.description_text_view);
        titleDescriptionTextView = (TextView) findViewById(R.id.title_description_text_view);
        codeTextView = (TextView) findViewById(R.id.code_text_view);
        viewPager = (ViewPager) findViewById(R.id.pager);
        priceTextView = (TextView) findViewById(R.id.price);
        availableTextView = (TextView) findViewById(R.id.available_text_view);
        SRadiobutton = (RadioButton) findViewById(R.id.S_radiobutton);
        MRadiobutton = (RadioButton) findViewById(R.id.M_radiobutton);
        LRadiobutton = (RadioButton) findViewById(R.id.L_radiobutton);
        XLRadiobutton = (RadioButton) findViewById(R.id.XL_radiobutton);
        XXLRadiobutton = (RadioButton) findViewById(R.id.XXL_radiobutton);
        instagramButton = (ImageButton) findViewById(R.id.instagram_button);
    }

    private void prepareRecommendedListData() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int childrenCount = (int) snapshot.getChildrenCount();
                if (childrenCount <= 6) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        if (!product.getId().equals(productId)) {
                            recommendedList.add(product);
                        }
                    }
                } else {
                    Random random = new Random();
                    // [0 ; childrenCount]
                    int[] randomChildNum = new int[6];
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    for (int i = 0; i < childrenCount; i++) {
                        list.add(i);
                    }
                    Collections.shuffle(list);
                    for (int i = 0; i < 6; i++) {
                        randomChildNum[i] = list.get(i);
                    }

                    Arrays.sort(randomChildNum);
                    int i = 0, k = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (k != 6 && i == randomChildNum[k]) {
                            k++;
                            Product product = dataSnapshot.getValue(Product.class);
                            if (!product.getId().equals(productId)) {
                                recommendedList.add(product);
                            }
                        }
                        i++;
                    }
                }
                Collections.shuffle(recommendedList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mAdapter.notifyDataSetChanged();
    }

    private void setViewPager() {
        try {
            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                    OneProductActivity.this,
                    product.getFirstImagePath(),
                    product.getSecondImagePath(),
                    product.getThirdImagePath(),
                    product.getFourthImagePath(),
                    product.getFifthImagePath(),
                    productId);
            viewPager.setAdapter(viewPagerAdapter);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
            tabLayout.setupWithViewPager(viewPager, true);
        } catch (Exception e) {

        }
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
        if (!product.isSize_S()) {
            SRadiobutton.setVisibility(View.GONE);
        }

        animRadiobutton(MRadiobutton);
        if (!product.isSize_M()) {
            MRadiobutton.setVisibility(View.GONE);
        }

        animRadiobutton(LRadiobutton);
        if (!product.isSize_L()) {
            LRadiobutton.setVisibility(View.GONE);
        }

        animRadiobutton(XLRadiobutton);
        if (!product.isSize_XL()) {
            XLRadiobutton.setVisibility(View.GONE);
        }

        animRadiobutton(XXLRadiobutton);
        if (!product.isSize_XXL()) {
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