package com.fields.curiumx.fields;


import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

public class DetailFieldActivity extends AppCompatActivity {

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
    String fieldName;
    String fieldAddress;
    String fieldArea;
    int fieldType;
    int fieldAccessType;
    int goalCount;
    ImageView fieldPhoto;
    TextView fieldLocation;
    LinearLayout area_map;
    TextView fieldTypeView;
    ImageView dropImage;
    ConstraintLayout container;

    String[] fieldTypeArray;
    String[] fieldAccessTypeArray;
    String[] fieldGoalCountArray;

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
        inflater.inflate(R.menu.edit_field, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_detail);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imTrainingHereButton = findViewById(R.id.imTrainingHereButton);
        imTrainingHereNoMore = findViewById(R.id.imTrainingHereNoMoreButton);
        amountOfPeople = findViewById(R.id.amountOfPeople);
        progressBar = findViewById(R.id.progress_bar);
        fieldPhoto = findViewById(R.id.FieldImage);
        fieldLocation = findViewById(R.id.field_area1);
        area_map = findViewById(R.id.area_map);
        fieldTypeView = findViewById(R.id.fieldType);
        dropImage = findViewById(R.id.dropdown);
        container = findViewById(R.id.gradient2);

        Bundle info = getIntent().getExtras();
        fieldName = info.getString("fieldName");
        fieldAddress = info.getString("fieldAddress");
        fieldArea = info.getString("fieldArea");
        fieldType = info.getInt("fieldType");
        fieldAccessType = info.getInt("fieldAccessType");
        goalCount = info.getInt("goalCount");
        fieldID = info.getString("fieldID");
        fieldLocation.setText(fieldArea);
        fieldTypeArray = getResources().getStringArray(R.array.field_type_array);
        fieldAccessTypeArray = getResources().getStringArray(R.array.field_access_type_array);
        fieldGoalCountArray = getResources().getStringArray(R.array.goal_count_array);
        String fieldTypeText = fieldTypeArray[fieldType];
        fieldTypeView.setText(getResources().getString(R.string.field_type_info, fieldTypeText));

        area_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setPackage("com.google.android.apps.maps");
                intent.setData(Uri.parse("https://www.google.com/maps/search/?api=1&query=" + fieldName));
                startActivity(intent);
            }
        });

        dropImage.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             final AlertDialog.Builder alertDialog = new AlertDialog
                                                     .Builder(DetailFieldActivity.this );
                                             View mView = getLayoutInflater().inflate(R.layout.field_detail_popup, null);
                                             TextView goalCountDrop = mView.findViewById(R.id.goalCountText);
                                             TextView accessDrop = mView.findViewById(R.id.access);
                                             TextView addressDrop = mView.findViewById(R.id.address);
                                             String fieldAccessTypeText = fieldAccessTypeArray[fieldAccessType];
                                             String fieldGoalCountText = fieldGoalCountArray[goalCount];
                                             goalCountDrop.setText(getResources().getString(R.string.goals, fieldGoalCountText));
                                             accessDrop.setText(getResources().getString(R.string.access_type, fieldAccessTypeText));
                                             addressDrop.setText(getResources().getString(R.string.address, fieldAddress));
                                             alertDialog.setView(mView);
                                             final AlertDialog dialog = alertDialog.create();
                                             dialog.show();
                                         }
                                     });



        final StorageReference storageRef = storage.getReference().child("fieldpics/"+fieldID+".jpg");
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    GlideApp.with(getApplicationContext())
                            .load(storageRef)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(fieldPhoto);
                }else {
                    fieldPhoto.setImageDrawable(getResources().getDrawable(R.drawable.field_photo3));
                }

            }
        });

        setTitle(fieldName);
        TextView fieldName = findViewById(R.id.fieldName);
        fieldName.setText(this.fieldName);

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
                container.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void onButtonPressImHere() {

        progressBar.setVisibility(View.VISIBLE);
        final Intent intent = new Intent(DetailFieldActivity.this, TrainingActivity.class);
        intent.putExtra("fieldName", fieldName);
        imTrainingHereButton.setVisibility(View.GONE);
        imTrainingHereNoMore.setEnabled(false);
        imTrainingHereNoMore.setVisibility(View.VISIBLE);
        db.collection("Users").document(uid).update("currentFieldID", fieldID);
        db.collection("Users").document(uid).update("currentFieldName", fieldName);
        db.collection("Users").document(uid).update("timestamp", FieldValue.serverTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                imTrainingHereNoMore.setEnabled(true);

            }
        });


    }

    public void onButtonPressImHereNoMore(){

        final Intent intent = new Intent(DetailFieldActivity.this, TrainingActivity.class);
        intent.putExtra("fieldName", fieldName);

        imTrainingHereButton.setVisibility(View.VISIBLE);
        imTrainingHereButton.setEnabled(false);
        imTrainingHereNoMore.setVisibility(View.GONE);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
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
                intent.putExtra("fieldName", fieldName);
                intent.putExtra("fieldID", fieldID);
                intent.putExtra("fieldType", fieldType);
                intent.putExtra("fieldAddress", fieldAddress);
                intent.putExtra("fieldArea", fieldArea);
                intent.putExtra("fieldAccessType", fieldAccessType);
                intent.putExtra("goalCount", goalCount);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }
}