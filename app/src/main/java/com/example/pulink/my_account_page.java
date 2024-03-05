package com.example.pulink;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class my_account_page extends Fragment {

    TextView nametext;
    ImageView user_image;
    public static final int GALLERY_REQUEST_CODE = 10; // Example value

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_account_page, container, false);
        nametext = view.findViewById(R.id.nametext);
        user_image = view.findViewById(R.id.user_image);

        String userId = Utility.getUserid();


        // Get a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotogallery();
//                updateimage();
            }
        });


        // Retrieve the name from Firestore
        db.collection("User").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            nametext.setText(name);

                            // Load user's image if it exists
                            String imageUrl = documentSnapshot.getString("image_url");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(imageUrl)
                                        .circleCrop()
                                        .into(user_image);
                            } else {
                                // If user doesn't have an image, you can set a default image here
//                                user_image.setImageResource(R.drawable.default_user_image);
                            }
                        } else {
                            // Document doesn't exist
                            nametext.setText("User");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to retrieve the name
                        nametext.setText("User");
                    }
                });

        return view;
    }


    public void gotogallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = Utility.getUserid();

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("user_images/" + userId + ".jpg");

            // Upload the image to Firebase Storage
            storageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully, get the download URL
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    // Update the user's profile in Firestore with the image URL
                                    db.collection("User").document(userId)
                                            .update("image_url", imageUrl)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Profile updated successfully
                                                    // You may want to display a message or update UI accordingly
                                                    // Retrieve the name and image URL from Firestore
                                                    db.collection("User").document(userId).get()
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    if (documentSnapshot.exists()) {

                                                                        // Load user's image if it exists
                                                                        String imageUrl = documentSnapshot.getString("image_url");
                                                                        if (imageUrl != null && !imageUrl.isEmpty()) {
                                                                            Glide.with(requireContext())
                                                                                    .load(imageUrl)
                                                                                    .circleCrop()
                                                                                    .into(user_image);
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Toast.makeText(getContext(),"Failed To Update Profile",Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Image upload failed
                        }
                    });
        }
    }


}
