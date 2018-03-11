package com.fields.curiumx.fields;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DetailFieldActivity extends Activity{

    public static final String PEOPLE_HERE_KEY = "People Here";
    public static final String FIELD_NAME = "Field Name";
    public static final String CURRENT_FIELD = "Current Field";
    public static final String UID = "Uid";
    ImageButton imTrainingHereButton;


    FirebaseFirestore db = FirebaseFirestore.getInstance();



    // The details of the venue that is being displayed.

     String venueName;
    private String venueID;
    private CollectionReference collectionReference;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference reference;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_detail);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        imTrainingHereButton = findViewById(R.id.imTrainingHereButton);
        imTrainingHereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPress();
            }
        });




        // Retrieves venues details from the intent sent from PlacePickerActivity
        Bundle venue = getIntent().getExtras();

        venueName = venue.getString("name");
        venueID = venue.getString("ID");

        setTitle(venueName);
        TextView fieldName = findViewById(R.id.fieldName);
        fieldName.setText(venueName);


        collectionReference = FirebaseFirestore.getInstance().collection("Fields");


//TODO add new field only if it doesn't already exist
        Map<String, Object> field = new HashMap<>();
        field.put(FIELD_NAME, venueName);
        field.put(PEOPLE_HERE_KEY, 0);
        collectionReference.document(venueID).set(field);


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

    public void onButtonPress(){

       String uid = user.getUid();
       reference = FirebaseFirestore.getInstance().collection("Users").document(uid);

       Map<String, Object> users = new HashMap<>();
       users.put(CURRENT_FIELD, venueName);
       users.put(UID, uid);
       reference.set(users);
   }
}

