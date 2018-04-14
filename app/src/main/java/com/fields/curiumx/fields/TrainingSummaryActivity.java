package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TrainingSummaryActivity extends Activity {
    TextView rept;
    Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_summary);

        Bundle info = getIntent().getExtras();
        String trainingRep = info.getString("trainingRep");

        rept = findViewById(R.id.rept);
        rept.setText(trainingRep);
        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }
}
