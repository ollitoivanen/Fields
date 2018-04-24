package com.fields.curiumx.fields;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class DetailTeamActivity extends Activity {

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
    String homeField;
    String countryBoi;

    TextView joinTeam;

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
        setContentView(R.layout.activity_detail_team);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
        joinTeam = findViewById(R.id.join);
        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               final DocumentSnapshot documentSnapshot = task.getResult();
               //Means request is pending
               if (documentSnapshot.get("usersTeam") == null){
                   joinTeam.setVisibility(View.VISIBLE);
                   joinTeam.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           //Can you avoid


                                   String username = documentSnapshot.get("username").toString();
                                   PendingMap pendingMap = new PendingMap(user.getUid(), username);
                                   db.collection("Teams").document(teamID1).collection("Pending Members").document(user.getUid()).set(pendingMap);
                                   db.collection("Users").document(uid).update("usersTeam", "Pending");
                                   joinTeam.setVisibility(View.GONE);
                                   Toast.makeText(getApplicationContext(), "Request sent", Toast.LENGTH_LONG).show();



                       }
                   });
               }else if (documentSnapshot.get("usersTeam").toString().equals("Pending")){
                   joinTeam.setVisibility(View.GONE);
               }
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







        final String username = user.getDisplayName();

        teamLevel  =findViewById(R.id.teamLevel);
        teamTextName = findViewById(R.id.teamName);
        teamTextCountry = findViewById(R.id.teamCountry);
        teamImage = findViewById(R.id.teamImage);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Team");
        progressBar = findViewById(R.id.progress_bar);
        //progressBar.setVisibility(View.VISIBLE);

        Bundle info = getIntent().getExtras();
        teamTextName.setText(info.getString("name"));
        countryBoi = info.getString("country");
        teamTextCountry.setText(info.getString("country"));
        teamLevel.setText(info.getString("level"));
       homeField = info.getString("homefield");
        workStyleDrop.setText(info.getString("workStyle"));
        leaderDrop.setText(info.getString("leader"));
        teamID1 = info.getString("teamID");

        setTitle(info.getString("name"));






        Query query = db.collection("Teams").document(teamID1).collection("Team's Events");

        FirestoreRecyclerOptions<EventMap> response = new FirestoreRecyclerOptions.Builder<EventMap>()
                .setQuery(query, EventMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<EventMap, DetailTeamActivity.eventHolder>(response) {
            @Override
            public void onBindViewHolder(DetailTeamActivity.eventHolder holder, int position, final EventMap model) {

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
                /*holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DetailTeamActivity.this, DetailEventActivity.class);
                        intent.putExtra("type", model.getEventType());
                        intent.putExtra("place", model.getEventField());
                        intent.putExtra("date", model.getEventStartDate());
                        intent.putExtra("timeEnd", model.getEventTimeEnd());
                        intent.putExtra("timeStart", model.getEventTimeStart());
                        intent.putExtra("eventID", model.getEventID());
                        intent.putExtra("teamID", teamID1);
                        startActivity(intent);

                        //I Do not associate with nigus

                    }
                });*/
            }

            @Override
            public DetailTeamActivity.eventHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.event_item, group, false);
                return new DetailTeamActivity.eventHolder(view);
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


        if (homeField.isEmpty()){
            homefieldDrop.setText("Home field: Not set");
        }else {
            homefieldDrop.setText("Home field:" + " " + homeField);
        }

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        eventRecycler.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }
}
