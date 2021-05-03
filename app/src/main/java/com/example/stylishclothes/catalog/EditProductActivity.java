package com.example.stylishclothes.catalog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stylishclothes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProductActivity extends AppCompatActivity {

    String title;
    byte[] imageFromCatalog;
    DBHelper DB = new DBHelper(this);
    Context context = this;
    ImageView imageView;
    Uri imageUri;
    private static final int PICK_IMAGE = 1;
    Intent intent;

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

        TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
        titleTextView.setText("Editing of " + title);

        EditText titleEditText = (EditText) findViewById(R.id.title_edit_text);
        titleEditText.setText(title);

        imageView = (ImageView) findViewById(R.id.uploaded_image);
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageFromCatalog, 0, imageFromCatalog.length));



        //upload image
        Button uploadPhotoButton = (Button) findViewById(R.id.upload_photo_button);
        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
            }
        });

        //Done editing
        FloatingActionButton floatingDoneButton = (FloatingActionButton) findViewById(R.id.floating_done_button);
        floatingDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (titleEditText.getText().toString().length() == 0)
                        Toast.makeText(context, "Product Not Edited", Toast.LENGTH_SHORT).show();
                    else {
                        DB.updateCatalog(title, titleEditText.getText().toString(), imageViewToByte(imageView));
                        Toast.makeText(context, "Product Edited", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Product Not Edited", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

}
