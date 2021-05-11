package com.example.stylishclothes.catalog;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

import java.io.ByteArrayOutputStream;
import java.util.Objects;

class ViewPagerAdapter extends PagerAdapter {

    Context mContext;
    byte[] mImage_1, mImage_2, mImage_3, mImage_4, mImage_5;
    private boolean isImageScaled = false;
    LayoutInflater mLayoutInflater;
    Bitmap bitmap;
    FragmentManager mFragmentManager;

    // Viewpager Constructor
    public ViewPagerAdapter(androidx.fragment.app.FragmentManager fragmentManager,
                            Context context,
                            byte[] image_1,
                            byte[] image_2,
                            byte[] image_3,
                            byte[] image_4,
                            byte[] image_5) {
        mContext = context;
        mImage_1 = image_1;
        mImage_2 = image_2;
        mImage_3 = image_3;
        mImage_4 = image_4;
        mImage_5 = image_5;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFragmentManager = fragmentManager;
    }

    @Override
    public int getCount() {
        // return the number of images
        int count = 0;
        if (mImage_1 != null) count++;
        if (mImage_2 != null) count++;
        if (mImage_3 != null) count++;
        if (mImage_4 != null) count++;
        if (mImage_5 != null) count++;
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
                    bitmap = BitmapFactory.decodeByteArray(mImage_1, 0, mImage_1.length);
                    break;
                case 1:
                    bitmap = BitmapFactory.decodeByteArray(mImage_2, 0, mImage_2.length);
                    break;
                case 2:
                    bitmap = BitmapFactory.decodeByteArray(mImage_3, 0, mImage_3.length);
                    break;
                case 3:
                    bitmap = BitmapFactory.decodeByteArray(mImage_4, 0, mImage_4.length);
                    break;
                case 4:
                    bitmap = BitmapFactory.decodeByteArray(mImage_5, 0, mImage_5.length);
                    break;
            }
            imageView.setImageBitmap(bitmap);
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
                bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                bundle.putByteArray("image", byteArray);
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
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}