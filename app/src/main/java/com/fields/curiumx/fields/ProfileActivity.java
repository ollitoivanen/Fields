package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
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

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ProfileActivity extends Activity implements View.OnClickListener{

    ImageButton profileButton;
    TextView testCurrentField;
    TextView username;
    TextView usersTeam;
    TextView roleText;
    DocumentReference reference;
    String currentField;
    String UserName;
    String UserTeam;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    long placeHolder;
    String placeHolder2;
    ProgressBar progressBar;
    ImageView profileImage;
    TextView friends;
    ConstraintLayout reputation;
    TextView fields_plus;
    TextView reputationText;
    TextView trainingCount;
    ConstraintLayout gradient;
    String uid = user.getUid();
    String[] userRoleArray;
    String [] userPositionArray;




    private void loadUserInformation() {

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(profileImage);
            }else{
                profileImage.setImageDrawable(getResources().getDrawable(R.drawable.profileim));
            }
        }else{
            startActivity(new Intent(ProfileActivity.this, SignUpActivity.class));
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
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileButton = findViewById(R.id.profile_button);
        profileButton.setImageDrawable(getResources().getDrawable(R.drawable.person_green));

        reference = FirebaseFirestore.getInstance().collection("Users").document(uid);
        userRoleArray = getResources().getStringArray(R.array.role_spinner);
        userPositionArray = getResources().getStringArray(R.array.position_spinner);
        friends = findViewById(R.id.friends);
        reputationText = findViewById(R.id.reputation);
        profileImage = findViewById(R.id.profilePhoto);
        fields_plus = findViewById(R.id.fields_plus);
        reputation = findViewById(R.id.rep);
        reputation.setOnClickListener(this);
        fields_plus.setOnClickListener(this);
        friends.setOnClickListener(this);

        gradient = findViewById(R.id.gradient);
        roleText = findViewById(R.id.position_role_text);
        trainingCount = findViewById(R.id.trainings);
        testCurrentField = findViewById(R.id.testCurrentField);
        username = findViewById(R.id.userName);
        usersTeam = findViewById(R.id.usersTeam);
        progressBar = findViewById(R.id.progress_bar);
        UserName = user.getDisplayName();
        username.setText(UserName);

        loadChanges();
        loadUserInformation();
        }

        public void loadChanges() {

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    testCurrentField.setVisibility(View.VISIBLE);
                    gradient.setVisibility(View.VISIBLE);
                    reputation.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    final DocumentSnapshot documentSnapshot = task.getResult();
                    setTitle(documentSnapshot.get("username").toString());
                    int userRole = documentSnapshot.getLong("userRole").intValue();
                    int userPosition = documentSnapshot.getLong("position").intValue();
                    int trainingCountText = documentSnapshot.getLong("trainingCount").intValue();
                    trainingCount.setText(getResources().getString(R.string.training, Long.toString(trainingCountText)));
                    String userRoleString = userRoleArray[userRole];

                    if (userPosition == -1) {
                        roleText.setText(getResources().getString(R.string.player_position_role_not_given, userRoleString));
                    }else{
                        String userPositionString = userPositionArray[userRole];
                        roleText.setText(getResources().getString(R.string.player_position_role_given, userRoleString, userPositionString));
                    }

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
                            placeHolder2 = placeHolder + " " + getResources().getString(R.string.minutes);
                        } else if (days < 1) {
                            placeHolder = hours;
                            placeHolder2 = placeHolder + " " + getResources().getString(R.string.hours);
                        } else {
                            placeHolder = days;
                            placeHolder2 = placeHolder + " " + getResources().getString(R.string.days);
                        }
                        currentField = documentSnapshot.get("currentFieldName").toString();
                        testCurrentField.setText(getResources().getString(R.string.last_seen_at, currentField, placeHolder2));


                        testCurrentField.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String fieldID = documentSnapshot.get("currentFieldID").toString();
                                db.collection("Fields").document(fieldID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot ds = task.getResult();
                                        String fieldArea = ds.get("fieldArea").toString();
                                        String fieldAddress = ds.get("fieldAddress").toString();
                                        String goalCount = ds.get("goalCount").toString();
                                        String fieldType= ds.get("fieldType").toString();
                                        String accessType = ds.get("accessType").toString();
                                        String creator = ds.get("creator").toString();
                                        String creatorName = ds.get("creatorName").toString();
                                        Intent intent = new Intent(ProfileActivity.this, DetailFieldActivity.class);
                                        intent.putExtra("fieldID", fieldID);
                                        intent.putExtra("fieldName", currentField);
                                        intent.putExtra("fieldArea", fieldArea);
                                        intent.putExtra("fieldAddress", fieldAddress);
                                        intent.putExtra("goalCount", goalCount);
                                        intent.putExtra("fieldType", fieldType);
                                        intent.putExtra("fieldAccessType", accessType);
                                        intent.putExtra("creator", creator);
                                        intent.putExtra("creatorName", creatorName);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
                    } else {
                        testCurrentField.setText(getResources().getString(R.string.not_at_any_field));
                    }
                    if (documentSnapshot.get("usersTeam") != null) {
                        UserTeam = documentSnapshot.get("usersTeam").toString();
                        usersTeam.setText(UserTeam);
                        usersTeam.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(ProfileActivity.this, TeamActivity.class));
                            }
                        });
                    } else {
                        usersTeam.setText(getResources().getString(R.string.not_at_team));
                    }
                    String reputation = documentSnapshot.get("userReputation").toString();
                    reputationText.setText(getResources().getString(R.string.reputation, reputation));
                    } else {
                    Log.d(TAG, "no such file");
                    Snackbar.make(findViewById(R.id.profileActivity), getResources().getString(R.string.error_occurred_loading_document), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
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

            case R.id.rep:
                startActivity(new Intent(ProfileActivity.this, ReputationActivity.class));
                break;
            case R.id.fields_plus:
                startActivity(new Intent(ProfileActivity.this, FieldsPlusStartActivity.class));
                break;
            case R.id.friends:
                startActivity(new Intent(ProfileActivity.this, FriendListActivity.class));
                break;
        }
    }
}
