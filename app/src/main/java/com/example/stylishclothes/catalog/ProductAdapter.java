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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    private Intent mIntent;
    private DatabaseReference databaseReference;
    private StorageReference storageRef;
    StorageReference photoRef;
    final int DEFAULT_PRODUCT = 0;
    final int FAVORITE_PRODUCT = 1;
    final int SHOPPING_CART_PRODUCT = 2;
    int productType;


    public ProductAdapter(Activity context, ArrayList<Product> products, int productType) {
        super(context, 0, products);
        this.productType = productType;
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


        //Price text view
        TextView priceTextView = (TextView) listItemView.findViewById(R.id.price_text_view);
        priceTextView.setText("₴" + currentProduct.getPrice());

        //LinearLayout
        View listProductItem = (View) listItemView.findViewById(R.id.list_product_item);
        listProductItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentProduct.DELETED) {
                    intentOneProductActivity(currentProduct);
                } else {
                    Toast.makeText(getContext(), "Product deleted! Refresh a page!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ImageButton showMenu = (ImageButton) listItemView.findViewById(R.id.show_menu);
        if (productType == DEFAULT_PRODUCT) {
            showMenu.setVisibility(View.VISIBLE);
            showMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(getContext(), showMenu);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            switch (id) {
                                case R.id.item_settings:
                                    if (!currentProduct.DELETED) {
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
                                                        databaseReference = FirebaseDatabase.getInstance().getReference("Categories/" + currentProduct.getCategory() + "/Products");
                                                        databaseReference.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                Product product = snapshot.child(productId).getValue(Product.class);
                                                                try {
                                                                    if (product.getTitleImagePath().equals("")) {
                                                                        databaseReference.child(productId).setValue(null);
                                                                        return;
                                                                    }

                                                                    Toast.makeText(getContext(), "Product deleted", Toast.LENGTH_SHORT).show();
                                                                    photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getTitleImagePath());
                                                                    photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            try {
                                                                                if (product.getFirstImagePath().equals("")) {
                                                                                    databaseReference.child(productId).setValue(null);
                                                                                    return;
                                                                                }

                                                                                photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getFirstImagePath());
                                                                                photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        try {
                                                                                            if (product.getSecondImagePath().equals("")) {
                                                                                                databaseReference.child(productId).setValue(null);
                                                                                                return;
                                                                                            }

                                                                                            photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getSecondImagePath());
                                                                                            photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    try {
                                                                                                        if (product.getThirdImagePath().equals("")) {
                                                                                                            databaseReference.child(productId).setValue(null);
                                                                                                            return;
                                                                                                        }

                                                                                                        photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getThirdImagePath());
                                                                                                        photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                try {
                                                                                                                    if (product.getFourthImagePath().equals("")) {
                                                                                                                        databaseReference.child(productId).setValue(null);
                                                                                                                        return;
                                                                                                                    }

                                                                                                                    photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getFourthImagePath());
                                                                                                                    photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            try {
                                                                                                                                if (product.getFifthImagePath().equals("")) {
                                                                                                                                    databaseReference.child(productId).setValue(null);
                                                                                                                                    return;
                                                                                                                                }

                                                                                                                                photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getFifthImagePath());
                                                                                                                                photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                        databaseReference.child(productId).setValue(null);
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
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
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
                    menu.inflate(R.menu.product_popup_menu);
                    menu.show();
                }
            });
        } else if (productType == FAVORITE_PRODUCT) {
            showMenu.setVisibility(View.VISIBLE);
            showMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(getContext(), showMenu);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_favorite_item:
                                    FirebaseDatabase.getInstance()
                                            .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Favorites/" + productId).removeValue();
                                    Toast.makeText(getContext(), "Видалено!", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return true;
                        }
                    });
                    menu.inflate(R.menu.favorite_product_popup_menu);
                    menu.show();
                }
            });
        } else if (productType == SHOPPING_CART_PRODUCT) {
            showMenu.setVisibility(View.VISIBLE);
            showMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(getContext(), showMenu);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_favorite_item:
                                    FirebaseDatabase.getInstance()
                                            .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/" + productId).removeValue();
                                    Toast.makeText(getContext(), "Видалено!", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return true;
                        }
                    });
                    menu.inflate(R.menu.favorite_product_popup_menu);
                    menu.show();
                }
            });
        }

        return listItemView;
    }

    private void intentOneProductActivity(Product currentProduct) {
        Intent intent = new Intent(getContext(), OneProductActivity.class);
        intent.putExtra("ProductId", currentProduct.getId());
        intent.putExtra("Title", currentProduct.getTitle());
        intent.putExtra("Category", currentProduct.getCategory());
        getContext().startActivity(intent);
    }

    private void intentEditProductActivity(Product currentProduct) {
        Intent intent = new Intent(getContext(), EditProductActivity.class);
        intent.putExtra("Title", currentProduct.getTitle());
        intent.putExtra("ProductId", currentProduct.getId());
        intent.putExtra("CategoryName", currentProduct.getCategory());
        intent.putExtra("Intent", mIntent);
        getContext().startActivity(intent);
    }
}
