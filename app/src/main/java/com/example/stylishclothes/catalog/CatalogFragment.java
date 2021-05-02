package com.example.stylishclothes.catalog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.stylishclothes.R;

import java.util.ArrayList;

public class CatalogFragment extends Fragment {

    private ArrayList<Category> categories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);
        getActivity().setTitle("Stylish Clothes");
        //setRetainInstance(true);
        //List
        categories = new ArrayList<Category>();
        categories.add(new Category("Штани", R.drawable.trousers, new Intent(getContext(), TrousersActivity.class)));
        categories.add(new Category("Толстовки", R.drawable.hoody, new Intent(getContext(), ProductsActivity.class)));

        ListView listView = (ListView) rootView.findViewById(R.id.category_list);
        FragmentManager fragmentManager = getFragmentManager();
        CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(), categories, fragmentManager);
        listView.setAdapter(categoryAdapter);
        

        return rootView;
    }

}
