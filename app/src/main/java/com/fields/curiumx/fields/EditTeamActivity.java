package com.fields.curiumx.fields;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditTeamActivity extends AppCompatActivity {
    ImageView teamPhotoEdit;
    Uri uriTeamImage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String teamImageUrl;
    String teamName;
    String teamCountry;
    int level;
    String teamID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team);

        Bundle info = getIntent().getExtras();
        teamName = info.getString("teamName");
        teamID = info.getString("teamID");
        teamCountry = info.getString("teamCountry");
        level = info.getInt("level");
        teamPhotoEdit = findViewById(R.id.teamPhotoEdit);
        teamPhotoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final StorageReference storageRef = storage.getReference().child("fieldpics/"+teamID+".jpg");
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Glide.with(getApplicationContext())
                            .load(storageRef)
                            .into(teamPhotoEdit);
                }else {
                    teamPhotoEdit.setImageDrawable(getResources().getDrawable(R.drawable.team_basic));
                }

            }
        });
    }


}
