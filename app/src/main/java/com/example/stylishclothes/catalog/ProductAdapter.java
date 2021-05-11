package com.example.stylishclothes.catalog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    DBHelper DB = new DBHelper(getContext());
    Intent mIntent;
    Bitmap bitmap;

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

        final Product currentProduct = getItem(position);
        //TextView
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);
        titleTextView.setText(currentProduct.getTitle());

        //ImageView
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.title_image_view);
        byte[] productImage = currentProduct.getImage();
        try {
            bitmap = BitmapFactory.decodeByteArray(productImage, 0, productImage.length);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.znak_voprosa);
        }



        //LinearLayout
        View listProductItem = (View) listItemView.findViewById(R.id.list_product_item);
        listProductItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitleOfOneProductActivity(currentProduct);
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
                                //TODO
                                Toast.makeText(getContext(), "Edited", Toast.LENGTH_SHORT).show();
                                intentEditProductActivity(currentProduct);
//                                AppCompatActivity activity = (AppCompatActivity) v.getContext();
//                                AddCategoryFragment fragment = new AddCategoryFragment();
//                                activity.getSupportFragmentManager().beginTransaction().replace(R.id.trousers_fragment_container, fragment).commit();
                                break;

                            case R.id.delete_item:
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setMessage("Are you sure you want to delete this product?");
                                builder.setCancelable(true);

                                builder.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                                DB.deleteCatalog(currentProduct.getTitle().toString());
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

    private void setTitleOfOneProductActivity(Product currentProduct) {
        Intent intent = new Intent(getContext(), OneProductActivity.class);
        intent.putExtra("Title", currentProduct.getTitle());
        getContext().startActivity(intent);
    }

    private void intentEditProductActivity(Product currentProduct) {
        Intent intent = new Intent(getContext(), EditProductActivity.class);
        intent.putExtra("Title", currentProduct.getTitle());
        intent.putExtra("Image", currentProduct.getImage());
        intent.putExtra("Intent", mIntent);
        getContext().startActivity(intent);
    }
}
