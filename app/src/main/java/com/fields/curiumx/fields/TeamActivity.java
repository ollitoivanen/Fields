package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    Button leaveButton;
    ImageView teamImage;
    ProgressBar progressBar;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Users").document(uid);
        createNewTeamButton = findViewById(R.id.createNewTeamButton);
        joinTeamButton = findViewById(R.id.joinTeamButton);
        createTeamText = findViewById(R.id.createTeamText);
        teamTextName = findViewById(R.id.teamName12);
        teamTextCountry = findViewById(R.id.teamName13);
        leaveButton = findViewById(R.id.leaveButton);
        teamImage = findViewById(R.id.teamImage);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Team");
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.get("User's team") == null) {

                    createNewTeamButton.setVisibility(View.VISIBLE);
                    joinTeamButton.setVisibility(View.VISIBLE);
                    createTeamText.setVisibility(View.VISIBLE);
                    leaveButton.setVisibility(View.GONE);
                    teamTextCountry.setVisibility(View.GONE);
                    teamTextName.setVisibility(View.GONE);
                    teamImage.setVisibility(View.GONE);
progressBar.setVisibility(View.GONE);



                    createNewTeamButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(TeamActivity.this, CreateNewTeamActivity.class));
                            finish();
                        }
                    });

                    joinTeamButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(TeamActivity.this, SearchActivity.class));
                        }
                    });
                } else {

                    final String testt = documentSnapshot.get("User's team").toString();

                    db.collection("Teams").whereEqualTo("teamNameText", testt).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            progressBar.setVisibility(View.GONE);

                            leaveButton.setVisibility(View.VISIBLE);
                            teamTextCountry.setVisibility(View.VISIBLE);
                            teamTextName.setVisibility(View.VISIBLE);
                            teamImage.setVisibility(View.VISIBLE);

                            QuerySnapshot querySnapshot = task.getResult();
                            DocumentSnapshot documentSnapshot1 = querySnapshot.getDocuments().get(0);
                            String nameBoi = documentSnapshot1.get("teamNameText").toString();
                            String countryBoi = documentSnapshot1.get("teamCountryText").toString();
                            teamTextName.setText(nameBoi);
                            teamTextCountry.setText(countryBoi);
                            leaveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    db.collection("Teams").document(testt).collection("TeamUsers").document(uid).delete();
                                    docRef.update("User's team", null);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "Left team successfully", Toast.LENGTH_LONG).show();
                                    //Delete user information
                                }
                            });

                            //Show users team in the TeamActivity, needs to be optimized
                        }
                    });

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
