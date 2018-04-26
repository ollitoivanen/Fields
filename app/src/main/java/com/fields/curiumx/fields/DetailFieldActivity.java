package com.fields.curiumx.fields;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static java.lang.String.valueOf;

public class DetailFieldActivity extends Activity {



    Button imTrainingHereButton;
    Button imTrainingHereNoMore;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference reference;
    String uid = user.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView amountOfPeople;
    ProgressBar progressBar;
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

    LayoutInflater layoutInflater;
    ConstraintLayout detail_field_activity;
    PopupWindow popupWindow;


    @Override
    protected void onRestart() {
        super.onRestart();
        imTrainingHereNoMore.setEnabled(false);
        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                imTrainingHereNoMore.setEnabled(true);
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.get("currentFieldID").toString().equals(fieldID)) {
                    imTrainingHereNoMore.setVisibility(View.VISIBLE);
                    imTrainingHereButton.setVisibility(View.GONE);
                } else {
                    imTrainingHereButton.setVisibility(View.VISIBLE);
                    imTrainingHereNoMore.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add, menu);
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
        fieldName1 = venue.getString("fieldName");
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
                    imTrainingHereButton.setVisibility(View.GONE);
                } else {
                    imTrainingHereButton.setVisibility(View.VISIBLE);
                    imTrainingHereNoMore.setVisibility(View.GONE);
                }
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

        progressBar.setVisibility(View.VISIBLE);
        final Intent intent = new Intent(DetailFieldActivity.this, TrainingActivity.class);
        intent.putExtra("fieldName", fieldName1);
        imTrainingHereButton.setVisibility(View.GONE);
        imTrainingHereNoMore.setEnabled(false);
        imTrainingHereNoMore.setVisibility(View.VISIBLE);
        db.collection("Users").document(uid).update("currentFieldID", fieldID);
        db.collection("Users").document(uid).update("currentFieldName", fieldName1);
        db.collection("Users").document(uid).update("timestamp", FieldValue.serverTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                startActivity(intent);
                imTrainingHereNoMore.setEnabled(true);

            }
        });


    }

    public void onButtonPressImHereNoMore(){

        final Intent intent = new Intent(DetailFieldActivity.this, TrainingActivity.class);
        intent.putExtra("fieldName", fieldName1);

        imTrainingHereButton.setVisibility(View.VISIBLE);
        imTrainingHereButton.setEnabled(false);
        imTrainingHereNoMore.setVisibility(View.GONE);
       startActivity(intent);
       imTrainingHereButton.setEnabled(true);


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
            case R.id.add2:
                layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                detail_field_activity = findViewById(R.id.detail_field_actvity);
                final ConstraintLayout popUp2 = findViewById(R.id.add_popup2);
                final ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.add_popup, popUp2);
                popupWindow = new PopupWindow(container, 1000, 220, false);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                popupWindow.setOutsideTouchable(true);
                popupWindow.setElevation(100);
                popupWindow.showAtLocation(detail_field_activity, Gravity.CENTER, 0,0 );
                final Button add_button = container.findViewById(R.id.set_homefield_button);
                db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        final DocumentSnapshot ds = task.getResult();
                        if (ds.get("usersTeamID") == null){
                            add_button.setEnabled(false);
                        }else {
                            add_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String usersTeam = ds.get("usersTeamID").toString();
                                    db.collection("Teams").document(usersTeam).update("homeField", fieldName1);
                                }
                            });
                        }

                    }
                });

                return true;



        }
        return super.onOptionsItemSelected(item);
    }


}