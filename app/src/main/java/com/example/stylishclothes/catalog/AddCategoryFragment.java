package com.example.stylishclothes.catalog;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.stylishclothes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class AddCategoryFragment extends Fragment {

    DBHelper DB;
    private ImageView image;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_category, container, false);
        getActivity().setTitle("Stylish Clothes");
        EditText titleEditText = (EditText) rootView.findViewById(R.id.title_edit_text);
        FloatingActionButton fab_done = (FloatingActionButton) rootView.findViewById(R.id.floating_done_button);

        // This callback will only be called when AddCategoryFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.trousers_fragment_container);
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        //upload image
        image = (ImageView) rootView.findViewById(R.id.uploaded_image);

        Button uploadPhotoButton = (Button) rootView.findViewById(R.id.upload_photo_button);
        uploadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
            }
        });

        fab_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DB = new DBHelper(getContext());
                String categoryTXT = "Штани";
                String titleTXT = titleEditText.getText().toString();
                //Cursor result = DB.getCatalogs("SELECT title FROM Catalogs");

                try {
                    if (titleTXT.length() == 0)
                        Toast.makeText(getContext(), "New Catalog Not Created", Toast.LENGTH_SHORT).show();
                    else {
                        DB.createCatalog(categoryTXT, titleTXT, imageViewToByte(image));
                        Toast.makeText(getContext(), "New Catalog Created", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "New Catalog Not Created", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.trousers_fragment_container);
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        });


        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), imageUri);
                image.setImageBitmap(bitmap);
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

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

}
