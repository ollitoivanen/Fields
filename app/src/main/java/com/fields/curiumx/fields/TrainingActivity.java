package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class TrainingActivity extends AppCompatActivity {
    TextView currentField;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView timeBox;
    long placeHolder0;
    long placeHolder;
    String placeHolder2;
    Button endTrainingButton;
    long diff;
    long reputation;
    String reputation1;
    String userReputation;
    Long userReputationNew;
    int time;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);


        currentField = findViewById(R.id.field_training);
        timeBox = findViewById(R.id.timeBox);
        endTrainingButton = findViewById(R.id.endTraining);


        final Bundle info = getIntent().getExtras();
        String fieldName = info.getString("fieldName");
        currentField.setText(fieldName);
        setTitle("Current Training");

        endTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot ds = task.getResult();
                        Map<String, Object> map = ds.getData();
                        Date checkTime = (Date) map.get("timestamp");
                        Date currentTime = Calendar.getInstance().getTime();
                        diff = currentTime.getTime() - checkTime.getTime();
                        userReputation = ds.get("userReputation").toString();

                        if (diff < 900000){
                            reputation = 0;
                        } else if (diff > 18000000) {
                    reputation = 0;
                } else {
                    reputation = diff / 60000;
                     userReputationNew = reputation + Long.parseLong(userReputation);
                     UserMap userMap = new UserMap();
                     db.collection("Users").document(uid).update("userReputation", userReputationNew);
                            db.collection("Users").document(uid).update("userReputation", userReputationNew);
                            db.collection("Users").document(uid).update("trainingCount", userMap.getTrainingCount() + 1);




                        }

                        db.collection("Users").document(uid).update("currentFieldID", "");
                        db.collection("Users").document(uid).update("currentFieldName", "");
                        db.collection("Users").document(uid).update("timestamp", null);
                        Intent intent = new Intent(TrainingActivity.this, TrainingSummaryActivity.class);

                        intent.putExtra("trainingRep", Long.toString(reputation));
                        startActivity(intent);
                        finish();
            }
        });
            }
        });



    }

    @Override
    protected void onStart() {
        loadChanges();
        super.onStart();
    }

    public void loadChanges() {
        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot ds = task.getResult();
                    Map<String, Object> map = ds.getData();
                    Date checkTime = (Date) map.get("timestamp");
                    Date currentTime = Calendar.getInstance().getTime();
                    diff = currentTime.getTime() - checkTime.getTime();
                    long seconds = diff / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;

                    if (minutes < 1) {
                        placeHolder2 = "<1 minute";
                    } else if (hours < 1) {
                        placeHolder = minutes;
                        placeHolder2 = placeHolder + " " + "minutes";
                    } else{
                        placeHolder = hours;
                        placeHolder0 = minutes - hours*60;
                        placeHolder2 = placeHolder + " " + "h" + " " + placeHolder0 + " " + "min";
                    }

                    timeBox.setText(placeHolder2);
                }
            }
        });


    }
}