package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.xml.datatype.Duration;

import static android.content.ContentValues.TAG;

public class ProfileActivity extends Activity {
    ImageButton profileButton;
    TextView testCurrentField;
    TextView username;
    TextView usersTeam;
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
    View divider;

    private void loadUserInformation() {

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(profileImage);
            }else{
                profileImage.setImageDrawable(getResources().getDrawable(R.drawable.field_photo3));
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
        divider = findViewById(R.id.divider);

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
                if (task.getResult().size() == 1 ){
                    friends.setText(String.valueOf(task.getResult().size()) + " " + friendText);
                    }else {
                    friends.setText(String.valueOf(task.getResult().size()) + " " + friendsText);

                }
            }
        });

                reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        testCurrentField.setVisibility(View.VISIBLE);
                        username.setVisibility(View.VISIBLE);
                        usersTeam.setVisibility(View.VISIBLE);
                        profileImage.setVisibility(View.VISIBLE);
                        friends.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        divider.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()){
                            final DocumentSnapshot documentSnapshot = task.getResult();
                            setTitle(documentSnapshot.get("username").toString());



                            if (!documentSnapshot.get("currentFieldName").toString().equals("")){

                                Map<String,Object> map = documentSnapshot.getData();
                                Date checkDate = (Date) map.get("timestamp");
                                Date currentTime = Calendar.getInstance().getTime();
                                long diff = currentTime.getTime() - checkDate.getTime();
                                long seconds = diff/1000;
                                long minutes = seconds/60;
                                long hours = minutes/60;
                                long days = hours/24;

                                if (hours < 1){
                                    placeHolder = minutes;
                                    placeHolder2 = placeHolder + " " + "minutes";
                                } else if (days < 1){
                                    placeHolder = hours;
                                    placeHolder2 = placeHolder + " " + "hours";
                                }else {
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
                                testCurrentField.setText("Last seen at:"+  " " + currentField + "," + " " + placeHolder2 + " " + "ago");
                            }else{
                                testCurrentField.setText("Not at any field");
                            }

                            if (documentSnapshot.get("User's team")!=null){
                                UserTeam = documentSnapshot.get("User's team").toString();
                                usersTeam.setText(UserTeam);


                            }else {
                                usersTeam.setText("Not at any team");
                            }
                        }else {
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


}
