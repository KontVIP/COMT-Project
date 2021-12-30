package com.example.stylishclothes.catalog;

import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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


public class OneProductActivity extends OptionsMenuProductActivity implements View.OnClickListener {

    Context context = this;
    public static ViewPager viewPager = null;
    ViewPagerAdapter viewPagerAdapter;
    TextView priceTextView, availableTextView, descriptionTextView, titleDescriptionTextView, codeTextView;
    RadioButton SRadiobutton, MRadiobutton, LRadiobutton, XLRadiobutton, XXLRadiobutton;
    RadioGroup sizeRadioGroup;
    ImageButton instagramButton;
    Product product;
    String productId, title, category, selectedSize;
    DatabaseReference databaseReference;
    Button copyIdButton;

    private ImageView shoppingCartImageView, favoriteImageView;
    int shoppingCartState = 0;
    int favoriteState = 0;

    RecyclerView recyclerView;
    private List<Product> recommendedList = new ArrayList<>();
    private RecommendedListAdapter mAdapter;

    Toast toast;

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
            //title = arguments.getString("Title");
            //setTitle(title);
            productId = arguments.getString("ProductId");
            //category = arguments.getString("Category");
        }

        //firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Categories/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    product = dataSnapshot.child("Products").child(productId).getValue(Product.class);

                    if (product != null) {

                        setTitle(product.getTitle());

                        //Recycler view / Recommended list
                        mAdapter = new RecommendedListAdapter(recommendedList, context);
                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(mAdapter);


                        int childrenCount = (int) dataSnapshot.child("Products").getChildrenCount();
                        if (childrenCount <= 6) {
                            for (DataSnapshot dataSnapshot2 : dataSnapshot.child("Products").getChildren()) {
                                Product product = dataSnapshot2.getValue(Product.class);
                                if (!product.getId().equals(productId)) {
                                    recommendedList.add(product);
                                }
                            }
                        } else {
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
                            for (DataSnapshot dataSnapshot2 : dataSnapshot.child("Products").getChildren()) {
                                if (k != 6 && i == randomChildNum[k]) {
                                    k++;
                                    Product product = dataSnapshot2.getValue(Product.class);
                                    if (!product.getId().equals(productId)) {
                                        recommendedList.add(product);
                                    }
                                }
                                i++;
                            }
                        }
                        Collections.shuffle(recommendedList);

                        mAdapter.notifyDataSetChanged();




                        try {
                            setViewPager();
                            setRadioButtons();
                            checkAvailable();
                            checkPrice();
                            checkDescription();
                            checkProductCode();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    try {
                        toast.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    toast = Toast.makeText(context, "Додано до Списку бажань!", Toast.LENGTH_SHORT);
                } else {
                    favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    favoriteState = 0;
                    FirebaseDatabase.getInstance()
                            .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Favorites/" + productId).removeValue();
                    try {
                        toast.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    toast = Toast.makeText(context, "Видалено зі Списку бажань!", Toast.LENGTH_SHORT);
                }
                toast.show();
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

        shoppingCartImageView.setOnClickListener(this);

        sizeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.S_radiobutton:
                    case R.id.M_radiobutton:
                    case R.id.L_radiobutton:
                    case R.id.XL_radiobutton:
                    case R.id.XXL_radiobutton:
                        shoppingCartState = 0;
                        setShoppingCartImage();
                        break;
                }
            }
        });


        copyIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Product id", productId);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Product id copied!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        sizeRadioGroup = (RadioGroup) findViewById(R.id.size_radio_group);
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
        copyIdButton = (Button) findViewById(R.id.copy_id_button);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_shopping_cart_button:
                if (SRadiobutton.isChecked()) {
                    selectedSize = "S";
                } else if (MRadiobutton.isChecked()) {
                    selectedSize = "M";
                } else if (LRadiobutton.isChecked()) {
                    selectedSize = "L";
                } else if (XLRadiobutton.isChecked()) {
                    selectedSize = "XL";
                } else if (XXLRadiobutton.isChecked()) {
                    selectedSize = "XXL";
                }
                if (selectedSize != null) {
                    Log.d("d", "ShoppingCartState = " + shoppingCartState + " LOLOLO");
                    if (shoppingCartState == 0) {
                        shoppingCartImageView.setImageResource(R.drawable.ic_baseline_shopping_cart_24);
                        Long unixTime = System.currentTimeMillis();

                        DatabaseReference productReference = FirebaseDatabase.getInstance()
                                .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/" + unixTime.toString());

                        productReference.child("id").setValue(productId);
                        productReference.child("quantity").setValue(1);
                        productReference.child("size").setValue(selectedSize);
                        shoppingCartState = 1;
                    } else {
                        Log.d("d", "REMOVING VALUE LOLOLO");
                        shoppingCartState = 0;
                        shoppingCartImageView.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
                        DatabaseReference shoppingCartReference = FirebaseDatabase.getInstance()
                                .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/");
                        shoppingCartReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    shoppingCartReference.child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount() != 0 && snapshot.child("id").getValue(String.class).equals(productId) && snapshot.child("size").getValue(String.class).equals(selectedSize)) {
                                                shoppingCartReference.child(dataSnapshot.getKey()).removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(context, "Виберіть розмір!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void setShoppingCartImage() {
        if (SRadiobutton.isChecked()) {
            selectedSize = "S";
        } else if (MRadiobutton.isChecked()) {
            selectedSize = "M";
        } else if (LRadiobutton.isChecked()) {
            selectedSize = "L";
        } else if (XLRadiobutton.isChecked()) {
            selectedSize = "XL";
        } else if (XXLRadiobutton.isChecked()) {
            selectedSize = "XXL";
        }
        DatabaseReference shoppingCartReference = FirebaseDatabase.getInstance()
                .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/");
        shoppingCartReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    shoppingCartReference.child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount() != 0 && snapshot.child("id").getValue(String.class).trim().equals(productId.trim()) && snapshot.child("size").getValue(String.class).trim().equals(selectedSize.trim())) {
                                shoppingCartState = 1;
                                shoppingCartImageView.setImageResource(R.drawable.ic_baseline_shopping_cart_24);
                            } else if (shoppingCartState != 1) {
                                shoppingCartState = 0;
                                shoppingCartImageView.setImageResource(R.drawable.ic_baseline_add_shopping_cart_24);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    private void setRadioButtons() {
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