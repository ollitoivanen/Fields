package com.fields.curiumx.fields;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DetailTeamActivity extends Activity {

    String teamName;
    String teamCountry;
    TextView textName;
    TextView textCountry;
    Button joinButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_team);

        textName = findViewById(R.id.teamName);
        textCountry = findViewById(R.id.teamCountry);
        joinButton = findViewById(R.id.join);


        Bundle info = getIntent().getExtras();
        teamName = info.getString("name");
        teamCountry = info.getString("country");

        textName.setText(teamName);
        textCountry.setText(teamCountry);

        final String username = user.getDisplayName();


        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> data = new HashMap<>();
                data.put("User's team", teamName);
                db.collection("Users").document(uid).set(data);

                Map<String, Object> data1 =new HashMap<>();
                data1.put("username", username);
                db.collection("Teams").document(teamName).collection("TeamUsers").document(uid).set(data1);
            }
        });

    }
}
