package com.example.stylishclothes.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.stylishclothes.R;
import com.example.stylishclothes.catalog.ViewPagerAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;


    Integer[] imageId = {R.drawable.clothes_1, R.drawable.clothes_2, R.drawable.hoody, R.drawable.trousers_main};
    String[] imagesName = {"image1","image2","image3","image4"};

    Timer timer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Stylish Clothes");

        viewPager = (ViewPager) rootView.findViewById(R.id.pager);

        PagerAdapter adapter = new HomeViewPagerAdapter(HomeFragment.this,imageId,imagesName);
        viewPager.setAdapter(adapter);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                viewPager.post(new Runnable(){

                    @Override
                    public void run() {
                        viewPager.setCurrentItem((viewPager.getCurrentItem()+1)%imageId.length);
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 6000, 6000);


        return rootView;
    }

}
