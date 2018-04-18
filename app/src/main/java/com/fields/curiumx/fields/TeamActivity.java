package com.fields.curiumx.fields;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class TeamActivity extends Activity {
    String teamName;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    TextView teamLevel;
    TextView teamTextName;
    TextView teamTextCountry;
    ImageView teamImage;
    TextView playerCount;
    ProgressBar progressBar;
    Boolean down1;
    ImageView dropIm1;
    LinearLayout drop2;
    LinearLayout country_map;
    TextView homefieldDrop;
    TextView leaderDrop;
    TextView workStyleDrop;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Users").document(uid);
        playerCount = findViewById(R.id.teamPlayerCount);
        down1 = false;
        dropIm1 = findViewById(R.id.dropdown1);
        drop2 = findViewById(R.id.drop2);
        homefieldDrop = findViewById(R.id.homeFieldText);
        leaderDrop = findViewById(R.id.leader_text);
        workStyleDrop = findViewById(R.id.workstyle_text);
        country_map = findViewById(R.id.country_map);






        dropIm1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!down1) {
                    down1 = true;
                    drop2.setVisibility(View.VISIBLE);
                    drop2.animate().translationY(drop2.getHeight()).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            homefieldDrop.setVisibility(View.VISIBLE);
                            homefieldDrop.setAlpha(0.0f);
                            homefieldDrop.animate().alpha(1.0f);

                            leaderDrop.setVisibility(View.VISIBLE);
                            leaderDrop.setAlpha(0.0f);
                            leaderDrop.animate().alpha(1.0f);

                            workStyleDrop.setVisibility(View.VISIBLE);
                            workStyleDrop.setAlpha(0.0f);
                            workStyleDrop.animate().alpha(1.0f);


                            super.onAnimationEnd(animation);
                        }
                    });
                } else {
                    down1 = false;

                    drop2.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                            homefieldDrop.setVisibility(View.GONE);


                            leaderDrop.setVisibility(View.GONE);


                            workStyleDrop.setVisibility(View.GONE);



                            drop2.setVisibility(View.INVISIBLE);

                            super.onAnimationEnd(animation);

                        }
                    });

                }
            }
        });



        teamLevel  =findViewById(R.id.teamLevel);
        teamTextName = findViewById(R.id.teamName);
        teamTextCountry = findViewById(R.id.teamCountry);
        teamImage = findViewById(R.id.teamImage);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Team");
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                progressBar.setVisibility(View.GONE);
                if (documentSnapshot.get("User's team") == null) {

                    startActivity(new Intent(TeamActivity.this, NoTeamActivity.class));
                    progressBar.setVisibility(View.GONE);

                } else {

                    final String testt = documentSnapshot.get("User's team").toString();

                    db.collection("Teams").whereEqualTo("teamNameText", testt).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            QuerySnapshot querySnapshot = task.getResult();
                            DocumentSnapshot documentSnapshot1 = querySnapshot.getDocuments().get(0);
                            String nameBoi = documentSnapshot1.get("teamNameText").toString();
                            final String countryBoi = documentSnapshot1.get("teamCountryText").toString();
                            String homeField = documentSnapshot1.get("homeField").toString();
                            String leader = documentSnapshot1.get("leader").toString();
                            String level = documentSnapshot1.get("level").toString();
                            String workStyle = documentSnapshot1.get("workStyle").toString();
                            String teamID = documentSnapshot1.get("teamID").toString();
                            teamTextName.setText(nameBoi);
                            teamTextCountry.setText(countryBoi);

                            db.collection("Teams").document(teamID).collection("TeamUsers").get();
                            int amountOfPlayers = task.getResult().size();
                            if (task.getResult().size()==1){
                                playerCount.setText(Integer.toString(amountOfPlayers) + " " + "Player");
                            }else {
                                playerCount.setText(Integer.toString(amountOfPlayers) + " " + "Players");
                            }

                            teamLevel.setText("Level:" + " " + level);
                            if (homeField.isEmpty()){
                                homefieldDrop.setText("Home field: Not set");
                            }else {
                                homefieldDrop.setText("Home field:" + " " + homeField);
                                }
                            leaderDrop.setText("Leader:" + " " + leader);
                            workStyleDrop.setText("Work style:" + " " + workStyle);

                            country_map.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.setPackage("com.google.android.apps.maps");
                                    intent.setData(Uri.parse("https://www.google.com/maps/search/?api=1&query=" + countryBoi));
                                    startActivity(intent);
                                }
                            });

                            final StorageReference storageRef = storage.getReference().child("teampics/"+teamID+".jpg");
                            storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()){
                                        Glide.with(getApplicationContext())
                                                .load(storageRef)
                                                .into(teamImage);
                                    }else {
                                        teamImage.setImageDrawable(getResources().getDrawable(R.drawable.field_photo3));
                                    }

                                }
                            });

                        }
                    });


                }
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
