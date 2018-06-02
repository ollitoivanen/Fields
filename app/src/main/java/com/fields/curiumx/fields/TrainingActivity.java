package com.fields.curiumx.fields;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    long userReputation;
    Long userReputationNew;
    int time;
    NotificationHelper notificationHelper;
    boolean fromNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationHelper = new NotificationHelper(this);

        setContentView(R.layout.activity_training);
        currentField = findViewById(R.id.field_training);
        timeBox = findViewById(R.id.timeBox);
        endTrainingButton = findViewById(R.id.endTraining);
        final Bundle info = getIntent().getExtras();
        final String fieldName = info.getString("fieldName");
        fromNotification = info.getBoolean("fromNotification");
        currentField.setText(fieldName);
        setTitle(getResources().getString(R.string.current_training));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        endTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                         notificationHelper.getManager().cancel(1);

                        DocumentSnapshot ds = task.getResult();
                        Map<String, Object> map = ds.getData();
                        Date checkTime = (Date) map.get("timestamp");
                        Date currentTime = Calendar.getInstance().getTime();
                        diff = currentTime.getTime() - checkTime.getTime();
                        userReputation = ds.getLong("userReputation");

                        if (diff < 900000){
                            reputation = 0;
                        } else if (diff > 18000000) {
                    reputation = 0;
                } else {
                    reputation = diff / 60000;
                     userReputationNew = reputation + userReputation;
                     db.collection("Users").document(uid).update("userReputation", userReputationNew,
                             "userReputation", userReputationNew,
                             "trainingCount", ds.getLong("trainingCount").intValue() + 1);



                     TrainingMap trainingMap = new TrainingMap(checkTime, reputation, diff, fieldName);

                     db.collection("Users").document(uid).collection("Trainings")
                             .document(Long.toString(System.currentTimeMillis())).set(trainingMap);
                        }

                        db.collection("Users").document(uid).update("currentFieldID", "",
                                "currentFieldName", "", "timestamp", null);
                        db.collection("Users").document(uid).collection("currentTraining").document(uid).delete();
                        Intent intent = new Intent(TrainingActivity.this, TrainingSummaryActivity.class);
                        intent.putExtra("trainingRep", Long.toString(reputation));
                        intent.putExtra("fromNotification", fromNotification);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}