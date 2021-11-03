package com.example.stylishclothes.catalog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stylishclothes.MainActivity;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProductActivity extends AppCompatActivity {

    String title, categoryName;
    Context context = this;
    Uri imageUri;
    private static final int PICK_IMAGE = 1;
    ImageView image;
    EditText titleEditText, productCodeEditText, titleDescriptionEditText, descriptionEditText, priceEditText;
    FloatingActionButton fab_done;
    byte[] img_title, img_1, img_2, img_3, img_4, img_5;
    RadioButton rYes, rNo;
    CheckBox sizeCheckBox_S, sizeCheckBox_M, sizeCheckBox_L, sizeCheckBox_XL, sizeCheckBox_XXL;
    int numImg;
    TextView titleTextView, categoryTextView;
    Button idButton;

    final int ADDED_SUCCESSFULLY = 1;
    final byte[] IMAGE_NOT_EXIST = "1".getBytes();

    Product product;
    String productId;
    DatabaseReference databaseReference;
    DatabaseReference categoryReference;
    private StorageReference storageRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        //get data from ProductAdapter
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            productId = arguments.getString("ProductId");
            title = arguments.getString("Title");
            categoryName = arguments.getString("CategoryName");
        }


        //firebase
        storageRef = FirebaseStorage.getInstance().getReference("ImageDB");
        databaseReference = FirebaseDatabase.getInstance().getReference("Categories/" + categoryName + "/Products");
        categoryReference = FirebaseDatabase.getInstance().getReference("Categories");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                product = snapshot.child(productId).getValue(Product.class);
                if (!product.DELETED) {
                    checkNumImg();
                    setImages(product.getTitleImagePath(), product.getFirstImagePath(), product.getSecondImagePath(), product.getThirdImagePath(), product.getFourthImagePath(), product.getFifthImagePath());
                    setCheckBoxes(product.isSize_S(), product.isSize_M(), product.isSize_L(), product.isSize_XL(), product.isSize_XXL());
                    setAvailable(product.isAvailable());
                    setPrice(product.getPrice());
                    setDescription(product.getTitleDescription(), product.getDescription());
                    setProductCode(product.getProductCode());
                } else {
                    startActivity(new Intent(context, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Title
        titleTextView = (TextView) findViewById(R.id.title_text_view);
        titleTextView.setText("Editing of " + title);
        titleEditText = (EditText) findViewById(R.id.title_edit_text);
        titleEditText.setText(title);


        //Price
        priceEditText = (EditText) findViewById(R.id.price_edit_text);

        //Check boxes
        sizeCheckBox_S = findViewById(R.id.size_S);
        sizeCheckBox_M = findViewById(R.id.size_M);
        sizeCheckBox_L = findViewById(R.id.size_L);
        sizeCheckBox_XL = findViewById(R.id.size_XL);
        sizeCheckBox_XXL = findViewById(R.id.size_XXL);

        //Radio buttons
        rYes = findViewById(R.id.available_yes);

        //Edit code
        productCodeEditText = (EditText) findViewById(R.id.product_code);

        //Descriptions
        descriptionEditText = findViewById(R.id.description_edit_text);
        titleDescriptionEditText = findViewById(R.id.title_description_edit_text);

        //Category TextView
        categoryTextView = findViewById(R.id.category_text_view);
        categoryTextView.setText(categoryName);

        //Id Button
        idButton = (Button) findViewById(R.id.id_button);
        idButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Product id", productId);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Скопійовано", Toast.LENGTH_SHORT).show();
            }
        });

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

        //Done editing
        FloatingActionButton floatingDoneButton = (FloatingActionButton) findViewById(R.id.floating_done_button);
        floatingDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addDataToDatabase() == ADDED_SUCCESSFULLY) {
                    finish();
                }

            }
        });
    }

    private void checkNumImg() {
        try {
            if (!(product.getFifthImagePath() == "")) {
                img_5 = IMAGE_NOT_EXIST;
                img_4 = IMAGE_NOT_EXIST;
                img_3 = IMAGE_NOT_EXIST;
                img_2 = IMAGE_NOT_EXIST;
                img_1 = IMAGE_NOT_EXIST;
            } else if (!(product.getFourthImagePath() == "")) {
                img_4 = IMAGE_NOT_EXIST;
                img_3 = IMAGE_NOT_EXIST;
                img_2 = IMAGE_NOT_EXIST;
                img_1 = IMAGE_NOT_EXIST;
            } else if (!(product.getThirdImagePath() == "")) {
                img_3 = IMAGE_NOT_EXIST;
                img_2 = IMAGE_NOT_EXIST;
                img_1 = IMAGE_NOT_EXIST;
            } else if (!(product.getSecondImagePath() == "")) {
                img_2 = IMAGE_NOT_EXIST;
                img_1 = IMAGE_NOT_EXIST;
            } else if (!(product.getFirstImagePath() == "")) {
                img_1 = IMAGE_NOT_EXIST;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int addDataToDatabase() {
        String title = titleEditText.getText().toString().trim();
        String titleDescription = titleDescriptionEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String price = priceEditText.getText().toString().trim();
        String code = productCodeEditText.getText().toString().trim();

        product.setAvailable(rYes.isChecked());
        product.setTitle(title);
        product.setTitleDescription(titleDescription);
        product.setDescription(description);
        product.setPrice(price);
        product.setProductCode(code);
        product.setSizeS(sizeCheckBox_S.isChecked());
        product.setSizeM(sizeCheckBox_M.isChecked());
        product.setSizeL(sizeCheckBox_L.isChecked());
        product.setSizeXL(sizeCheckBox_XL.isChecked());
        product.setSizeXXL(sizeCheckBox_XXL.isChecked());

        UploadTask uploadTask;
        Task<Uri> task;
        if (img_title != null) {
            try {
                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getTitleImagePath());
                storageRef.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final StorageReference mRef = FirebaseStorage.getInstance().getReference("ImageDB").child(java.util.UUID.randomUUID() + "_0_" + product.getTitle());
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
                    databaseReference.child(productId).setValue(product);
                }
            });
        }
        if (img_1 != null && img_1 != IMAGE_NOT_EXIST) {
            try {
                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getFirstImagePath());
                storageRef.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final StorageReference mRef = FirebaseStorage.getInstance().getReference("ImageDB").child(java.util.UUID.randomUUID() + "_1_" + product.getTitle());
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
                    databaseReference.child(productId).setValue(product);
                }
            });
        }
        if (img_2 != null && img_2 != IMAGE_NOT_EXIST) {
            try {
                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getSecondImagePath());
                storageRef.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final StorageReference mRef = FirebaseStorage.getInstance().getReference("ImageDB").child(java.util.UUID.randomUUID() + "_2_" + product.getTitle());
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
                    databaseReference.child(productId).setValue(product);
                }
            });
        }
        if (img_3 != null && img_3 != IMAGE_NOT_EXIST) {
            try {
                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getThirdImagePath());
                storageRef.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final StorageReference mRef = FirebaseStorage.getInstance().getReference("ImageDB").child(java.util.UUID.randomUUID() + "_3_" + product.getTitle());
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
                    databaseReference.child(productId).setValue(product);
                }
            });
        }
        if (img_4 != null && img_4 != IMAGE_NOT_EXIST) {
            try {
                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getFourthImagePath());
                storageRef.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final StorageReference mRef = FirebaseStorage.getInstance().getReference("ImageDB").child(java.util.UUID.randomUUID() + "_4_" + product.getTitle());
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
                    databaseReference.child(productId).setValue(product);
                }
            });
        }
        if (img_5 != null && img_5 != IMAGE_NOT_EXIST) {
            try {
                storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getFifthImagePath());
                storageRef.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final StorageReference mRef = FirebaseStorage.getInstance().getReference("ImageDB").child(java.util.UUID.randomUUID() + "_5_" + product.getTitle());
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
                    databaseReference.child(productId).setValue(product);
                }
            });
        }

        databaseReference.child(productId).setValue(product);
        return ADDED_SUCCESSFULLY;
    }


    private void setImages(String TitleImg, String FirstImg, String SecondImg, String ThirdImg, String FourthImg, String FifthImg) {
        if (TitleImg != null && !TitleImg.equals("")) {
            image = (ImageView) findViewById(R.id.title_image);
            Picasso.get().load(TitleImg).into(image);
        }
        if (FirstImg != null && !FirstImg.equals("")) {
            image = (ImageView) findViewById(R.id.first_image);
            Picasso.get().load(FirstImg).into(image);
        }
        if (SecondImg != null && !SecondImg.equals("")) {
            image = (ImageView) findViewById(R.id.second_image);
            Picasso.get().load(SecondImg).into(image);
        }
        if (ThirdImg != null && !ThirdImg.equals("")) {
            image = (ImageView) findViewById(R.id.third_image);
            Picasso.get().load(ThirdImg).into(image);
        }
        if (FourthImg != null && !FourthImg.equals("")) {
            image = (ImageView) findViewById(R.id.fourth_image);
            Picasso.get().load(FourthImg).into(image);
        }
        if (FifthImg != null && !FifthImg.equals("")) {
            image = (ImageView) findViewById(R.id.fifth_image);
            Picasso.get().load(FifthImg).into(image);
        }


    }

    private void setProductCode(String productCode) {
        productCodeEditText.setText(productCode);
    }

    private void setDescription(String titleDescription, String description) {
        titleDescriptionEditText.setText(titleDescription);
        descriptionEditText.setText(description);
    }

    private void setPrice(String price) {
        priceEditText.setText(price);
    }

    private void setAvailable(boolean available) {
        if (available) {
            rYes.setChecked(true);
        }
    }

    private void setCheckBoxes(boolean isCheckedS, boolean isCheckedM, boolean isCheckedL, boolean isCheckedXL, boolean isCheckedXXL) {
        if (isCheckedS) {
            sizeCheckBox_S.setChecked(true);
        }
        if (isCheckedM) {
            sizeCheckBox_M.setChecked(true);
        }
        if (isCheckedL) {
            sizeCheckBox_L.setChecked(true);
        }
        if (isCheckedXL) {
            sizeCheckBox_XL.setChecked(true);
        }
        if (isCheckedXXL) {
            sizeCheckBox_XXL.setChecked(true);
        }
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), imageUri);
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
