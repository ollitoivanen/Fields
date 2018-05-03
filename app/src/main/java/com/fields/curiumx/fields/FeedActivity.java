package com.fields.curiumx.fields;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.ResolvableApiException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FeedActivity extends AppCompatActivity {

    ImageButton feedButton;
    TextView textView;
    Button logoutButton;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    CardView teamCardView;
    CardView teamCardViewNoTeam;
    TextView searchCardView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;









    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(mAuthListener);

        if (mAuth.getCurrentUser()== null){
            finish();
            startActivity(new Intent(FeedActivity.this, SignUpActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        setTitle("Feed");
        feedButton = findViewById(R.id.feed_button);
        feedButton.setImageDrawable(getResources().getDrawable(R.drawable.home_green));




        textView = findViewById(R.id.textview);
        searchCardView = findViewById(R.id.search);
        searchCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedActivity.this, SearchActivity.class));
            }
        });






        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    finish();
                    startActivity(new Intent(FeedActivity.this, SignUpActivity.class));
                }else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    uid = user.getUid();
                    teamCardView = findViewById(R.id.teamCard);
                    teamCardViewNoTeam = findViewById(R.id.teamCardNoTeam);

                    db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            DocumentSnapshot ds = task.getResult();
                            if (ds.get("usersTeam")==null){
                                teamCardViewNoTeam.setVisibility(View.VISIBLE);
                                teamCardViewNoTeam.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(FeedActivity.this, NoTeamActivity.class));
                                        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                                        finish();

                                    }
                                });
                            }else {

                                teamCardView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(FeedActivity.this, TeamActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                                    }
                                });

                            }

                        }
                    });


                }
            }
        };




    }

    @Override
    protected void onRestart() {
        super.onRestart();

        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot ds = task.getResult();
                if (ds.get("usersTeam") == null) {
                    teamCardViewNoTeam.setVisibility(View.VISIBLE);
                    teamCardViewNoTeam.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(FeedActivity.this, NoTeamActivity.class));
                            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                            finish();


                        }
                    });
                } else {

                    teamCardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(FeedActivity.this, TeamActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                        }
                    });

                }
            }
        });
    }

    public void onProfileClick(View view){
        LinearLayout activityBar = findViewById(R.id.activityBar);
        Intent intent = new Intent(this, ProfileActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, activityBar, "bar");
        startActivity(intent, options.toBundle());


    }





}








