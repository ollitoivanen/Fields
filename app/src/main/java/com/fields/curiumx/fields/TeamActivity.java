package com.fields.curiumx.fields;


import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TeamActivity extends AppCompatActivity implements View.OnClickListener{
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    String teamName;
    int teamCountry;
    String teamFullName;
    TextView teamLevel;
    TextView teamTextName;
    TextView teamTextCountry;
    TextView teamEventsText;
    ImageView teamImage;
    ImageView addEventImage;
    TextView playerCount;
    ProgressBar progressBar;
    ConstraintLayout emptyConstraint;
    LinearLayout country_map;
    ConstraintLayout basicInfoCont;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    EmptyRecyclerView eventRecycler;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String teamID1;
    int level;
    String teamLevelString;
    String[] teamLevelArray;
    String[] teamCountryArray;
    String eventTypeString;
    String[] eventArray;
    String teamCountryString;
    FloatingActionButton chatFloat;
    StorageReference teamImageRef;
    Boolean teamFieldsPlus;
    boolean adapterSet = false;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pending_players, menu);
        inflater.inflate(R.menu.edit_team, menu);
        inflater.inflate(R.menu.leave_team, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private class eventHolder extends EmptyRecyclerView.ViewHolder {

        TextView textDate;
        TextView textTime;
        TextView textPlace;
        TextView textType;



        private eventHolder(View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.eventDate);
            textTime = itemView.findViewById(R.id.eventTime);
            textPlace = itemView.findViewById(R.id.eventPlace);
            textType = itemView.findViewById(R.id.eventType);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);



        teamCountryArray = getResources().getStringArray(R.array.country_list);
        chatFloat = findViewById(R.id.chat_float);
        final DocumentReference docRef = db.collection("Users").document(uid);
        playerCount = findViewById(R.id.teamPlayerCount);
        country_map = findViewById(R.id.country_map);
        teamEventsText = findViewById(R.id.events_text);
        basicInfoCont = findViewById(R.id.basicInfoContainer);
        addEventImage = findViewById(R.id.add_event_image);
        teamLevel = findViewById(R.id.teamLevel);
        teamTextName = findViewById(R.id.teamName);
        teamTextCountry = findViewById(R.id.teamCountry);
        teamImage = findViewById(R.id.teamImage);
        progressBar = findViewById(R.id.progress_bar_team);
        eventRecycler = findViewById(R.id.eventRecycler);
        emptyConstraint = findViewById(R.id.empty_constraint);
        playerCount.setOnClickListener(this);
        addEventImage.setOnClickListener(this);
        chatFloat.setOnClickListener(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.team));
        init();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();

                teamID1 = documentSnapshot.get("usersTeamID").toString();
                db.collection("Teams").document(teamID1).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        Query query = db.collection("Teams").document(teamID1).collection("Team's Events");

                        final FirestoreRecyclerOptions<EventMap> response = new FirestoreRecyclerOptions.Builder<EventMap>()
                                .setQuery(query, EventMap.class)
                                .build();

                        adapter = new FirestoreRecyclerAdapter<EventMap, eventHolder>(response) {
                            @Override
                            public void onBindViewHolder(@NonNull eventHolder holder, int position, @NonNull final EventMap model) {

                              if (model.getEventStartDateInMillis() - System.currentTimeMillis() < 0) {

                                    db.collection("Teams").document(teamID1)
                                            .collection("Team's Events")
                                            .document(model.getEventID()).delete();
                                    if (!model.getEventField().equals("")){
                                        db.collection("Fields").document(model.getEventID())
                                                .collection("fieldEvents").document(model.getEventID()).delete();
                                    }

                                } else {
                                    eventArray = getResources().getStringArray(R.array.event_type_array);
                                    eventTypeString = eventArray[model.getEventType()];
                                    holder.textType.setText(eventTypeString);

                                    Date dateSave = model.getEventStartDate();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
                                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTF"));
                                    final String date = dateFormat.format(dateSave);
                                    holder.textDate.setText(date);

                                    if (!model.getEventField().equals("")) {
                                        holder.textPlace.setText(getResources().getString(R.string.event_field, model.getEventField()));
                                    } else holder.textPlace.setVisibility(View.GONE);
                                    holder.textTime.setText(getResources().getString(R.string.training_time, model.getEventTimeStart(), model.getEventTimeEnd()));
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(TeamActivity.this, DetailEventActivity.class);
                                            intent.putExtra("type", eventTypeString);
                                            intent.putExtra("place", model.getEventField());
                                            intent.putExtra("date", date);
                                            intent.putExtra("timeEnd", model.getEventTimeEnd());
                                            intent.putExtra("timeStart", model.getEventTimeStart());
                                            intent.putExtra("eventID", model.getEventID());
                                            intent.putExtra("teamID", teamID1);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                                        }
                                    });
                                }
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
                        adapterSet = true;

                        DocumentSnapshot documentSnapshot1 = task.getResult();
                        teamName = documentSnapshot1.get("teamUsernameText").toString();
                        teamCountry = documentSnapshot1.getLong("teamCountryText").intValue();
                        teamFullName = documentSnapshot1.get("teamFullNameText").toString();
                        level = documentSnapshot1.getLong("level").intValue();
                        db.collection("Teams").document(teamID1).collection("TeamUsers")
                                .whereEqualTo("memberFieldsPlus", true).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().size()==0){
                                    teamFieldsPlus = false;
                                }else {
                                    teamFieldsPlus = true;
                                }

                                teamLevelArray = getResources().getStringArray(R.array.level_array);
                                teamLevelString = teamLevelArray[level];
                                teamCountryString = teamCountryArray[teamCountry];
                                setTitle(teamName);
                                teamTextName.setText(teamFullName);
                                teamTextCountry.setText(teamCountryString);
                                teamLevel.setText(teamLevelString);

                                progressBar.setVisibility(View.GONE);
                                basicInfoCont.setVisibility(View.VISIBLE);
                                addEventImage.setVisibility(View.VISIBLE);
                                teamEventsText.setVisibility(View.VISIBLE);
                                eventRecycler.setEmptyView(emptyConstraint);
                                eventRecycler.setVisibility(View.VISIBLE);
                                chatFloat.setVisibility(View.VISIBLE);

                                teamImageRef = FirebaseStorage.getInstance()
                                            .getReference().child("teampics/"+teamID1+".jpg");
                                    teamImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                GlideApp.with(getApplicationContext())
                                                        .load(teamImageRef)
                                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                        .skipMemoryCache(true)
                                                        .into(teamImage);
                                            } else {
                                                teamImage.setImageDrawable(getResources().getDrawable(R.drawable.team_default));

                                            }
                                        }
                                    });

                                    country_map.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setPackage("com.google.android.apps.maps");
                                        intent.setData(Uri.parse("https://www.google.com/maps/search/?api=1&query=" + teamCountry));
                                        startActivity(intent);
                                        adapter.stopListening();
                                    }
                                });
                            }
                                });
                    }
                });
                }else{
                    Snackbar.make(findViewById(R.id.team_activity), getResources()
                            .getString(R.string.error_occurred_loading_document), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapterSet) {
            adapter.stopListening();
            eventRecycler.setAdapter(null);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapterSet) {
            adapter.stopListening();

            eventRecycler.setAdapter(null);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.startListening();
        eventRecycler.setAdapter(adapter);
        teamImageRef =
                FirebaseStorage.getInstance()
                        .getReference().child("teampics/" + teamID1 + ".jpg");
        teamImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {


                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    GlideApp.with(getApplicationContext())
                            .load(teamImageRef)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(teamImage);
                } else {
                    teamImage.setImageDrawable(getResources().getDrawable(R.drawable.team_default));
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
                onBackPressed();
                finish();
                return true;
            case R.id.leave:

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeamActivity.this );
                View mView = getLayoutInflater().inflate(R.layout.leave_team_popup, null);
                Button leaveButton = mView.findViewById(R.id.leave);
                leaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection("Users").document(uid).update("usersTeamID", null);
                        db.collection("Teams").document(teamID1).collection("TeamUsers").document(uid).delete();
                        db.collection("Users").document(uid).update("usersTeam", null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(TeamActivity.this, NoTeamActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                Toast.makeText(getApplicationContext(), "Left team", Toast.LENGTH_LONG).show();
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                                finish();
                            }



                        });
                    }
                });


                alertDialog.setView(mView);
                final AlertDialog dialog = alertDialog.create();
                dialog.show();

                Button stayButton = mView.findViewById(R.id.stay);
                stayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        }
                });




                break;

            case R.id.pending_player:
                Intent intent = new Intent(TeamActivity.this, PendingPlayerActivity.class);
                intent.putExtra("teamID", teamID1);
                intent.putExtra("teamName", teamName);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                break;



            case R.id.edit_team:
                Intent intent1 = new Intent(TeamActivity.this, EditTeamActivity.class);
                intent1.putExtra("teamID", teamID1);
                intent1.putExtra("teamName", teamName);
                intent1.putExtra("teamCountry", teamCountry);
                intent1.putExtra("teamFullName", teamFullName);
                intent1.putExtra("level", level);
                startActivity(intent1);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.teamPlayerCount:
                int i = 1;
                Intent intent = new Intent(TeamActivity.this, TeamPlayersActivity.class);
                intent.putExtra("teamID", teamID1);
                intent.putExtra("intentForm", i);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);


                break;
            case R.id.add_event_image:
                Intent intent2 = new Intent(TeamActivity.this, NewEventActivity.class);
                intent2.putExtra("teamID", teamID1);
                startActivity(intent2);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                break;

            case R.id.chat_float:
                Intent intent1 = new Intent(TeamActivity.this, TeamChatActivity.class);
                intent1.putExtra("teamFieldsPlus", teamFieldsPlus);
                intent1.putExtra("teamID", teamID1);
                startActivity(intent1);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                break;

        }
    }

    public void onProfileClick(View view) {
        LinearLayout activityBar = findViewById(R.id.activityBar);
        Intent intent = new Intent(this, ProfileActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, activityBar, "bar");
        startActivity(intent, options.toBundle());
        adapter.stopListening();
    }

    public void onFeedClick(View view){
        LinearLayout activityBar = findViewById(R.id.activityBar);
        Intent intent = new Intent(this, FeedActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, activityBar, "bar");
        startActivity(intent, options.toBundle());
        adapter.stopListening();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }
}
