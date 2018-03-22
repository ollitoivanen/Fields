package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class TeamActivity extends Activity {
    String teamName;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    Button createNewTeamButton;
    Button joinTeamButton;
    TextView createTeamText;
    TextView teamTextName;
    TextView teamTextCountry;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(uid);
        createNewTeamButton = findViewById(R.id.createNewTeamButton);
        joinTeamButton = findViewById(R.id.joinTeamButton);
        createTeamText = findViewById(R.id.createTeamText);
        teamTextName = findViewById(R.id.teamName12);
        teamTextCountry = findViewById(R.id.teamName13);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               DocumentSnapshot documentSnapshot = task.getResult();
               if (documentSnapshot.get("User's team")==null){

                   createNewTeamButton.setVisibility(View.VISIBLE);
                   joinTeamButton.setVisibility(View.VISIBLE);
                   createTeamText.setVisibility(View.VISIBLE);

                   createNewTeamButton.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           startActivity(new Intent(TeamActivity.this, CreateNewTeamActivity.class));
                       }
                   });

                   joinTeamButton.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           startActivity(new Intent(TeamActivity.this, SearchActivity.class));
                       }
                   });
                }else {
                   String testt = documentSnapshot.get("User's team").toString();

                   db.collection("Teams").whereEqualTo("teamNameText", testt).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           QuerySnapshot querySnapshot = task.getResult();
                           DocumentSnapshot documentSnapshot1 = querySnapshot.getDocuments().get(0);
                           String nameBoi = documentSnapshot1.get("teamNameText").toString();
                           String countryBoi = documentSnapshot1.get("teamCountryText").toString();
                           teamTextName.setText(nameBoi);
                           teamTextCountry.setText(countryBoi);
                           //Show users team in the TeamActivity, needs to be optimized
                       }
                   });

                       }
            }
        });






    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
