package com.example.pulink;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;

public class Utility {

    static String getUserid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    static CollectionReference getCollectionReferenceForName(){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("User").document(currentUser.getUid()).collection("Name");
    }
}
