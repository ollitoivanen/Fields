package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class DetailEventActivity extends Activity {
    TextView detailType;
    TextView detailPlace;
    TextView detailTime;
    TextView detailDate;
    ImageView crossRed;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    String eventID;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);
        detailType = findViewById(R.id.eventTypeDetail);
        detailDate = findViewById(R.id.eventDateDetail);
        detailPlace = findViewById(R.id.eventPlaceDetail);
        detailTime = findViewById(R.id.eventTimeDetail);
        progressBar = findViewById(R.id.prgress_delete);
        crossRed =findViewById(R.id.crossRed);
        crossRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crossRed.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       DocumentSnapshot ds = task.getResult();
                       String teamID = ds.get("usersTeamID").toString();

                       db.collection("Teams").document(teamID).collection("Team's Events")
                               .document(eventID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               crossRed.setEnabled(true);
                               progressBar.setVisibility(View.GONE);
                               Toast.makeText(getApplicationContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(DetailEventActivity.this, TeamActivity.class);
                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                               startActivity(intent);

                           }
                       });
                    }
                });
            }
        });

        Bundle info = getIntent().getExtras();
        String type  = info.getString("type");
        String place = info.getString("place");
        String date = info.getString("date");
        String timeStart = info.getString("timeStart");
        String timeEnd = info.getString("timeEnd");
         eventID = info.getString("eventID");

        detailType.setText(type);
        detailDate.setText(date);
        if (place.equals("")){
            detailPlace.setText(getResources().getString(R.string.location_not_given));
        }else {
            detailPlace.setText(place);
        }
        detailTime.setText(timeStart + "- " + timeEnd);
    }
}
