package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ProfileActivity extends Activity implements View.OnClickListener{
    ImageButton profileButton;
    TextView testCurrentField;
    TextView username;
    TextView usersTeam;
    TextView bioText;
    TextView roleText;
    DocumentReference reference;
    String currentField;
    String UserName;
    String UserTeam;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    long placeHolder;
    String placeHolder2;
    ProgressBar progressBar;
    ImageView profileImage;
    TextView friends;
    TextView challenges;
    TextView badges;
    ConstraintLayout rep;
    TextView fields_plus;


    private void loadUserInformation() {

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(profileImage);
            }else{
                profileImage.setImageDrawable(getResources().getDrawable(R.drawable.profileim));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_profile:
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("DisplayName",user.getDisplayName());
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        progressBar.setVisibility(View.GONE);
        loadChanges();
        loadUserInformation();
        UserName = user.getDisplayName();
        username.setText(UserName);
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String friendText = getString(R.string.friend);
        final String friendsText = getString(R.string.friends);
        friends = findViewById(R.id.friends);
        profileButton = findViewById(R.id.profile_button);
        profileButton.setImageDrawable(getResources().getDrawable(R.drawable.person_green));
        profileImage = findViewById(R.id.profilePhoto);
        challenges = findViewById(R.id.challenges);
        badges = findViewById(R.id.badges);
        fields_plus = findViewById(R.id.fields_plus);
        rep = findViewById(R.id.rep);
        rep.setOnClickListener(this);
        badges.setOnClickListener(this);
        challenges.setOnClickListener(this);
        fields_plus.setOnClickListener(this);

        roleText = findViewById(R.id.position_role_text);
        bioText = findViewById(R.id.bio_text1);

        loadUserInformation();

        testCurrentField = findViewById(R.id.testCurrentField);
        username = findViewById(R.id.userName);
        usersTeam = findViewById(R.id.usersTeam);
        progressBar = findViewById(R.id.progress_bar);
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, FriendListActivity.class));
            }
        });

        String uid = user.getUid();
        UserName = user.getDisplayName();
        username.setText(UserName);
        reference = FirebaseFirestore.getInstance().collection("Users").document(uid);


        reference.collection("Friends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.getResult().size() == 1) {
                    friends.setText(String.valueOf(task.getResult().size()) + " " + friendText);
                } else {
                    friends.setText(String.valueOf(task.getResult().size()) + " " + friendsText);

                }
            }
        });
        loadChanges();
    }

        public void loadChanges() {
            reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    testCurrentField.setVisibility(View.VISIBLE);
                    username.setVisibility(View.VISIBLE);
                    usersTeam.setVisibility(View.VISIBLE);
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

                            testCurrentField.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String venueID = documentSnapshot.get("Current Field ID").toString();

                                    Intent intent = new Intent(ProfileActivity.this, DetailFieldActivity.class);
                                    intent.putExtra("ID", venueID);
                                    intent.putExtra("name", currentField);
                                    startActivity(intent);
                                }
                            });

                            currentField = documentSnapshot.get("Current Field name").toString();
                            testCurrentField.setText("Last seen at:" + " " + currentField + "," + " " + placeHolder2 + " " + "ago");
                        } else {
                            testCurrentField.setText("Not at any field");
                        }

                        if (documentSnapshot.get("User's team") != null) {
                            UserTeam = documentSnapshot.get("User's team").toString();
                            usersTeam.setText(UserTeam);
                        } else {
                            usersTeam.setText("Not at any team");
                        }


                    } else {
                        Log.d(TAG, "no such file");
                    }

                }
            });
        }










    public void onExploreClick(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void onProfileClick(View view){
    }

    

    public void onFeedClick(View view){
        LinearLayout activityBar = findViewById(R.id.activityBar);
        Intent intent = new Intent(this, FeedActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, activityBar, "bar");
        startActivity(intent, options.toBundle());


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.challenges:
                startActivity(new Intent(ProfileActivity.this, ChallengesActivity.class));
                break;
            case R.id.badges:
                startActivity(new Intent(ProfileActivity.this, BadgesActivity.class));
                break;
            case R.id.rep:
                startActivity(new Intent(ProfileActivity.this, ReputationActivity.class));
                break;
            case R.id.fields_plus:
                startActivity(new Intent(ProfileActivity.this, FieldsPlusStartActivity.class));
                break;
        }
    }
}
