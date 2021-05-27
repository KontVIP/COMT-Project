package com.example.stylishclothes.catalog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.stylishclothes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class ImageFragment extends Fragment {

    TouchImageView touchImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);

        //Get data from ViewPagerAdapter
        Bundle bundle = this.getArguments();
        String img = bundle.getString("Image");

        //"Back action"
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.image_frame);
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        //TouchImageView
        touchImageView = (TouchImageView) rootView.findViewById(R.id.touch_image);
        Picasso.get().load(img).into(touchImageView);

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
