package com.fields.curiumx.fields;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.concurrent.atomic.AtomicReferenceArray;

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
    String creatorName;
    ImageView fieldPhoto;
    TextView fieldLocation;
    LinearLayout area_map;
    TextView fieldTypeText;
    LinearLayout drop;
    TextView goalCountDrop;
    TextView accessDrop;
    TextView addressDrop;
    TextView creatorDrop;
    ImageView dropImage;
    Boolean down;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_field, menu);
        return super.onCreateOptionsMenu(menu);
    }




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
        fieldLocation = findViewById(R.id.field_area1);
        area_map = findViewById(R.id.area_map);
        fieldTypeText = findViewById(R.id.fieldType);
        goalCountDrop = findViewById(R.id.goalCountText);
        accessDrop = findViewById(R.id.access);
        addressDrop = findViewById(R.id.address);
        creatorDrop = findViewById(R.id.creator);
        drop = findViewById(R.id.drop);
        dropImage = findViewById(R.id.dropdown);
        down = false;




        Bundle venue = getIntent().getExtras();
        fieldName1 = venue.getString("fieldName2");
        fieldAddress = venue.getString("fieldAddress");
        fieldArea = venue.getString("fieldArea");
        fieldType = venue.getString("fieldType");
        fieldAccessType = venue.getString("fieldAccessType");
        goalCount = venue.getString("goalCount");
        creator =venue.getString("creator");
        fieldID = venue.getString("fieldID");
        creatorName = venue.getString("creatorName");
        fieldLocation.setText(fieldArea);
        fieldTypeText.setText(fieldType);

        goalCountDrop.setText("Goals:" + " " + goalCount);
        accessDrop.setText("Access type:" + " " + fieldAccessType);
        addressDrop.setText("Address:" + " " + fieldAddress);
        creatorDrop.setText("Creator:" + " " + creatorName);

        area_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setPackage("com.google.android.apps.maps");
                intent.setData(Uri.parse("https://www.google.com/maps/search/?api=1&query=" + fieldName1));
                startActivity(intent);
            }
        });

        dropImage.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             if (!down) {
                                                 down = true;
                                                 drop.setVisibility(View.VISIBLE);
                                                 drop.animate().translationY(drop.getHeight()).setListener(new AnimatorListenerAdapter() {
                                                     @Override
                                                     public void onAnimationEnd(Animator animation) {

                                                         goalCountDrop.setVisibility(View.VISIBLE);
                                                         goalCountDrop.setAlpha(0.0f);
                                                         goalCountDrop.animate().alpha(1.0f);

                                                         accessDrop.setVisibility(View.VISIBLE);
                                                         accessDrop.setAlpha(0.0f);
                                                         accessDrop.animate().alpha(1.0f);

                                                         addressDrop.setVisibility(View.VISIBLE);
                                                         addressDrop.setAlpha(0.0f);
                                                         addressDrop.animate().alpha(1.0f);

                                                         creatorDrop.setVisibility(View.VISIBLE);
                                                         creatorDrop.setAlpha(0.0f);
                                                         creatorDrop.animate().alpha(1.0f);
                                                         super.onAnimationEnd(animation);
                                                     }
                                                 });
                                             } else {
                                                 down = false;

                                                 drop.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
                                                     @Override
                                                     public void onAnimationEnd(Animator animation) {

                                                         goalCountDrop.setVisibility(View.GONE);


                                                         accessDrop.setVisibility(View.GONE);


                                                         addressDrop.setVisibility(View.GONE);


                                                         creatorDrop.setVisibility(View.GONE);

                                                         drop.setVisibility(View.INVISIBLE);

                                                         super.onAnimationEnd(animation);

                                                     }
                                                 });

                                             }
                                         }
                                     });


        final StorageReference storageRef = storage.getReference().child("fieldpics/"+fieldID+".jpg");
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Glide.with(getApplicationContext())
                            .load(storageRef)
                            .into(fieldPhoto);
                }else {
                    fieldPhoto.setImageDrawable(getResources().getDrawable(R.drawable.field_photo3));
                }

            }
        });




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
                if (documentSnapshot.get("currentFieldID").toString().equals(fieldID)) {
                    imTrainingHereNoMore.setVisibility(View.VISIBLE);
                }else
                    imTrainingHereButton.setVisibility(View.VISIBLE);
            }
        });


        db.collection("Users").whereEqualTo("currentFieldID", fieldID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        db.collection("Users").document(uid).update("currentFieldID", fieldID);
        db.collection("Users").document(uid).update("currentFieldName", fieldName1);
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
            case R.id.edit_field:
                Intent intent = new Intent(DetailFieldActivity.this, EditFieldActivity.class);
                intent.putExtra("fieldName", fieldName1);
                intent.putExtra("fieldID", fieldID);
                intent.putExtra("fieldType", fieldType);
                intent.putExtra("fieldAddress", fieldAddress);
                intent.putExtra("fieldArea", fieldArea);
                intent.putExtra("fieldAccessType", fieldAccessType);
                intent.putExtra("goalCount", goalCount);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}