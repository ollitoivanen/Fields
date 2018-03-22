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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.DateTimeUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
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

        amountOfPeople = findViewById(R.id.amountOfPeople);

        Bundle venue = getIntent().getExtras();
        venueName = venue.getString("name");
        venueID = venue.getString("ID");
        setTitle(venueName);
        TextView fieldName = findViewById(R.id.fieldName);
        fieldName.setText(venueName);




        db.collection("Users").whereEqualTo("Current Field ID", venueID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                amountOfPeople.setText(String.valueOf(task.getResult().size()));
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
    }

    public void onButtonPressImHere() {

        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                    if (!documentSnapshot.get("Current Field ID").toString().equals(venueID)) {
                        //It WORKS!!!!!!


                        db.collection("Users").document(uid).update("Current Field ID", venueID);
                        db.collection("Users").document(uid).update("Current Field name", venueName);
                        db.collection("Users").document(uid).update("timestamp", FieldValue.serverTimestamp());


                    } else {
                        Toast.makeText(getApplicationContext(), "You are already at this field. Check out first.", Toast.LENGTH_LONG).show();
                    }
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
}