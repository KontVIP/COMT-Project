package com.example.stylishclothes.catalog;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stylishclothes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProductActivity extends AppCompatActivity {

    String title, code, titleDescription, description, price, category;
    byte[] imageFromCatalog;
    DBHelper DB = new DBHelper(this);
    Context context = this;
    Uri imageUri;
    private static final int PICK_IMAGE = 1;
    Intent intent;
    ImageView image;
    EditText titleEditText, productCodeEditText, titleDescriptionEditText, descriptionEditText, priceEditText;
    FloatingActionButton fab_done;
    byte[] img_title, img_1, img_2, img_3, img_4, img_5;
    RadioButton rYes, rNo;
    CheckBox sizeCheckBox_S, sizeCheckBox_M, sizeCheckBox_L, sizeCheckBox_XL, sizeCheckBox_XXL;
    int numImg, availability, size_S, size_M, size_L, size_XL, size_XXL;
    TextView titleTextView;
    Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        
        //get data from ProductAdapter
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            title = arguments.getString("Title");
            imageFromCatalog = arguments.getByteArray("Image");
            intent = arguments.getParcelable("Intent");
        }

        //Load data
        Cursor result = DB.getProduct("SELECT * FROM Catalogs WHERE title = ?", title);
        if (result.getCount() == 0) {
            Toast.makeText(this, "No Catalogs Exist", Toast.LENGTH_SHORT).show();
        } else {
            result.moveToFirst();
            category = result.getString(0);
            img_title = result.getBlob(2);
            img_1 = result.getBlob(3);
            img_2 = result.getBlob(4);
            img_3 = result.getBlob(5);
            img_4 = result.getBlob(6);
            img_5 = result.getBlob(7);
            availability = result.getInt(8);
            code = result.getString(9);
            size_S = result.getInt(10);
            size_M = result.getInt(11);
            size_L = result.getInt(12);
            size_XL = result.getInt(13);
            size_XXL = result.getInt(14);
            titleDescription = result.getString(15);
            description = result.getString(16);
            price = result.getString(17);
        }

        //Title
        titleTextView = (TextView) findViewById(R.id.title_text_view);
        titleTextView.setText("Editing of " + title);
        titleEditText = (EditText) findViewById(R.id.title_edit_text);
        titleEditText.setText(title);

        //Images
        try {
            image = (ImageView) findViewById(R.id.title_image);
            bitmap = BitmapFactory.decodeByteArray(img_title, 0, img_title.length);
            image.setImageBitmap(bitmap);
            image = (ImageView) findViewById(R.id.first_image);
            bitmap = BitmapFactory.decodeByteArray(img_1, 0, img_1.length);
            image.setImageBitmap(bitmap);
            image = (ImageView) findViewById(R.id.second_image);
            bitmap = BitmapFactory.decodeByteArray(img_2, 0, img_2.length);
            image.setImageBitmap(bitmap);
            image = (ImageView) findViewById(R.id.third_image);
            bitmap = BitmapFactory.decodeByteArray(img_3, 0, img_3.length);
            image.setImageBitmap(bitmap);
            image = (ImageView) findViewById(R.id.fourth_image);
            bitmap = BitmapFactory.decodeByteArray(img_4, 0, img_4.length);
            image.setImageBitmap(bitmap);
            image = (ImageView) findViewById(R.id.fifth_image);
            bitmap = BitmapFactory.decodeByteArray(img_5, 0, img_5.length);
            image.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //Price
        priceEditText = (EditText) findViewById(R.id.price_edit_text);
        priceEditText.setText(price);

        //Check boxes
        sizeCheckBox_S = findViewById(R.id.size_S);
        if(size_S == 1) sizeCheckBox_S.setChecked(true);
        sizeCheckBox_M = findViewById(R.id.size_M);
        if(size_M == 1) sizeCheckBox_M.setChecked(true);
        sizeCheckBox_L = findViewById(R.id.size_L);
        if(size_L == 1) sizeCheckBox_L.setChecked(true);
        sizeCheckBox_XL = findViewById(R.id.size_XL);
        if(size_XL == 1) sizeCheckBox_XL.setChecked(true);
        sizeCheckBox_XXL = findViewById(R.id.size_XXL);
        if(size_XXL == 1) sizeCheckBox_XXL.setChecked(true);

        //Radio buttons
        rYes = findViewById(R.id.available_yes);
        if (availability == 1) rYes.setChecked(true);

        //Edit code
        productCodeEditText = (EditText) findViewById(R.id.product_code);
        productCodeEditText.setText(code);

        //Descriptions
        descriptionEditText = findViewById(R.id.description_edit_text);
        descriptionEditText.setText(description);
        titleDescriptionEditText = findViewById(R.id.title_description_edit_text);
        titleDescriptionEditText.setText(titleDescription);

        //Upload title image
        Button uploadTitlePhotoButton = (Button) findViewById(R.id.upload_title_image_button);
        uploadTitlePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numImg = 0;
                image = (ImageView) findViewById(R.id.title_image);
                imageIntent();
            }
        });

        //Upload first image
        Button uploadFirstPhotoButton = (Button) findViewById(R.id.upload_first_image_button);
        uploadFirstPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numImg = 1;
                image = (ImageView) findViewById(R.id.first_image);
                imageIntent();
            }
        });

        //Upload second image
        Button uploadSecondPhotoButton = (Button) findViewById(R.id.upload_second_image_button);
        uploadSecondPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_1 != null) {
                    numImg = 2;
                    image = (ImageView) findViewById(R.id.second_image);
                    imageIntent();
                } else Toast.makeText(context, "Choose previous photo", Toast.LENGTH_SHORT).show();
            }
        });

        //Upload third image
        Button uploadThirdPhotoButton = (Button) findViewById(R.id.upload_third_image_button);
        uploadThirdPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_2 != null) {
                    numImg = 3;
                    image = (ImageView) findViewById(R.id.third_image);
                    imageIntent();
                } else Toast.makeText(context, "Choose previous photo", Toast.LENGTH_SHORT).show();
            }
        });

        //Upload fourth image
        Button uploadFourthPhotoButton = (Button) findViewById(R.id.upload_fourth_image_button);
        uploadFourthPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_3 != null) {
                    numImg = 4;
                    image = (ImageView) findViewById(R.id.fourth_image);
                    imageIntent();
                } else Toast.makeText(context, "Choose previous photo", Toast.LENGTH_SHORT).show();
            }
        });

        //Upload fifth image
        Button uploadFifthPhotoButton = (Button) findViewById(R.id.upload_fifth_image_button);
        uploadFifthPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_4 != null) {
                    numImg = 5;
                    image = (ImageView) findViewById(R.id.fifth_image);
                    imageIntent();
                } else Toast.makeText(context, "Choose previous photo", Toast.LENGTH_SHORT).show();
            }
        });

        //Category spinner
        Spinner categorySpinner = findViewById(R.id.category_spinner);
        switch (category){
            case "Штани":
                categorySpinner.setSelection(0);
                break;
            case "Толстовки":
                categorySpinner.setSelection(1);
        }
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSpinnerItem = categorySpinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Done editing
        FloatingActionButton floatingDoneButton = (FloatingActionButton) findViewById(R.id.floating_done_button);
        floatingDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
                    if (titleEditText.getText().toString().length() == 0)
                        Toast.makeText(context, "Product Not Edited", Toast.LENGTH_SHORT).show();
                    else {
                        DB.updateCatalog(title,
                                categorySpinner.getSelectedItem().toString(),
                                titleEditText.getText().toString(),
                                img_title,
                                img_1,
                                img_2,
                                img_3,
                                img_4,
                                img_5,
                                rYes.isChecked(),
                                productCodeEditText.getText().toString(),
                                sizeCheckBox_S.isChecked(),
                                sizeCheckBox_M.isChecked(),
                                sizeCheckBox_L.isChecked(),
                                sizeCheckBox_XL.isChecked(),
                                sizeCheckBox_XXL.isChecked(),
                                titleDescriptionEditText.getText().toString(),
                                descriptionEditText.getText().toString(),
                                priceEditText.getText().toString());
                        Toast.makeText(context, "Product Edited", Toast.LENGTH_SHORT).show();
                    }
//                } catch (Exception e) {
//                    Toast.makeText(context, "Product Not Edited", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);
                image.setImageBitmap(bitmap);
                switch (numImg) {
                    case 0:
                        img_title = imageViewToByte(image);
                        break;
                    case 1:
                        img_1 = imageViewToByte(image);
                        break;
                    case 2:
                        img_2 = imageViewToByte(image);
                        break;
                    case 3:
                        img_3 = imageViewToByte(image);
                        break;
                    case 4:
                        img_4 = imageViewToByte(image);
                        break;
                    case 5:
                        img_5 = imageViewToByte(image);
                        break;
                }
                while (bitmap.getByteCount() > 1500000) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
                }
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void imageIntent() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (numImg == 0) {
            while (bitmap.getByteCount() > 1500000) {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
            }
        } else {
            while (bitmap.getByteCount() > 3000000) {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
            }
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

}
