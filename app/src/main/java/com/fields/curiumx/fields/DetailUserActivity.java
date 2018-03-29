package com.fields.curiumx.fields;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DetailUserActivity extends Activity {
    TextView DisplayName;
    TextView currentField;
    Button addFriend;
    String usernameString;
    Button removeFriend;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID;
    String displayNameString;
    String currentFieldName;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String uid = user.getUid();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);

        Bundle info = getIntent().getExtras();
        usernameString = info.getString("username");
        userID = info.getString("userID");
        displayNameString = info.getString("displayName");
        currentFieldName = info.getString("currentFieldName");


        currentField = findViewById(R.id.currentField);
        removeFriend = findViewById(R.id.unFollowButton);
        DisplayName = findViewById(R.id.username);
        DisplayName.setText(displayNameString);
        currentField.setText("Currently at:" + " " + currentFieldName);
        setTitle(usernameString);
    }

    @Override
    protected void onStart() {
        super.onStart();
    db.collection("Users").document(uid).collection("Friends").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    addFriend.setVisibility(View.GONE);
                    removeFriend.setVisibility(View.VISIBLE);
                }
            }
        });


        addFriend = findViewById(R.id.followButton);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFriend.setVisibility(View.VISIBLE);
                addFriend.setVisibility(View.GONE);

                Map<String, Object> data = new HashMap<>();
                data.put("username", usernameString);
                data.put("userID", userID);
                db.collection("Users").document(uid).collection("Friends").document(userID).set(data);
            }
        });

        removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFriend.setVisibility(View.GONE);
                addFriend.setVisibility(View.VISIBLE);
                db.collection("Users").document(uid).collection("Friends").document(userID).delete();
            }
        });







    }



}
