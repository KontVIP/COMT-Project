package com.example.stylishclothes.catalog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.stylishclothes.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AddProductFragment extends Fragment {

    private ImageView image;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;
    EditText titleEditText, productCodeEditText, titleDescriptionEditText, descriptionEditText, priceEditText;
    FloatingActionButton fab_done;
    byte[] img_title, img_1, img_2, img_3, img_4, img_5;
    RadioButton rYes, rNo;
    CheckBox size_S, size_M, size_L, size_XL, size_XXL;
    int numImg;
    final int ADDED_SUCCESSFULLY = 1;
    String title;
    TextView categoryTextview;

    Product product;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference categoryReference;
    private StorageReference storageRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_category, container, false);
        getActivity().setTitle("Stylish Clothes");

        //get data from ProductListActivity
        Bundle bundle = this.getArguments();
        title = bundle.getString("Title");

        //Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Product");
        categoryReference = firebaseDatabase.getReference("Categories");
        storageRef = FirebaseStorage.getInstance().getReference("ImageDB");

        product = new Product();

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
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

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
                } else
                    Toast.makeText(getContext(), "Choose previous photo", Toast.LENGTH_SHORT).show();
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
                } else
                    Toast.makeText(getContext(), "Choose previous photo", Toast.LENGTH_SHORT).show();
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
                } else
                    Toast.makeText(getContext(), "Choose previous photo", Toast.LENGTH_SHORT).show();
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
                } else
                    Toast.makeText(getContext(), "Choose previous photo", Toast.LENGTH_SHORT).show();
            }
        });

        //Category TextView
        categoryTextview = rootView.findViewById(R.id.category_text_view);
        categoryTextview.setText(title);

        //Done button
        fab_done = (FloatingActionButton) rootView.findViewById(R.id.floating_done_button);
        fab_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addDataToDatabase() == ADDED_SUCCESSFULLY) {
                    Toast.makeText(getActivity(), "Data added!", Toast.LENGTH_SHORT).show();
                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
        });

        return rootView;
    }

    private int addDataToDatabase() {
        Long unixTime = System.currentTimeMillis();
        String id = databaseReference.child(unixTime.toString()).getKey();
        String title = titleEditText.getText().toString().trim();
        String titleDescription = titleDescriptionEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String price = priceEditText.getText().toString().trim();
        String code = productCodeEditText.getText().toString().trim();
        String category = categoryTextview.getText().toString().trim();

        product.setCategory(category);
        product.setAvailable(rYes.isChecked());
        product.setId(id);
        product.setTitle(title);
        product.setTitleDescription(titleDescription);
        product.setDescription(description);
        product.setPrice(price);
        product.setProductCode(code);
        product.setSizeS(size_S.isChecked());
        product.setSizeM(size_M.isChecked());
        product.setSizeL(size_L.isChecked());
        product.setSizeXL(size_XL.isChecked());
        product.setSizeXXL(size_XXL.isChecked());

        UploadTask uploadTask;
        Task<Uri> task;
        if (img_title != null) {
            final StorageReference mRef = storageRef.child(java.util.UUID.randomUUID() + "img");
            uploadTask = mRef.putBytes(img_title);
            task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return mRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    product.setTitleImagePath(task.getResult().toString());
                    firebaseDatabase.getReference("Categories/" + category + "/Products").child(id).setValue(product);
                }
            });
        }
        if (img_1 != null) {
            final StorageReference mRef = storageRef.child(java.util.UUID.randomUUID() + "img");
            uploadTask = mRef.putBytes(img_1);
            task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return mRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    product.setFirstImagePath(task.getResult().toString());
                    firebaseDatabase.getReference("Categories/" + category + "/Products").child(id).setValue(product);
                }
            });
        }
        if (img_2 != null) {
            final StorageReference mRef = storageRef.child(java.util.UUID.randomUUID() + "img");
            uploadTask = mRef.putBytes(img_2);
            task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return mRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    product.setSecondImagePath(task.getResult().toString());
                    firebaseDatabase.getReference("Categories/" + category + "/Products").child(id).setValue(product);
                }
            });
        }
        if (img_3 != null) {
            final StorageReference mRef = storageRef.child(java.util.UUID.randomUUID() + "img");
            uploadTask = mRef.putBytes(img_3);
            task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return mRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    product.setThirdImagePath(task.getResult().toString());
                    firebaseDatabase.getReference("Categories/" + category + "/Products").child(id).setValue(product);
                }
            });
        }
        if (img_4 != null) {
            final StorageReference mRef = storageRef.child(java.util.UUID.randomUUID() + "img");
            uploadTask = mRef.putBytes(img_4);
            task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return mRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    product.setFourthImagePath(task.getResult().toString());
                    firebaseDatabase.getReference("Categories/" + category + "/Products").child(id).setValue(product);
                }
            });
        }
        if (img_5 != null) {
            final StorageReference mRef = storageRef.child(java.util.UUID.randomUUID() + "img");
            uploadTask = mRef.putBytes(img_5);
            task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return mRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    product.setFifthImagePath(task.getResult().toString());
                    firebaseDatabase.getReference("Categories/" + category + "/Products").child(id).setValue(product);
                }
            });
        }
        return ADDED_SUCCESSFULLY;
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
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
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (numImg == 0) {
            while (bitmap.getByteCount() > 500000) {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
            }
        } else {
            while (bitmap.getByteCount() > 25000000) {
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / 1.1), (int) (bitmap.getHeight() / 1.1), false);
            }
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

}
