package com.example.stylishclothes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.stylishclothes.auth.AuthActivity;
import com.example.stylishclothes.auth.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class MenuFragment extends Fragment implements View.OnClickListener {

    FirebaseUser user;
    DatabaseReference reference;
    private TextView signOutTextView, fullNameTextView, emailTextView;
    private String userID;
    private FirebaseAuth firebaseAuth;
    private ImageView avatarImageView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Stylish Clothes");
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();


        avatarImageView = (ImageView) rootView.findViewById(R.id.avatar_image_view);

        signOutTextView = (TextView) rootView.findViewById(R.id.sign_out_text_view);
        signOutTextView.setOnClickListener(this);

        fullNameTextView = (TextView) rootView.findViewById(R.id.full_name_text_view);
        fullNameTextView.setOnClickListener(this);

        emailTextView = (TextView) rootView.findViewById(R.id.email_text_view);
        emailTextView.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);


                String fullName = userProfile.fullName;
                String email = userProfile.email;
                fullNameTextView.setText(fullName);
                emailTextView.setText(email);
                try {
                    Picasso.get().load(userProfile.profilePhotoPath).into(avatarImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Что-то пошло не так!", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_out_text_view:
                GoogleSignIn.getClient(getContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                        .signOut();
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), AuthActivity.class));
                break;
            case R.id.full_name_text_view:
                break;
            case R.id.email_text_view:
                break;
        }
    }
}
