package com.example.stylishclothes.catalog;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

import com.example.stylishclothes.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class ViewPagerAdapter extends PagerAdapter {

    Context mContext;
    String mImage_1, mImage_2, mImage_3, mImage_4, mImage_5;
    private boolean isImageScaled = false;
    LayoutInflater mLayoutInflater;
    Bitmap bitmap;
    FragmentManager mFragmentManager;
    String productId, currentImagePath;

    // Viewpager Constructor
    public ViewPagerAdapter(androidx.fragment.app.FragmentManager fragmentManager,
                            Context context,
                            String image_1,
                            String image_2,
                            String image_3,
                            String image_4,
                            String image_5,
                            String id) {
        mContext = context;
        mImage_1 = image_1;
        mImage_2 = image_2;
        mImage_3 = image_3;
        mImage_4 = image_4;
        mImage_5 = image_5;
        productId = id;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFragmentManager = fragmentManager;
    }

    @Override
    public int getCount() {
        // return the number of images
        int count = 0;
        if (!mImage_1.equals("")) count++;
        if (!mImage_2.equals("")) count++;
        if (!mImage_3.equals("")) count++;
        if (!mImage_4.equals("")) count++;
        if (!mImage_5.equals("")) count++;
        return count;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.view_pager_item, container, false);

        // referencing the image view from the item.xml file
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewMain);

        // setting the image in the imageView
        try {
            switch (position) {
                case 0:
                    Picasso.get().load(mImage_1).into(imageView);
                    break;
                case 1:
                    Picasso.get().load(mImage_2).into(imageView);
                    break;
                case 2:
                    Picasso.get().load(mImage_3).into(imageView);
                    break;
                case 3:
                    Picasso.get().load(mImage_4).into(imageView);
                    break;
                case 4:
                    Picasso.get().load(mImage_5).into(imageView);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                Fragment fragment = new ImageFragment();
                ft.add(R.id.image_frame, fragment);
                ft.setCustomAnimations(R.anim.alpha, 0, R.anim.anti_alpha, 0);
                Bundle bundle = new Bundle();
                bundle.putString("Image_1", mImage_1);
                bundle.putString("Image_2", mImage_2);
                bundle.putString("Image_3", mImage_3);
                bundle.putString("Image_4", mImage_4);
                bundle.putString("Image_5", mImage_5);
                bundle.putInt("CurrentPosition", position);
                fragment.setArguments(bundle);
                ft.show(fragment);
                ft.commit();
            }
        });

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        switch (position) {
            case 0:
                currentImagePath = mImage_1;
                break;
            case 1:
                currentImagePath = mImage_2;
                break;
            case 2:
                currentImagePath = mImage_3;
                break;
            case 3:
                currentImagePath = mImage_4;
                break;
            case 4:
                currentImagePath = mImage_5;
                break;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}