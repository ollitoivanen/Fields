package com.fields.curiumx.fields;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

import retrofit2.http.GET;

import static java.lang.String.valueOf;

public class DetailFieldActivity extends Activity {

    ImageButton imTrainingHereButton;
    ImageButton imTrainingHereNoMore;
    String venueName;
    String venueID;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference reference;
    String uid = user.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView amountOfPeople;
    ProgressBar progressBar;
    String distance;
    TextView distan;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String fieldID;
    String fieldName1;
    String fieldAddress;
    String fieldArea;
    String fieldType;
    String fieldAccessType;
    String goalCount;
    String creator;
    ImageView fieldPhoto;





    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_detail);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        imTrainingHereButton = findViewById(R.id.imTrainingHereButton);
        imTrainingHereNoMore = findViewById(R.id.imTrainingHereNoMoreButton);
        amountOfPeople = findViewById(R.id.amountOfPeople);
        progressBar = findViewById(R.id.progress_bar);
        fieldPhoto = findViewById(R.id.FieldImage);

        Bundle venue = getIntent().getExtras();
         fieldName1 = venue.getString("fieldName2");
        fieldAddress = venue.getString("fieldAddress");
        fieldAddress = venue.getString("fieldArea");
        fieldType = venue.getString("fieldType");
        fieldAccessType = venue.getString("fieldAccessType");
        goalCount = venue.getString("goalCount");
        creator =venue.getString("creator");
        fieldID = venue.getString("fieldID");

        StorageReference storageRef = storage.getReference().child("fieldpics/"+fieldID+".jpg");
        storageRef.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fieldPhoto.setImageDrawable(getResources().getDrawable(R.drawable.field_photo3));
            }
        });

        Glide.with(this)
                .load(storageRef)
                .into(fieldPhoto);


        setTitle(fieldName1);
        TextView fieldName = findViewById(R.id.fieldName);
        fieldName.setText(fieldName1);




        imTrainingHereNoMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressImHereNoMore();
            }
        });

        imTrainingHereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressImHere();
            }
        });

        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.get("currentFieldID").toString().equals(venueID)) {
                    imTrainingHereNoMore.setVisibility(View.VISIBLE);
                }else
                    imTrainingHereButton.setVisibility(View.VISIBLE);
            }
        });


        db.collection("Users").whereEqualTo("currentFieldID", venueID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String text0 = valueOf(task.getResult().size());
                String text = getResources().getString(R.string.people_here, text0);
                amountOfPeople.setText(text);
                amountOfPeople.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void onButtonPressImHere() {

        imTrainingHereButton.setVisibility(View.GONE);
        imTrainingHereNoMore.setVisibility(View.VISIBLE);
        db.collection("Users").document(uid).update("currentFieldID", venueID);
        db.collection("Users").document(uid).update("currentFieldName", venueName);
        db.collection("Users").document(uid).update("timestamp", FieldValue.serverTimestamp());
    }

    public void onButtonPressImHereNoMore(){


        imTrainingHereButton.setVisibility(View.VISIBLE);
        imTrainingHereNoMore.setVisibility(View.GONE);
        db.collection("Users").document(uid).update("currentFieldID", "");
        db.collection("Users").document(uid).update("currentFieldName", "");
        db.collection("Users").document(uid).update("timestamp", null);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}