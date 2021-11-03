package com.example.stylishclothes.catalog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.stylishclothes.InternetConnection;
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

import static android.app.Activity.RESULT_OK;

public class CatalogFragment extends Fragment implements View.OnClickListener {

    private ArrayList<Category> categories;
    private FloatingActionButton fab;
    private ListView listView;
    private EditText dialogEditText;
    private ImageView dialogImageView;
    private LayoutInflater dialogInflater;
    private View dialogLayout;
    private CategoryAdapter categoryAdapter;
    private ProgressBar checkInternetProgressBar;

    private static final int PICK_IMAGE = 1;
    private Uri imageUri;
    private byte[] image;

    private DatabaseReference databaseReference;
    private StorageReference storageRef;

    private Category category;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);
        getActivity().setTitle("Stylish Clothes");
        //setRetainInstance(true);

        init(rootView);


        //List
        categories = new ArrayList<Category>();
        listView.setDivider(null);

        try {
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Refresh Data
        final SwipeRefreshLayout pullToRefresh = rootView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    checkInternetConnection();
                    if (InternetConnection.checkConnection(getContext())) {
                        refreshData();
                    }
                    pullToRefresh.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        fab.setOnClickListener(this);
        dialogImageView.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_add_button:
                dialogEditText.setText("");
                dialogImageView.setImageResource(R.drawable.plus_image);
                image = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Створення категорії");
                builder.setMessage("Введіть назву та додайте титульне зображення категорії.");
                builder.setCancelable(true);

                if(dialogLayout.getParent()!=null) {
                    ((ViewGroup) dialogLayout.getParent()).removeView(dialogLayout); // <- fix
                }
                builder.setView(dialogLayout);

                builder.setPositiveButton(
                        "Створити",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                addDataToDatabase();
                            }
                        });

                builder.setNegativeButton(
                        "Скасувати",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.dialog_image_view:
                imageIntent();
                break;
        }
    }

    private void init(View rootView) {

        checkInternetProgressBar = getActivity().findViewById(R.id.check_internet_spin_kit);
        category = new Category();

        dialogInflater = getLayoutInflater();
        dialogLayout = dialogInflater.inflate(R.layout.dialog_create_category, null);
        dialogEditText = dialogLayout.findViewById(R.id.dialog_edit_text);
        dialogImageView = dialogLayout.findViewById(R.id.dialog_image_view);

        databaseReference = FirebaseDatabase.getInstance().getReference("Categories");
        storageRef = FirebaseStorage.getInstance().getReference("Categories");

        fab = (FloatingActionButton) rootView.findViewById(R.id.floating_add_button);
        listView = (ListView) rootView.findViewById(R.id.category_list);
    }

    public void loadData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    categories.add(category);
                }
                FragmentManager fragmentManager = getFragmentManager();
                try {
                    categoryAdapter = new CategoryAdapter(getActivity(), categories, fragmentManager);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listView.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void refreshData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    categories.add(category);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addDataToDatabase() {
        String title = dialogEditText.getText().toString().trim();
        category.title = title;

        if(title.equals("")) {
            dialogEditText.setError("Необхідна назва!");
            dialogLayout.requestFocus();
            return;
        }

        UploadTask uploadTask;
        Task<Uri> task;
        if (image != null) {
            final StorageReference mRef = storageRef.child(java.util.UUID.randomUUID() + "img");
            uploadTask = mRef.putBytes(image);
            task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return mRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    category.imagePath = task.getResult().toString();
                    databaseReference.child(dialogEditText.getText().toString().trim()).setValue(category);
                }
            });
        }
        Toast.makeText(getContext(), "Data added", Toast.LENGTH_SHORT).show();
    }

    private void checkInternetConnection() {
        try {
            checkInternetProgressBar.setVisibility(View.VISIBLE);
            if (InternetConnection.checkConnection(getContext())) {
                checkInternetProgressBar.setVisibility(View.GONE);
            } else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkInternetConnection();
                    }
                }, 2000);
            }
        } catch (Exception e) {
            checkInternetProgressBar.setVisibility(View.GONE);
            e.printStackTrace();
        }

    }

    private void imageIntent() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), imageUri);
                dialogImageView.setImageBitmap(bitmap);
                image = imageViewToByte(dialogImageView);
                while (bitmap.getByteCount() > 1000000) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
                }
                dialogImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        while (bitmap.getByteCount() > 1000000) {
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 2, bitmap.getHeight() / 2, false);
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

}
