package com.fields.curiumx.fields;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class DetailUserActivity extends AppCompatActivity {

    FloatingActionButton chatUser;


    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DocumentReference reference;
    String uid = user.getUid();
    String realName;
    String username;
    String userID;
    String currentFieldID;
    String currentFieldName;
    String usersTeam;
    String usersTeamID;
    int userRole;
    String userReputation;
    int position;
    String userRoleText;
    String userPositionText;
    ProgressBar progressBar;
    long placeHolder;
    String placeHolder2;
    TextView currentField;
    TextView realNameText;
    TextView usersTeamText;
    TextView roleText;
    TextView rept;
    ImageView profileImage;
    ImageView badge_reputation;
    String[] userRoleArray;
    String [] userPositionArray;
    Date timestamp;
    ConstraintLayout gradient;
    ConstraintLayout reputation;
    TextView addFriend;
    TextView removeFriend;
    ConstraintLayout friendContainer;
    ConstraintLayout teamTab;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
                }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        teamTab = findViewById(R.id.team_with_icon_detail);

        profileImage = findViewById(R.id.profilePhoto_detail);
        userRoleArray = getResources().getStringArray(R.array.role_spinner);
        userPositionArray = getResources().getStringArray(R.array.position_spinner);
        badge_reputation = findViewById(R.id.badge_rep_detail);

        chatUser = findViewById(R.id.chat_user);
        friendContainer = findViewById(R.id.friend_container);
        addFriend = findViewById(R.id.add_friend);
        removeFriend = findViewById(R.id.remove_friend);
        roleText = findViewById(R.id.position_role_text_detail);
        currentField = findViewById(R.id.current_field_detail);
        realNameText = findViewById(R.id.real_name_detail);
        usersTeamText = findViewById(R.id.usersTeam_detail);
        rept = findViewById(R.id.reputation_detail);
        progressBar = findViewById(R.id.progress_bar);
        usersTeamText = findViewById(R.id.usersTeam_detail);
        gradient = findViewById(R.id.gradient_detail);
        reputation = findViewById(R.id.rep_detail);

        chatUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailUserActivity.this, UserChatActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
            }
        });


        Bundle info = getIntent().getExtras();
        username = info.getString("username");
        realName = info.getString("realName");
        userID = info.getString("userID");
        currentFieldID = info.getString("currentFieldID");
        currentFieldName = info.getString("currentFieldName");
        usersTeamID = info.getString("usersTeamID");
        userRole = info.getInt("userRole");
        userReputation = info.getString("userReputation");
        position = info.getInt("position");
        if (!currentFieldName.equals("")) {
            timestamp = (Date) info.getSerializable("timestamp");
            Date currentTime = Calendar.getInstance().getTime();
            long diff = currentTime.getTime() - timestamp.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (hours < 1) {
                placeHolder = minutes;
                placeHolder2 = placeHolder + " " + "minutes";
            } else if (hours < 2) {
                placeHolder = hours;
                placeHolder2 = placeHolder + " " + getResources().getString(R.string.hour);
            } else if (days < 1) {
                placeHolder = hours;
                placeHolder2 = placeHolder + " " + getResources().getString(R.string.hours);
            } else if (days < 2) {
                placeHolder = days;
                placeHolder2 = placeHolder + " " + getResources().getString(R.string.day);
            } else {
                placeHolder = days;
                placeHolder2 = placeHolder2 + " " + getResources().getString(R.string.days);
            }
            currentField.setText(getResources().getString(R.string.last_seen_at, currentFieldName, placeHolder2));

            currentField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("Fields").document(currentFieldID).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot ds = task.getResult();
                                    String fieldArea = ds.get("fieldArea").toString();
                                    String fieldAddress = ds.get("fieldAddress").toString();
                                    String goalCount = ds.get("goalCount").toString();
                                    String fieldType = ds.get("fieldType").toString();
                                    String accessType = ds.get("accessType").toString();
                                    Intent intent = new Intent(DetailUserActivity.this, DetailFieldActivity.class);
                                    intent.putExtra("fieldID", currentFieldID);
                                    intent.putExtra("fieldName", currentFieldName);
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
            currentField.setText(getResources().getString(R.string.not_at_any_field));
        }

        long reputationInt = Long.parseLong(userReputation);


        if (reputationInt < 500) {

            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_bronze_1));
        } else if (reputationInt < 1500) {
            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_silver_2));
        } else if (reputationInt < 3000) {

            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_gold_3));
        } else if (reputationInt < 6000) {

            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_4));
        } else if (reputationInt < 10000) {

            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_5));
        } else if (reputationInt < 15000) {

            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_6));
        } else if (reputationInt < 21000) {

            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_7));
        } else if (reputationInt < 28000) {

            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_8));
        } else if (reputationInt < 38000) {

            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_9));
        } else if (reputationInt < 48000) {

            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_10));
        } else if (reputationInt < 58000) {

            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_11));
        } else if (reputationInt < 70000) {

            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_12));
        } else if (reputationInt < 85000) {

            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_13));
        } else if (reputationInt >= 85000) {

            badge_reputation.setImageDrawable(getResources().getDrawable(R.drawable.badge_14));
        }

        if (usersTeamID != null) {
            db.collection("Teams").whereEqualTo("teamID", usersTeamID).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    DocumentSnapshot ds = task.getResult().getDocuments().get(0);
                    usersTeam = ds.get("teamUsernameText").toString();
                    usersTeamText.setText(usersTeam);
                }
            });

            teamTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("Teams").document(usersTeamID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot ds = task.getResult();
                            int level = ds.getLong("level").intValue();
                            String name = ds.get("teamUsernameText").toString();
                            String fullName = ds.get("teamFullNameText").toString();
                            int teamCountry = ds.getLong("teamCountryText").intValue();

                            Intent intent = new Intent(DetailUserActivity.this, DetailTeamActivity.class);
                            intent.putExtra("name", name);
                            intent.putExtra("fullname", fullName);
                            intent.putExtra("country", teamCountry);
                            intent.putExtra("teamID", usersTeamID);
                            intent.putExtra("level", level);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                        }
                    });
                }
            });
        } else {
            usersTeamText.setText(getResources().getString(R.string.not_at_team));
        }

        setTitle(username);

        realNameText.setText(realName);
        rept.setText(getResources().getString(R.string.reputation, userReputation));

        userRoleText = userRoleArray[userRole];
        if (position == -1) {
            roleText.setText(getResources().getString(R.string.player_position_role_not_given, userRoleText));
        } else {
            userPositionText = userPositionArray[position];
            roleText.setText(getResources().getString(R.string.player_position_role_given, userRoleText, userPositionText));
        }

        final StorageReference storageRef = storage.getReference().child("profilepics/" + userID + ".jpg");
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    currentField.setVisibility(View.VISIBLE);
                    gradient.setVisibility(View.VISIBLE);
                    reputation.setVisibility(View.VISIBLE);
                    friendContainer.setVisibility(View.VISIBLE);
                    chatUser.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    GlideApp.with(getApplicationContext())
                            .load(storageRef)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(profileImage);
                } else {
                    profileImage.setImageDrawable(getResources().getDrawable(R.drawable.profile_default));
                    currentField.setVisibility(View.VISIBLE);
                    gradient.setVisibility(View.VISIBLE);
                    reputation.setVisibility(View.VISIBLE);
                    rept.setVisibility(View.VISIBLE);
                    friendContainer.setVisibility(View.VISIBLE);
                    chatUser.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

        db.collection("Users").document(uid).collection("Friends").document(userID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.getResult().exists()){
                    addFriend.setVisibility(View.VISIBLE);
                    removeFriend.setVisibility(View.GONE);
                }else {
                    addFriend.setVisibility(View.GONE);
                    removeFriend.setVisibility(View.VISIBLE);
                }
            }
        });

        removeFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFriendClick();
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendClick();
            }
        });
    }

    public void removeFriendClick(){

        db.collection("Friends").whereEqualTo("adder", uid).whereEqualTo( "target", userID)
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                task.getResult().getDocuments().get(0).getReference().delete();
            }
        });

        progressBar.setVisibility(View.VISIBLE);
        db.collection("Users").document(uid).collection("Friends")
                .document(userID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                removeFriend.setVisibility(View.GONE);
                addFriend.setVisibility(View.VISIBLE);
            }
        });
    }

    public void addFriendClick(){
        final String random = UUID.randomUUID().toString().substring(24);

        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot ds = task.getResult();
                String token = ds.get("token").toString();
                FriendMapTest friendMapTest = new FriendMapTest(uid, userID, token);
                db.collection("Friends").document(random).set(friendMapTest);
            }
        });


        progressBar.setVisibility(View.VISIBLE);
        FriendMap friendMap = new FriendMap(username, userID, user.getDisplayName());
        db.collection("Users").document(uid).collection("Friends")
                .document(userID).set(friendMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                removeFriend.setVisibility(View.VISIBLE);
                addFriend.setVisibility(View.GONE);
            }
        });




    }

    public void onFeedClick(View view) {
        LinearLayout activityBar = findViewById(R.id.activityBar);
        Intent intent = new Intent(this, FeedActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, activityBar, "bar");
        startActivity(intent, options.toBundle());
    }

    public void onProfileClick(View view){
        LinearLayout activityBar = findViewById(R.id.activityBar);
        Intent intent = new Intent(this, ProfileActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, activityBar, "bar");
        startActivity(intent, options.toBundle());


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }
}
