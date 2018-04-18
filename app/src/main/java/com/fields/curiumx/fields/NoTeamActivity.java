package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NoTeamActivity extends Activity {
    Button createNewTeamButton;
    Button joinTeamButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_team);
        createNewTeamButton = findViewById(R.id.createNewTeamButton);
        joinTeamButton = findViewById(R.id.joinTeamButton);
        createNewTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoTeamActivity.this, CreateNewTeamActivity.class));
                finish();
            }
        });

        joinTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NoTeamActivity.this, SearchActivity.class));
            }
        });
    }
}
