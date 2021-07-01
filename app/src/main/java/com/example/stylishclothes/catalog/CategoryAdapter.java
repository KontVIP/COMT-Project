package com.example.stylishclothes.catalog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.stylishclothes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {

    FragmentManager mFragmentManager;
    DatabaseReference databaseReference;

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

        databaseReference = FirebaseDatabase.getInstance().getReference("Categories");

        final Category currentCategory = getItem(position);
        //TextView
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_catalog_text_view);
        titleTextView.setText(currentCategory.title);

        //ImageView
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.title_catalog_image_view);
        Picasso.get().load(currentCategory.imagePath).into(imageView);

        //LinearLayout
        View listCategoryItem = (View) listItemView.findViewById(R.id.list_catalog_item);
        listCategoryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toActivityProducts(currentCategory);
            }
        });


        //Close ImageButton
        ImageButton closeImageButton = (ImageButton) listItemView.findViewById(R.id.delete_category_image_button);
        closeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder closeBuilder = new AlertDialog.Builder(getContext());
                closeBuilder.setMessage("Are you sure you want to delete this category?");
                closeBuilder.setCancelable(true);

                closeBuilder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentCategory.imagePath);
                                photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        databaseReference.child(currentCategory.title).setValue(null);
                                        Toast.makeText(getContext(), "Category deleted", Toast.LENGTH_SHORT).show();
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

        return listItemView;
    }

    private void toActivityProducts(Category currentCategory) {
            Intent intent = new Intent(getContext(), ProductListActivity.class);
            intent.putExtra("Title", currentCategory.title);
            getContext().startActivity(intent);
    }
}
