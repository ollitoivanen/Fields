package com.fields.curiumx.fields;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailTeamActivity extends Activity {

    String teamName;
    String teamCountry;
    TextView textName;
    TextView textCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_team);

        textName = findViewById(R.id.teamName);
        textCountry = findViewById(R.id.teamCountry);


        Bundle info = getIntent().getExtras();
        teamName = info.getString("name");
        teamCountry = info.getString("country");

        textName.setText(teamName);
        textCountry.setText(teamCountry);

    }
}
