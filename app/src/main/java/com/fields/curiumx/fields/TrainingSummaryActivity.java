package com.fields.curiumx.fields;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TrainingSummaryActivity extends AppCompatActivity {
    TextView rept;
    Button done;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean fromNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_summary);
        setTitle(getResources().getString(R.string.training_summary));

        Bundle info = getIntent().getExtras();
        String trainingRep = info.getString("trainingRep");
        fromNotification = info.getBoolean("fromNotification");

        rept = findViewById(R.id.rept);
        rept.setText(getResources().getString(R.string.reputation_count, trainingRep));
        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromNotification){
                    startActivity(new Intent(TrainingSummaryActivity.this, FeedActivity.class));
                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                    finish();
                }else {
                    onBackPressed();
                    finish();
                }

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }
}
