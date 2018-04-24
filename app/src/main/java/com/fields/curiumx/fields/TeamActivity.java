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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeamActivity extends Activity {
    String nameBoi;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    TextView teamLevel;
    TextView teamTextName;
    TextView teamTextCountry;
    ImageView teamImage;
    ImageView addEventImage;
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
    @BindView(R.id.eventRecycler)
    EmptyRecyclerView eventRecycler;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String teamID1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.leave_team, menu);
        inflater.inflate(R.menu.pending_players, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public class eventHolder extends EmptyRecyclerView.ViewHolder {

        CardView root = findViewById(R.id.root);
        @BindView(R.id.eventDate)
        TextView textDate;
        @BindView(R.id.eventTime)
        TextView textTime;
        @BindView(R.id.eventPlace)
        TextView textPlace;
        @BindView(R.id.eventType)
        TextView textType;



        public eventHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        final DocumentReference docRef = db.collection("Users").document(uid);
        playerCount = findViewById(R.id.teamPlayerCount);
        down1 = false;
        eventRecycler = findViewById(R.id.eventRecycler);
        init();



        ButterKnife.bind(this);
        dropIm1 = findViewById(R.id.dropdown1);
        drop2 = findViewById(R.id.drop2);
        homefieldDrop = findViewById(R.id.homeFieldText);
        leaderDrop = findViewById(R.id.leader_text);
        workStyleDrop = findViewById(R.id.workstyle_text);
        country_map = findViewById(R.id.country_map);
        addEventImage = findViewById(R.id.add_event_image);
        addEventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeamActivity.this, NewEventActivity.class));
            }
        });


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
                if (documentSnapshot.get("usersTeam") == null) {

                    startActivity(new Intent(TeamActivity.this, NoTeamActivity.class));
                    progressBar.setVisibility(View.GONE);

                } else {

                     teamID1 = documentSnapshot.get("usersTeamID").toString();

                    db.collection("Teams").document(teamID1).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            DocumentSnapshot documentSnapshot1 = task.getResult();
                            nameBoi = documentSnapshot1.get("teamNameText").toString();
                            final String countryBoi = documentSnapshot1.get("teamCountryText").toString();
                            String homeField = documentSnapshot1.get("homeField").toString();
                            String leader = documentSnapshot1.get("leader").toString();
                            String level = documentSnapshot1.get("level").toString();
                            String workStyle = documentSnapshot1.get("workStyle").toString();
                            teamTextName.setText(nameBoi);
                            teamTextCountry.setText(countryBoi);



                            Query query = db.collection("Teams").document(teamID1).collection("Team's Events");

                            FirestoreRecyclerOptions<EventMap> response = new FirestoreRecyclerOptions.Builder<EventMap>()
                                    .setQuery(query, EventMap.class)
                                    .build();

                            adapter = new FirestoreRecyclerAdapter<EventMap, eventHolder>(response) {
                                @Override
                                public void onBindViewHolder(eventHolder holder, int position, final EventMap model) {

                                    Date currentTime = Calendar.getInstance().getTime();





                                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM");
                                    String compareDate = dateFormat.format(currentTime);
                                    if (compareDate.equals(model.getEventStartDate())){
                                        holder.textDate.setText("Today");
                                    }else {
                                        holder.textDate.setText(model.getEventStartDate());
                                    }
                                    holder.textType.setText(model.getEventType());
                                    holder.textTime.setText(model.getEventTimeStart() + "- " + model.getEventTimeEnd());
                                    if (!model.getEventField().equals("")) {
                                        holder.textPlace.setText("at " + model.getEventField());
                                    }
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(TeamActivity.this, DetailEventActivity.class);
                                            intent.putExtra("type", model.getEventType());
                                            intent.putExtra("place", model.getEventField());
                                            intent.putExtra("date", model.getEventStartDate());
                                            intent.putExtra("timeEnd", model.getEventTimeEnd());
                                            intent.putExtra("timeStart", model.getEventTimeStart());
                                            intent.putExtra("eventID", model.getEventID());
                                            intent.putExtra("teamID", teamID1);
                                            startActivity(intent);

                                        }
                                    });
                                }

                                @Override
                                public eventHolder onCreateViewHolder(ViewGroup group, int i) {
                                    View view = LayoutInflater.from(group.getContext())
                                            .inflate(R.layout.event_item, group, false);
                                    return new eventHolder(view);
                                }

                                @Override
                                public void onError(FirebaseFirestoreException e) {
                                    Log.e("error", e.getMessage());
                                }
                            };

                            adapter.notifyDataSetChanged();
                            eventRecycler.setAdapter(adapter);
                            adapter.startListening();


                            db.collection("Teams").document(teamID1).collection("TeamUsers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int amountOfPlayers = task.getResult().size();
                                    if (task.getResult().size()==1){
                                        playerCount.setText(Integer.toString(amountOfPlayers) + " " + "Player");
                                    }else {
                                        playerCount.setText(Integer.toString(amountOfPlayers) + " " + "Players");
                                    }
                                }
                            });


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

                            final StorageReference storageRef = storage.getReference().child("teampics/"+teamID1+".jpg");
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
    public void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        eventRecycler.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.leave:
                db.collection("Users").document(uid).update("usersTeamID", null);
                db.collection("Teams").document(teamID1).collection("TeamUsers").document(uid).delete();
                db.collection("Users").document(uid).update("usersTeam", null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(TeamActivity.this, NoTeamActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Toast.makeText(getApplicationContext(), "Left team", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }
                });

            case R.id.pending_player:
                Intent intent = new Intent(TeamActivity.this, PendingPlayerActivity.class);
                intent.putExtra("teamID", teamID1);
                intent.putExtra("teamName", nameBoi);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
