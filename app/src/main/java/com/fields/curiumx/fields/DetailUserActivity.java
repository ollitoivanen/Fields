package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class DetailUserActivity extends Activity {

    FloatingActionButton chatUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DocumentReference reference;
    String uid = user.getUid();
    String displayName;
    String username;
    String userID;
    String currentFieldID;
    String currentFieldName;
    String usersTeam;
    String usersTeamID;
    String userRole;
    String userReputation;
    String userBio;
    String position;
    TextView friends;
    ProgressBar progressBar;
    long placeHolder;
    String placeHolder2;
    String currentField;
    String UserTeam;
    TextView testCurrentField;
    TextView usernameText;
    TextView usersTeamText;
    TextView bioText;
    TextView roleText;
    TextView rept;
    ImageView profileImage;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.message_player:
                Intent intent = new Intent(DetailUserActivity.this, UserChatActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);

        profileImage = findViewById(R.id.profilePhoto);
        final String friendText = getString(R.string.friend);
        final String friendsText = getString(R.string.friends);

        roleText = findViewById(R.id.position_role_text);
        bioText = findViewById(R.id.bio_text1);
        testCurrentField = findViewById(R.id.testCurrentField);
        usernameText = findViewById(R.id.userName);
        usersTeamText = findViewById(R.id.usersTeam);
        rept = findViewById(R.id.reputation);
        friends = findViewById(R.id.friends);
        progressBar = findViewById(R.id.progress_bar);





        Bundle info = getIntent().getExtras();
        displayName = info.getString("displayName");
        username = info.getString("username");
        userID = info.getString("userID");
        currentFieldID = info.getString("currentFieldID");
        currentFieldName = info.getString("currentFieldName");
        usersTeam = info.getString("usersTeam");
        usersTeamID = info.getString("usersTeamID");
        userRole = info.getString("userRole");
        userReputation = info.getString("userReputation");
        userBio = info.getString("userBio");
        position = info.getString("position");


        final StorageReference storageRef = storage.getReference().child("profilepics/"+userID+".jpg");
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Glide.with(getApplicationContext())
                            .load(storageRef)
                            .into(profileImage);
                }else {
                    profileImage.setImageDrawable(getResources().getDrawable(R.drawable.profileim));
                }

            }
        });


             loadChanges();
    }

    public void loadChanges() {
                reference = FirebaseFirestore.getInstance().collection("Users").document(userID);

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                testCurrentField.setVisibility(View.VISIBLE);
                usernameText.setVisibility(View.VISIBLE);
                usersTeamText.setVisibility(View.VISIBLE);
                profileImage.setVisibility(View.VISIBLE);
                friends.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                bioText.setVisibility(View.VISIBLE);
                roleText.setVisibility(View.VISIBLE);
                if (task.isSuccessful()) {


                    final DocumentSnapshot documentSnapshot = task.getResult();
                    setTitle(documentSnapshot.get("username").toString());
                    bioText.setText(documentSnapshot.get("userBio").toString());
                    roleText.setText(documentSnapshot.get("userRole").toString() + "," + " " + documentSnapshot.get("position").toString());


                    if (!documentSnapshot.get("currentFieldName").toString().equals("")) {

                        Map<String, Object> map = documentSnapshot.getData();
                        Date checkDate = (Date) map.get("timestamp");
                        Date currentTime = Calendar.getInstance().getTime();
                        long diff = currentTime.getTime() - checkDate.getTime();
                        long seconds = diff / 1000;
                        long minutes = seconds / 60;
                        long hours = minutes / 60;
                        long days = hours / 24;

                        if (hours < 1) {
                            placeHolder = minutes;
                            placeHolder2 = placeHolder + " " + "minutes";
                        } else if (days < 1) {
                            placeHolder = hours;
                            placeHolder2 = placeHolder + " " + "hours";
                        } else {
                            placeHolder = days;
                            placeHolder2 = placeHolder2 + " " + "days";
                        }



                      //testCurrentField.setOnClickListener(new View.OnClickListener() {
                      //    @Override
                      //    public void onClick(View v) {
                      //        String venueID = documentSnapshot.get("currentFieldID").toString();
                      //
                      //        Intent intent = new Intent(DetailUserActivity.this, DetailFieldActivity.class);
                      //        intent.putExtra("ID", venueID);
                      //        intent.putExtra("name", currentField);
                      //        startActivity(intent);
                      //    }
                      //});
                      //
                        currentField = documentSnapshot.get("currentFieldName").toString();
                        testCurrentField.setText("Last seen at:" + " " + currentField + "," + " " + placeHolder2 + " " + "ago");
                    } else {
                        testCurrentField.setText("Not at any field");
                    }

                    if (documentSnapshot.get("User's team") != null) {
                        UserTeam = documentSnapshot.get("User's team").toString();
                        usersTeamText.setText(UserTeam);
                    } else {
                        usersTeamText.setText("Not at any team");
                    }

                    String reputation = documentSnapshot.get("userReputation").toString();
                    rept.setText(reputation);

                } else {
                    Log.d(TAG, "no such file");
                }

            }
        });
    }





}
