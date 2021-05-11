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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
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
    EditText titleEditText, productCodeEditText, titleDescriptionEditText, descriptionEditText, priceEditText;
    FloatingActionButton fab_done;
    byte[] img_title, img_1, img_2, img_3, img_4, img_5;
    RadioButton rYes, rNo;
    CheckBox size_S, size_M, size_L, size_XL, size_XXL;
    int numImg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_category, container, false);
        getActivity().setTitle("Stylish Clothes");

        //Edit text
        titleEditText = (EditText) rootView.findViewById(R.id.title_edit_text);
        titleDescriptionEditText = (EditText) rootView.findViewById(R.id.title_description_edit_text);
        descriptionEditText = (EditText) rootView.findViewById(R.id.description_edit_text);
        priceEditText = (EditText) rootView.findViewById(R.id.price_edit_text);

        //Edit code
        productCodeEditText = rootView.findViewById(R.id.product_code);

        //Checkboxes
        size_S = rootView.findViewById(R.id.size_S);
        size_M = rootView.findViewById(R.id.size_M);
        size_L = rootView.findViewById(R.id.size_L);
        size_XL = rootView.findViewById(R.id.size_XL);
        size_XXL = rootView.findViewById(R.id.size_XXL);

        //Radio buttons
        rYes = rootView.findViewById(R.id.available_yes);

        //"Back action"
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.trousers_fragment_container);
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        //Upload title image
        Button uploadTitlePhotoButton = (Button) rootView.findViewById(R.id.upload_title_image_button);
        uploadTitlePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numImg = 0;
                image = (ImageView) rootView.findViewById(R.id.title_image);
                imageIntent();
            }
        });

        //Upload first image
        Button uploadFirstPhotoButton = (Button) rootView.findViewById(R.id.upload_first_image_button);
        uploadFirstPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numImg = 1;
                image = (ImageView) rootView.findViewById(R.id.first_image);
                imageIntent();
            }
        });

        //Upload second image
        Button uploadSecondPhotoButton = (Button) rootView.findViewById(R.id.upload_second_image_button);
        uploadSecondPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_1 != null) {
                    numImg = 2;
                    image = (ImageView) rootView.findViewById(R.id.second_image);
                    imageIntent();
                } else Toast.makeText(getContext(), "Choose previous photo", Toast.LENGTH_SHORT).show();
            }
        });

        //Upload third image
        Button uploadThirdPhotoButton = (Button) rootView.findViewById(R.id.upload_third_image_button);
        uploadThirdPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_2 != null) {
                    numImg = 3;
                    image = (ImageView) rootView.findViewById(R.id.third_image);
                    imageIntent();
                } else Toast.makeText(getContext(), "Choose previous photo", Toast.LENGTH_SHORT).show();
            }
        });

        //Upload fourth image
        Button uploadFourthPhotoButton = (Button) rootView.findViewById(R.id.upload_fourth_image_button);
        uploadFourthPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_3 != null) {
                    numImg = 4;
                    image = (ImageView) rootView.findViewById(R.id.fourth_image);
                    imageIntent();
                } else Toast.makeText(getContext(), "Choose previous photo", Toast.LENGTH_SHORT).show();
            }
        });

        //Upload fifth image
        Button uploadFifthPhotoButton = (Button) rootView.findViewById(R.id.upload_fifth_image_button);
        uploadFifthPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_4 != null) {
                    numImg = 5;
                    image = (ImageView) rootView.findViewById(R.id.fifth_image);
                    imageIntent();
                } else Toast.makeText(getContext(), "Choose previous photo", Toast.LENGTH_SHORT).show();
            }
        });

        //Category spinner
        Spinner categorySpinner = rootView.findViewById(R.id.category_spinner);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Done button
        fab_done = (FloatingActionButton) rootView.findViewById(R.id.floating_done_button);
        fab_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DB = new DBHelper(getContext());
                try {
                    if (titleEditText.getText().toString().length() == 0)
                        Toast.makeText(getContext(), "New Catalog NOT Created", Toast.LENGTH_SHORT).show();
                    else {
                        DB.createCatalog(categorySpinner.getSelectedItem().toString(),
                                titleEditText.getText().toString(),
                                img_title,
                                img_1,
                                img_2,
                                img_3,
                                img_4,
                                img_5,
                                rYes.isChecked(),
                                productCodeEditText.getText().toString(),
                                size_S.isChecked(),
                                size_M.isChecked(),
                                size_L.isChecked(),
                                size_XL.isChecked(),
                                size_XXL.isChecked(),
                                titleDescriptionEditText.getText().toString(),
                                descriptionEditText.getText().toString(),
                                priceEditText.getText().toString());
                        Toast.makeText(getContext(), "New Catalog Created", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "New Catalog NOT Created", Toast.LENGTH_SHORT).show();
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
                while (bitmap.getByteCount() > 500000) {
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
            while (bitmap.getByteCount() > 500000) {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
            }
        } else {
            while (bitmap.getByteCount() > 1500000) {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
            }
        }
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
