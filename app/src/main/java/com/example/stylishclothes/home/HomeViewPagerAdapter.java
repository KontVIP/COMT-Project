package com.example.stylishclothes.home;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.stylishclothes.R;

public class HomeViewPagerAdapter extends PagerAdapter {

    private Fragment fragment;
    private Integer[] imagesArray;
    private String[] namesArray;

    public HomeViewPagerAdapter(Fragment fragment, Integer[] imagesArray, String[] namesArray){

        this.fragment = fragment;
        this.imagesArray = imagesArray;
        this.namesArray = namesArray;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (fragment).getLayoutInflater();

        View itemView = inflater.inflate(R.layout.view_pager_item, container, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewMain);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(imagesArray[position]);
        ((ViewPager)container).addView(itemView);

        return itemView;
    }

    @Override
    public int getCount() {
        return imagesArray.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View)object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}