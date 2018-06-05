package com.fields.curiumx.fields;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, BillingProcessor.IBillingHandler{

    ImageButton profileButton;
    TextView testCurrentField;
    TextView realNameTextView;
    TextView usersTeam;
    TextView roleText;
    DocumentReference reference;
    String currentField;
    String UserName;
    String UserTeam;
    String realName;
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
    int userRole;
    int userPosition;
    int trainingCountText;
    long reputationInt;
    ImageView badge_rep;
    Boolean fieldsPlus;
    BillingProcessor bp;
    String teamID;
    ConstraintLayout teamTab;
    StorageReference profileImageRef;
    protected String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhP70LSlF/j2XxzB5EERbyj1J/N8l6EJS8tCWLtbaB7a72Rr7uYWex6CgtQ2gGsRSpInGa1dOyjT9cV+JvKNVTv/WyhIEpcFQJiI2rlQcAkAWNivaffsBxUfODq6Xp2urNdgQ/35CTp/wYm75oHxE9nnqpI4X0Jk1iUKKBew8DIo2JUh9ezjruk2b+txmFTyDi0Fdm6yLmLUL0eed0mU5KrQO0FO5OHI990bCfQPIoZGKA7FPbiWSS09rn36j3HinD4fc2L52LgIwvz4vcWyMRmCioWygxpMnyUs+TP0C3mXrdJiZkrmYig5T1zgtdy4wru5EOtW6qYwSYsj64WAS1wIDAQAB";

    private void loadUserInformation() {

        if (user != null) {

            profileImageRef = FirebaseStorage.getInstance().getReference().child("profilepics/" + uid + "/" +uid+".jpg");
            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    GlideApp.with(getApplicationContext())
                            .load(uri)
                            .into(profileImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    profileImage.setImageDrawable(getResources().getDrawable(R.drawable.profile_default));

                }
            });


        }else{
            startActivity(new Intent(ProfileActivity.this, SignUpActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_profile, menu);
        inflater.inflate(R.menu.settings_user, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_profile:
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("username",user.getDisplayName());
                intent.putExtra("realName", realName);
                intent.putExtra("userRole", userRole);
                intent.putExtra("userPosition", userPosition);
                intent.putExtra("teamID", teamID);

                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                break;
            case R.id.settings_user:
                Intent intent1 = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        progressBar.setVisibility(View.GONE);
        loadChanges();
        loadUserInformation();
        UserName = user.getDisplayName();
        realNameTextView.setText(realName);
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bp = new BillingProcessor(this, key, this);
        teamTab = findViewById(R.id.team_with_icon);



        profileButton = findViewById(R.id.profile_button);
        profileButton.setImageDrawable(getResources().getDrawable(R.drawable.person_green));
        badge_rep = findViewById(R.id.badge_rep);

        reference = FirebaseFirestore.getInstance().collection("Users").document(uid);
        userRoleArray = getResources().getStringArray(R.array.role_spinner);
        userPositionArray = getResources().getStringArray(R.array.position_spinner);
        friends = findViewById(R.id.friends);
        reputationText = findViewById(R.id.reputation);
        profileImage = findViewById(R.id.profilePhoto);
        fields_plus = findViewById(R.id.fields_plus);
        reputation = findViewById(R.id.rep);
        trainingCount = findViewById(R.id.trainings);

        reputation.setOnClickListener(this);
        fields_plus.setOnClickListener(this);
        friends.setOnClickListener(this);
        trainingCount.setOnClickListener(this);

        gradient = findViewById(R.id.gradient);
        roleText = findViewById(R.id.position_role_text);
        testCurrentField = findViewById(R.id.testCurrentField);
        realNameTextView = findViewById(R.id.real_name);
        usersTeam = findViewById(R.id.usersTeam);
        progressBar = findViewById(R.id.progress_bar);
        UserName = user.getDisplayName();

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
                    setTitle(user.getDisplayName());
                    userRole = documentSnapshot.getLong("userRole").intValue();
                    userPosition = documentSnapshot.getLong("position").intValue();
                    trainingCountText = documentSnapshot.getLong("trainingCount").intValue();
                    if (trainingCountText==1){
                        trainingCount.setText(getResources().getString(R.string.training, Long.toString(trainingCountText)));


                    }else {
                        trainingCount.setText(getResources().getString(R.string.trainings, Long.toString(trainingCountText)));

                    }
                    fieldsPlus = documentSnapshot.getBoolean("fieldsPlus");
                    if (fieldsPlus){
                        if (bp.loadOwnedPurchasesFromGoogle() && bp.isSubscribed("fields_plus")){
                            fields_plus.setVisibility(View.GONE);
                        }else {
                            db.collection("Users").document(uid).update("fieldsPlus", false);
                            fieldsPlus = false;
                        }
                    }else{
                        fields_plus.setVisibility(View.VISIBLE);
                    }
                    realName = documentSnapshot.get("realName").toString();
                    realNameTextView.setText(realName);
                    String userRoleString = userRoleArray[userRole];


                    if (userPosition == -1) {
                        roleText.setText(getResources().getString(R.string.player_position_role_not_given, userRoleString));
                    }else{
                        String userPositionString = userPositionArray[userPosition];
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
                            placeHolder2 = placeHolder + " " + "minutes";
                        }else if (hours<2){
                            placeHolder = hours;
                            placeHolder2 = placeHolder + " " + getResources().getString(R.string.hour);
                        } else if (days < 1) {
                            placeHolder = hours;
                            placeHolder2 = placeHolder + " " + getResources().getString(R.string.hours);
                        }else if (days<2){
                            placeHolder = days;
                            placeHolder2 = placeHolder + " " + getResources().getString(R.string.day);
                        } else {
                            placeHolder = days;
                            placeHolder2 = placeHolder2 + " " + getResources().getString(R.string.days);
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
                                        Intent intent = new Intent(ProfileActivity.this, DetailFieldActivity.class);
                                        intent.putExtra("fieldID", fieldID);
                                        intent.putExtra("fieldName", currentField);
                                        intent.putExtra("fieldArea", fieldArea);
                                        intent.putExtra("fieldAddress", fieldAddress);
                                        intent.putExtra("goalCount", goalCount);
                                        intent.putExtra("fieldType", fieldType);
                                        intent.putExtra("fieldAccessType", accessType);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                                    }
                                });
                            }
                        });
                    } else {
                        testCurrentField.setText(getResources().getString(R.string.not_at_any_field));
                    }

                    if (documentSnapshot.get("usersTeamID") != null && !documentSnapshot.get("usersTeamID").equals("")) {
                            teamID = documentSnapshot.get("usersTeamID").toString();
                            db.collection("Teams").whereEqualTo("teamID", teamID).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                                            UserTeam = ds.get("teamUsernameText").toString();
                                            usersTeam.setText(UserTeam);
                                            teamTab.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startActivity(new Intent(ProfileActivity.this, TeamActivity.class));
                                                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                                                }
                                            });


                                        }
                                    });

                    } else {
                        usersTeam.setText(getResources().getString(R.string.not_at_team));
                    }
                    reputationInt = documentSnapshot.getLong("userReputation").intValue();
                    reputationText.setText(getResources().getString(R.string.reputation, Long.toString(reputationInt)));


                    if (reputationInt < 500) {

                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_bronze_1));
                    } else if (reputationInt < 1500) {
                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_silver_2));
                    } else if (reputationInt < 3000) {

                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_gold_3));
                    } else if (reputationInt < 6000) {

                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_4));
                    } else if (reputationInt < 10000) {

                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_5));
                    } else if (reputationInt < 15000) {

                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_6));
                    } else if (reputationInt < 21000) {

                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_7));
                    } else if (reputationInt < 28000) {

                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_8));
                    } else if (reputationInt < 38000) {

                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_9));
                    } else if (reputationInt < 48000) {

                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_10));
                    } else if (reputationInt < 58000) {

                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_11));
                    } else if (reputationInt < 70000) {

                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_12));
                    } else if (reputationInt < 85000) {

                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_13));
                    } else if (reputationInt >= 85000) {

                        badge_rep.setImageDrawable(getResources().getDrawable(R.drawable.badge_14));
                    }
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
                Intent intent = new Intent(ProfileActivity.this, ReputationActivity.class);
                intent.putExtra("reputation", reputationInt);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                break;
            case R.id.fields_plus:
                startActivity(new Intent(ProfileActivity.this, FieldsPlusStartActivity.class));
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                break;
            case R.id.friends:
                startActivity(new Intent(ProfileActivity.this, FriendListActivity.class));
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                break;
          case R.id.trainings:
                Intent intent1 = new Intent(ProfileActivity.this, AllTrainingsActivity.class);
                intent1.putExtra("fieldsPlus", fieldsPlus);
                startActivity(intent1);
              overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {


    }
}
