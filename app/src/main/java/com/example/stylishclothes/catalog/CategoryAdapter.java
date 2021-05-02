package com.example.stylishclothes.catalog;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.stylishclothes.R;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {
    FragmentManager mFragmentManager;

    public CategoryAdapter(Activity context, ArrayList<Category> category, FragmentManager fragmentManager) {
        super(context, 0, category);
        mFragmentManager = fragmentManager;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_catalog_item,
                    parent,
                    false);
        }

        final Category currentCategory = getItem(position);
        //TextView
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_catalog_text_view);
        titleTextView.setText(currentCategory.getTitle());

        //ImageView
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.title_catalog_image_view);
        imageView.setImageResource(currentCategory.getImage());

        //LinearLayout
        View listCategoryItem = (View) listItemView.findViewById(R.id.list_catalog_item);
        listCategoryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toActivityProducts(currentCategory.getIntent());
            }
        });


        return listItemView;
    }

    private void toActivityProducts(Intent intent) {
        getContext().startActivity(intent);
        //mFragmentManager.beginTransaction().replace(R.id.fragment_container, new ProductsFragment()).commit();

    }
}
