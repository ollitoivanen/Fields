package com.fields.curiumx.fields;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateNewTeamActivity extends Activity {
    EditText teamName;
    EditText teamCountry;
    Button saveTeamButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_team);
        teamName = findViewById(R.id.teamName);
        teamCountry  = findViewById(R.id.teamCountry);
        saveTeamButton = findViewById(R.id.saveTeamButton);
        saveTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTeam();
            }
        });
    }



    public void saveTeam(){
        final String teamNameText = teamName.getText().toString();
        final String teamCountryText = teamCountry.getText().toString();
        final String username = user.getDisplayName();

        if (teamNameText.isEmpty()){
            teamName.setError("Team name is required");
            teamName.requestFocus();
            return;
        }

        if (teamCountryText.isEmpty()){
            teamCountry.setError("Team country is required");
            teamCountry.requestFocus();
            return;
        }

        //Check if team with the same name already exists
        db.collection("Teams").document(teamNameText).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    teamName.setError("Team with same name already exists");
                    teamName.requestFocus();

                }else{
                    //If team with same name doesn't exist, save it
                    db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();


                            //Check if user is already in a team
                            if (documentSnapshot.get("User's team") == null){
                                TeamMap data1 = new TeamMap(teamNameText, teamCountryText);
                                db.collection("Teams").document(teamNameText).set(data1);

                                Map<String, Object> data = new HashMap<>();
                                data.put("username", username);

                                db.collection("Teams").document(teamNameText).collection("TeamUsers").document(uid).set(data);
                                Map<String, Object> data2 = new HashMap<>();
                                data2.put("User's team", teamNameText);

                                db.collection("Users").document(uid).set(data2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(getApplicationContext(), "You are already in a team", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
