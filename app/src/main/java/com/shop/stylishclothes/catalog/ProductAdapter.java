package com.shop.stylishclothes.catalog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shop.stylishclothes.CheckAdmin;
import com.shop.stylishclothes.MainActivity;
import com.shop.stylishclothes.R;
import com.shop.stylishclothes.shoppingcart.ShoppingCartFragment;
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

    private DatabaseReference databaseReference;
    private StorageReference storageRef;
    StorageReference photoRef;
    int productType;
    ShoppingCartFragment shoppingCartFragment;
    String offerType;
    String offerId;

    final int DEFAULT_PRODUCT = 0;
    final int FAVORITE_PRODUCT = 1;
    final int SHOPPING_CART_PRODUCT = 2;
    final int CHECKOUT_PRODUCT = 3;
    final int ADMIN_PANEL_PRODUCT = 4;


    public ProductAdapter(Activity activityContext, ArrayList<Product> products, int productType) {
        super(activityContext, 0, products);
        this.productType = productType;
    }

    public ProductAdapter(Activity activityContext, ArrayList<Product> products, int productType, String offerType, String offerId) {
        super(activityContext, 0, products);
        this.productType = productType;
        this.offerType = offerType;
        this.offerId = offerId;
    }

    public ProductAdapter(Activity activityContext, ArrayList<Product> products, int productType, ShoppingCartFragment shoppingCartFragment) {
        super(activityContext, 0, products);
        this.productType = productType;
        this.shoppingCartFragment = shoppingCartFragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null && productType == DEFAULT_PRODUCT) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_product_item,
                    parent, false);
        } else if (listItemView == null && productType == FAVORITE_PRODUCT) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_product_item,
                    parent, false);
        } else if (listItemView == null && productType == SHOPPING_CART_PRODUCT) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_shopping_cart_item,
                    parent, false);
        } else if (listItemView == null && productType == CHECKOUT_PRODUCT) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_checkout_product_item,
                    parent, false);
        } else if (listItemView == null && productType == ADMIN_PANEL_PRODUCT) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_checkout_product_item, //it is correct to use checkout_product_item
                    parent, false);
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
        priceTextView.setText("???" + currentProduct.getPrice());

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
        try {
            if (CheckAdmin.Companion.isAdmin(FirebaseAuth.getInstance().getCurrentUser())) {
                showMenu.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (productType == DEFAULT_PRODUCT) {
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
                                case R.id.delete_list_item:
                                    FirebaseDatabase.getInstance()
                                            .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Favorites/" + productId).removeValue();
                                    Toast.makeText(getContext(), "????????????????!", Toast.LENGTH_SHORT).show();
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
            //quantity buttons
            ImageButton minusButton = listItemView.findViewById(R.id.minus_image_button);
            ImageButton plusButton = listItemView.findViewById(R.id.plus_image_button);
            TextView quantityTextView = listItemView.findViewById(R.id.quantity_text_view);

            //size textView
            TextView sizeTextView = listItemView.findViewById(R.id.size_text_view);

            //Firebase load quantity
            DatabaseReference shoppingCartReference = FirebaseDatabase.getInstance()
                    .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/");
            shoppingCartReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                 for (DataSnapshot dataSnapshot : snapshot.getChildren())    {
                     shoppingCartReference.child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                             if (snapshot.child("id").getValue(String.class) != null) {
                                 if (snapshot.child("id").getValue(String.class).equals(productId) && snapshot.child("size").getValue(String.class).equals(currentProduct.currentSize)) {
                                     sizeTextView.setText(snapshot.child("size").getValue(String.class));

                                     int quantity = snapshot.child("quantity").getValue(Integer.class);
                                     quantityTextView.setText(String.valueOf(quantity));
                                     if (quantity < 99) {
                                         plusButton.setImageResource(R.drawable.plus);
                                     } else {
                                         plusButton.setImageResource(R.drawable.plus_grey);
                                     }

                                     if (quantity > 1) {
                                         minusButton.setImageResource(R.drawable.minus_green);
                                     } else {
                                         minusButton.setImageResource(R.drawable.minus_grey);
                                     }
                                 }
                             }
                         }
                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {

                         }
                     });
                 }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });


            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Integer.valueOf(quantityTextView.getText().toString()) < 99) {
                        quantityTextView.setText(String.valueOf(Integer.valueOf(quantityTextView.getText().toString()) + 1));

                        FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                  if (dataSnapshot.child("id").getValue(String.class).equals(productId) && dataSnapshot.child("size").getValue(String.class).equals(currentProduct.currentSize)) {

                                      FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/" + dataSnapshot.getKey() + "/")
                                              .child("quantity").setValue(Integer.valueOf(quantityTextView.getText().toString().trim()));
                                  }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        if (Integer.valueOf(quantityTextView.getText().toString()) > 1) {
                            minusButton.setImageResource(R.drawable.minus_green);
                        }
                        if ( Integer.valueOf(quantityTextView.getText().toString()) == 99) {
                            plusButton.setImageResource(R.drawable.plus_grey);
                        }
                        quantityTextView.setText(String.valueOf(Integer.valueOf(quantityTextView.getText().toString())));

                        shoppingCartFragment.updateCheckoutFab(Integer.valueOf(currentProduct.getPrice()));
                    }
                }
            });

            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.valueOf(quantityTextView.getText().toString()) > 1) {
                        quantityTextView.setText(String.valueOf(Integer.valueOf(quantityTextView.getText().toString()) - 1));

                        FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            if (dataSnapshot.child("id").getValue(String.class).equals(productId) && dataSnapshot.child("size").getValue(String.class).equals(currentProduct.currentSize)) {

                                                FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/" + dataSnapshot.getKey() + "/")
                                                        .child("quantity").setValue(Integer.valueOf(quantityTextView.getText().toString().trim()));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        if (Integer.valueOf(quantityTextView.getText().toString()) == 1) {
                            minusButton.setImageResource(R.drawable.minus_grey);
                        }
                        if (Integer.valueOf(quantityTextView.getText().toString()) < 99) {
                            plusButton.setImageResource(R.drawable.plus);
                        }
                        quantityTextView.setText(String.valueOf(Integer.valueOf(quantityTextView.getText().toString())));

                        shoppingCartFragment.updateCheckoutFab(Integer.valueOf("-" + currentProduct.getPrice()));
                    }
                }
            });

            //show menu
            showMenu.setVisibility(View.VISIBLE);
            showMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(getContext(), showMenu);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete_list_item:

                                    FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        if (dataSnapshot.child("id").getValue(String.class).equals(productId) && dataSnapshot.child("size").getValue(String.class).equals(currentProduct.currentSize)) {

                                                            FirebaseDatabase.getInstance().getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/" + dataSnapshot.getKey() + "/")
                                                                    .removeValue();
                                                            Toast.makeText(getContext(), "????????????????!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getContext(), MainActivity.class);
                                                            intent.putExtra("Fragment", R.id.nav_shopping_cart);
                                                            getContext().startActivity(intent);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                    break;
                            }
                            return true;
                        }
                    });
                    menu.inflate(R.menu.favorite_product_popup_menu);
                    menu.show();
                }
            });
        } else if (productType == CHECKOUT_PRODUCT) {
            TextView quantityTextView = listItemView.findViewById(R.id.quantity_text_view);
            TextView sizeTextView = listItemView.findViewById(R.id.size_text_view);

            //Firebase load quantity and size
            DatabaseReference shoppingCartReference = FirebaseDatabase.getInstance()
                    .getReference("Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Shopping Cart/");
            shoppingCartReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())    {
                        shoppingCartReference.child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child("id").getValue(String.class) != null) {
                                    if (snapshot.child("id").getValue(String.class).equals(productId) && snapshot.child("size").getValue(String.class).equals(currentProduct.currentSize)) {
                                        sizeTextView.setText(snapshot.child("size").getValue(String.class));
                                        int quantity = snapshot.child("quantity").getValue(Integer.class);
                                        quantityTextView.setText(String.valueOf(quantity));
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else if (productType == ADMIN_PANEL_PRODUCT) {

            TextView quantityTextView = listItemView.findViewById(R.id.quantity_text_view);
            TextView sizeTextView = listItemView.findViewById(R.id.size_text_view);

            //Firebase load quantity and size
            DatabaseReference shoppingCartReference = FirebaseDatabase.getInstance().getReference("Offers/" + offerType);
            shoppingCartReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String offerIdFromSnapshot = dataSnapshot.child("offerId").getValue(String.class);
                        for (DataSnapshot productSnapshot : dataSnapshot.child("listOfOfferProduct").getChildren()) {
                            if (
                                    productSnapshot.child("id").getValue(String.class).equals(productId)
                                    && productSnapshot.child("size").getValue(String.class).equals(currentProduct.currentSize)
                                    && offerIdFromSnapshot.equals(offerId)
                            ) {
                                sizeTextView.setText(productSnapshot.child("size").getValue(String.class));
                                quantityTextView.setText(productSnapshot.child("quantity").getValue(Integer.class).toString());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        return listItemView;
    }

    private void intentOneProductActivity(Product currentProduct) {
        Intent intent = new Intent(getContext(), OneProductActivity.class);
        intent.putExtra("ProductId", currentProduct.getId());
        getContext().startActivity(intent);
    }

    private void intentEditProductActivity(Product currentProduct) {
        Intent intent = new Intent(getContext(), EditProductActivity.class);
        intent.putExtra("Title", currentProduct.getTitle());
        intent.putExtra("ProductId", currentProduct.getId());
        intent.putExtra("CategoryName", currentProduct.getCategory());
        getContext().startActivity(intent);
    }
}
