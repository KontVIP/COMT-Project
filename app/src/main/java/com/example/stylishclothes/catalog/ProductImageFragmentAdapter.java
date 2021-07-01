package com.example.stylishclothes.catalog;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.stylishclothes.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

class ProductImageFragmentAdapter extends PagerAdapter {

    Context context;
    LayoutInflater mLayoutInflater;
    String img_1, img_2, img_3, img_4, img_5;


    // Viewpager Constructor
    public ProductImageFragmentAdapter(Context context, String img_1, String img_2, String img_3, String img_4, String img_5) {
        this.context = context;
        this.img_1 = img_1;
        this.img_2 = img_2;
        this.img_3 = img_3;
        this.img_4 = img_4;
        this.img_5 = img_5;

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        int count = 0;
        if (!img_1.equals("")) count++;
        if (!img_2.equals("")) count++;
        if (!img_3.equals("")) count++;
        if (!img_4.equals("")) count++;
        if (!img_5.equals("")) count++;
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
        View itemView = mLayoutInflater.inflate(R.layout.touch_image_viewpager_item, container, false);

        // referencing the image view from the item.xml file
        TouchImageView touchImageView = (TouchImageView) itemView.findViewById(R.id.touch_image);

        // setting the image in the TouchImageView
        try {
            switch (position) {
                case 0:
                    Picasso.get().load(img_1).into(touchImageView);
                    break;
                case 1:
                    Picasso.get().load(img_2).into(touchImageView);
                    break;
                case 2:
                    Picasso.get().load(img_3).into(touchImageView);
                    break;
                case 3:
                    Picasso.get().load(img_4).into(touchImageView);
                    break;
                case 4:
                    Picasso.get().load(img_5).into(touchImageView);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}