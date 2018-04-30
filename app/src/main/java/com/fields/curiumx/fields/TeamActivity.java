package com.fields.curiumx.fields;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.firebase.ui.auth.ui.phone.SubmitConfirmationCodeFragment.TAG;

public class TeamActivity extends Activity implements View.OnClickListener{
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    String teamName;
    String teamCountry;
    TextView teamLevel;
    TextView teamTextName;
    TextView teamTextCountry;
    TextView teamEventsText;
    ImageView teamImage;
    ImageView addEventImage;
    TextView playerCount;
    ProgressBar progressBar;
    TextView emptyText;
    LinearLayout country_map;
    ConstraintLayout basicInfoCont;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    @BindView(R.id.eventRecycler)
    EmptyRecyclerView eventRecycler;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String teamID1;
    int level;
    String teamLevelString;
    String[] teamLevelArray;
    String eventTypeString;
    String[] eventArray;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_team, menu);
        inflater.inflate(R.menu.pending_players, menu);
        inflater.inflate(R.menu.edit_team, menu);
        inflater.inflate(R.menu.leave_team, menu);

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
        country_map = findViewById(R.id.country_map);
        teamEventsText = findViewById(R.id.events_text);
        basicInfoCont = findViewById(R.id.basicInfoContainer);
        addEventImage = findViewById(R.id.add_event_image);
        teamLevel  =findViewById(R.id.teamLevel);
        teamTextName = findViewById(R.id.teamName);
        teamTextCountry = findViewById(R.id.teamCountry);
        teamImage = findViewById(R.id.teamImage);
        progressBar = findViewById(R.id.progress_bar);
        eventRecycler = findViewById(R.id.eventRecycler);
        emptyText = findViewById(R.id.empty_text);
        playerCount.setOnClickListener(this);
        addEventImage.setOnClickListener(this);
        eventRecycler.setEmptyView(emptyText);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Team");
        init();
        ButterKnife.bind(this);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                progressBar.setVisibility(View.GONE);
                if (documentSnapshot.get("usersTeam") == null) {

                    startActivity(new Intent(TeamActivity.this, NoTeamActivity.class));
                    progressBar.setVisibility(View.GONE);
                    finish();

                } else {

                    teamID1 = documentSnapshot.get("usersTeamID").toString();
                    db.collection("Teams").document(teamID1).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            Query query = db.collection("Teams").document(teamID1).collection("Team's Events");

                            FirestoreRecyclerOptions<EventMap> response = new FirestoreRecyclerOptions.Builder<EventMap>()
                                    .setQuery(query, EventMap.class)
                                    .build();

                            adapter = new FirestoreRecyclerAdapter<EventMap, eventHolder>(response) {
                                @Override
                                public void onBindViewHolder(@NonNull eventHolder holder, int position, @NonNull final EventMap model) {

                                    eventArray = getResources().getStringArray(R.array.event_type_array);
                                    eventTypeString = eventArray[model.getEventType()];
                                    holder.textType.setText(eventTypeString);

                                    String dateSave = model.eventStartDate;
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM", Locale.US);
                                    try {
                                        Date dateSaveToDate = dateFormat.parse(dateSave);
                                        SimpleDateFormat timeFormat = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
                                        String finalDateSave = timeFormat.format(dateSaveToDate);
                                        holder.textDate.setText(finalDateSave);
                                        } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if (!model.getEventField().equals("")) {
                                        holder.textPlace.setText(getResources().getString(R.string.event_field, model.getEventField()));
                                    }else  holder.textPlace.setVisibility(View.GONE);
                                    holder.textTime.setText(getResources().getString(R.string.training_time, model.getEventTimeStart(), model.getEventTimeEnd()));
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(TeamActivity.this, DetailEventActivity.class);
                                            intent.putExtra("type", eventTypeString);
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

                            DocumentSnapshot documentSnapshot1 = task.getResult();
                            teamName = documentSnapshot1.get("teamNameText").toString();
                            teamCountry = documentSnapshot1.get("teamCountryText").toString();
                            level = documentSnapshot1.getLong("level").intValue();
                            teamLevelArray = getResources().getStringArray(R.array.level_array);
                            teamLevelString = teamLevelArray[level];
                            teamTextName.setText(teamName);
                            teamTextCountry.setText(teamCountry);
                            teamLevel.setText(teamLevelString);
                            basicInfoCont.setVisibility(View.VISIBLE);
                            addEventImage.setVisibility(View.VISIBLE);
                            teamEventsText.setVisibility(View.VISIBLE);
                            eventRecycler.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                            country_map.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.setPackage("com.google.android.apps.maps");
                                    intent.setData(Uri.parse("https://www.google.com/maps/search/?api=1&query=" + teamCountry));
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
                        finish();
                    }
                });
                break;

            case R.id.pending_player:
                Intent intent = new Intent(TeamActivity.this, PendingPlayerActivity.class);
                intent.putExtra("teamID", teamID1);
                intent.putExtra("teamName", teamName);
                startActivity(intent);
                break;

            case R.id.chat_team:
                startActivity(new Intent(TeamActivity.this, TeamChatActivity.class));
                break;

            case R.id.edit_team:
                Intent intent1 = new Intent(TeamActivity.this, EditTeamActivity.class);
                intent1.putExtra("teamID", teamID1);
                intent1.putExtra("teamName", teamName);
                intent1.putExtra("teamCountry", teamCountry);
                intent1.putExtra("level", level);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.teamPlayerCount:
                startActivity(new Intent(TeamActivity.this, PlayerCountActivity.class));
                break;
            case R.id.add_event_image:
                startActivity(new Intent(TeamActivity.this, NewEventActivity.class));
                break;

        }
    }
}
