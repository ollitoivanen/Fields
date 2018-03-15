package com.fields.curiumx.fields;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DetailFieldActivity extends Activity {

    public final String PEOPLE_HERE_KEY = "People Here";
    public final String FIELD_NAME = "Field Name";
    public final String CURRENT_FIELD = "Current Field";
    public final String UID = "Uid";
    public final String PEOPLE_HERE_BY_UID = "People here by uid";
    ImageButton imTrainingHereButton;
    Button imTrainingHereNoMore;
    String venueName;
    String venueID;
    CollectionReference peopleHereColRef;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference reference;
    String uid = user.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView amountOfPeople;
    int size;




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_detail);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        imTrainingHereButton = findViewById(R.id.imTrainingHereButton);
        imTrainingHereNoMore = findViewById(R.id.imTrainingHereNoMoreButton);
        imTrainingHereNoMore.setVisibility(View.GONE);

        amountOfPeople = findViewById(R.id.amountOfPeople);

        Bundle venue = getIntent().getExtras();
        venueName = venue.getString("name");
        venueID = venue.getString("ID");
        setTitle(venueName);
        TextView fieldName = findViewById(R.id.fieldName);
        fieldName.setText(venueName);
        peopleHereColRef = db.collection("Fields").document(venueID).collection("People Here");
        reference = db.collection("Fields").document(venueID);




        peopleHereColRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                 @Override
                                                 public void onEvent(QuerySnapshot value, FirebaseFirestoreException e) {
                                                      Log.d(TAG, "gg" + value.size());
                                                      amountOfPeople.setText(String.valueOf(value.size()));

                                                 }
                                             });



        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.getResult().exists()) {
                    Map<String, Object> initialData = new HashMap<>();
                    initialData.put(FIELD_NAME, venueName);
                    reference.set(initialData);


                }
            }
        });

        peopleHereColRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    imTrainingHereNoMore.setVisibility(View.VISIBLE);
                } else {
                    imTrainingHereNoMore.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        imTrainingHereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressImHere();
            }
        });

        imTrainingHereNoMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressImHereNoMore();
            }
        });
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

    public void onButtonPressImHere() {

peopleHereColRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (!task.getResult().exists()){
            Map<String, Object> data = new HashMap<>();
            data.put("testing", 0);
            data.put("name", user.getDisplayName());
            peopleHereColRef.document(uid).set(data);
            imTrainingHereNoMore.setVisibility(View.VISIBLE);
        }else {
            Toast.makeText(getApplicationContext(), "You are already here", Toast.LENGTH_SHORT).show();
        }
    }
});
    }

    public void onButtonPressImHereNoMore(){
        peopleHereColRef.document(uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "You are not training here anymore", Toast.LENGTH_SHORT).show();
                imTrainingHereNoMore.setVisibility(View.GONE);
            }
        });

    }
    }



