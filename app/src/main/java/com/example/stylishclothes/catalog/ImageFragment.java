package com.example.stylishclothes.catalog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.stylishclothes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

public class ImageFragment extends Fragment {

    TouchImageView touchImageView;
    public static int viewPagerPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);

        //Get data from ViewPagerAdapter
        Bundle bundle = this.getArguments();
        String img_1 = bundle.getString("Image_1");
        String img_2 = bundle.getString("Image_2");
        String img_3 = bundle.getString("Image_3");
        String img_4 = bundle.getString("Image_4");
        String img_5 = bundle.getString("Image_5");
        int currentPosition = bundle.getInt("CurrentPosition");

        //ViewPager
        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        ProductImageFragmentAdapter adapter = new ProductImageFragmentAdapter(getContext(), img_1, img_2, img_3, img_4, img_5);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);
        viewPager.setCurrentItem(viewPagerPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                OneProductActivity.viewPager.setCurrentItem(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //"Back action"
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.image_frame);
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

//        //TouchImageView
//        touchImageView = (TouchImageView) rootView.findViewById(R.id.touch_image);
//        Picasso.get().load(img).into(touchImageView);

        //Floating close button
        FloatingActionButton closeButton = (FloatingActionButton) rootView.findViewById(R.id.floating_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.image_frame);
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        });

        return rootView;
    }
}
