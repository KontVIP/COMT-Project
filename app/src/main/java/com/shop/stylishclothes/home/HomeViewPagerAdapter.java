package com.shop.stylishclothes.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.shop.stylishclothes.R;
import com.shop.stylishclothes.catalog.OneProductActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class HomeViewPagerAdapter extends PagerAdapter {

    private Fragment fragment;
    private ArrayList imagesArrayList;
    private Context context;
    private ArrayList productIds;

    public HomeViewPagerAdapter(Fragment fragment, ArrayList imagesArray, ArrayList productIds, Context context) {

        this.fragment = fragment;
        this.imagesArrayList = imagesArray;
        this.productIds = productIds;
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = (fragment).getLayoutInflater();
        View itemView = inflater.inflate(R.layout.view_pager_item_main_ad, container, false);

        //Delete the ad data (search which exactly item must be deleted by URL)
        ImageView closeImageView = (ImageView) itemView.findViewById(R.id.close_image_view);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder closeBuilder = new AlertDialog.Builder(context);
                closeBuilder.setMessage("Are you sure you want to delete this ad?");
                closeBuilder.setCancelable(true);

                closeBuilder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseDatabase.getInstance().getReference("Home/advertisement/").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            if (dataSnapshot.child("img").getValue() != null && imagesArrayList.get(position).equals(dataSnapshot.child("img").getValue().toString())) {
                                                FirebaseStorage.getInstance().getReferenceFromUrl(Objects.requireNonNull(dataSnapshot.child("img").getValue(String.class))).delete();
                                                dataSnapshot.getRef().removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        });

                closeBuilder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog closeAlert = closeBuilder.create();
                closeAlert.show();
            }

        });

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageViewMain);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(imagesArrayList.get(position) + "").into(imageView);
        ((ViewPager) container).addView(itemView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!productIds.get(position).toString().equals("0")) {
                    Intent intent = new Intent(context, OneProductActivity.class);
                    intent.putExtra("ProductId", productIds.get(position).toString());
                    context.startActivity(intent);
                }
            }
        });

        return itemView;
    }

    @Override
    public int getCount() {
        return imagesArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}