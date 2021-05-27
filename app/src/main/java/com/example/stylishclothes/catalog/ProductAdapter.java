package com.example.stylishclothes.catalog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stylishclothes.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    private Intent mIntent;
    private DatabaseReference databaseReference;
    private StorageReference storageRef;
    StorageReference photoRef;

    public ProductAdapter(Activity context, ArrayList<Product> products, Intent intent) {
        super(context, 0, products);
        mIntent = intent;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_product_item,
                    parent,
                    false);
        }

        //firebase
        storageRef = FirebaseStorage.getInstance().getReference("ImageDB");
        databaseReference = FirebaseDatabase.getInstance().getReference("Product");

        final Product currentProduct = getItem(position);
        final String productId = currentProduct.getId();
        //title textView
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);
        titleTextView.setText(currentProduct.getTitle());

        //Title imageView
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.title_image_view);
        Picasso.get().load(currentProduct.getTitleImagePath()).into(imageView);


        //LinearLayout
        View listProductItem = (View) listItemView.findViewById(R.id.list_product_item);
        listProductItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentProduct.DELETED) {
                    intentOneProductActivity(currentProduct);
                } else {
                    Toast.makeText(getContext(), "Product deleted! Refresh a page!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Popup menu
        ImageButton showMenu = (ImageButton) listItemView.findViewById(R.id.show_menu);
        showMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu (getContext(), showMenu);
                menu.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener ()
                {
                    @Override
                    public boolean onMenuItemClick (MenuItem item)
                    {
                        int id = item.getItemId();
                        switch (id)
                        {
                            case R.id.item_settings:
                                if(!currentProduct.DELETED) {
                                    intentEditProductActivity(currentProduct);
                                } else {
                                    Toast.makeText(getContext(), "Product deleted! Refresh a page!", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.delete_item:
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("Are you sure you want to delete this product?");
                                builder.setCancelable(true);

                                builder.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                currentProduct.markDeleted();
                                                try {
                                                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                                    photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentProduct.getTitleImagePath());
                                                    photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            try{
                                                                photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentProduct.getFirstImagePath());
                                                                photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        try {
                                                                            photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentProduct.getSecondImagePath());
                                                                            photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    try {
                                                                                        photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentProduct.getThirdImagePath());
                                                                                        photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                try {
                                                                                                    photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentProduct.getFourthImagePath());
                                                                                                    photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            try {
                                                                                                                photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentProduct.getFifthImagePath());
                                                                                                                photoRef.delete();
                                                                                                            } catch (Exception e) {
                                                                                                                e.printStackTrace();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                                } catch (Exception e) {

                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    } catch (Exception e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                }
                                                                            });
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                });
                                                            } catch (Exception e) {

                                                            }
                                                        }
                                                    });
                                                    //next lines provoke crash and then executes catch (It's necessarily)
                                                    photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentProduct.getFifthImagePath());
                                                    photoRef.delete();
                                                } catch (Exception e) {
                                                    e.printStackTrace();

                                                }
                                                databaseReference.child(productId).setValue(null);
                                            }
                                        });

                                builder.setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert = builder.create();
                                alert.show();
                                break;
                        }
                        return true;
                    }
                });
                menu.inflate (R.menu.product_popup_menu);
                menu.show();
            }
        });


        return listItemView;
    }

    private void intentOneProductActivity(Product currentProduct) {
        Intent intent = new Intent(getContext(), OneProductActivity.class);
        intent.putExtra("ProductId", currentProduct.getId());
        intent.putExtra("Title", currentProduct.getTitle());
        getContext().startActivity(intent);
    }

    private void intentEditProductActivity(Product currentProduct) {
        Intent intent = new Intent(getContext(), EditProductActivity.class);
        intent.putExtra("Title", currentProduct.getTitle());
        intent.putExtra("ProductId", currentProduct.getId());
        intent.putExtra("Intent", mIntent);
        getContext().startActivity(intent);
    }
}
