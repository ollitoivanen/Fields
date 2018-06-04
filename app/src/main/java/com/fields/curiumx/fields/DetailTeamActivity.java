package com.fields.curiumx.fields;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



public class DetailTeamActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    TextView teamLevel;
    TextView teamTextName;
    TextView teamTextCountry;
    ImageView teamImage;
    TextView playerCount;
    ProgressBar progressBar;
    String eventTypeString;
    String[] eventArray;
    LinearLayout country_map;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    EmptyRecyclerView eventRecycler;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String teamID1;
    String teamFullName;
    int teamCountry;
    int teamLevelInt;
    TextView joinTeam;
    String [] countryArray;
    int i;

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
        setContentView(R.layout.activity_detail_team);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        playerCount = findViewById(R.id.teamPlayerCount);
        eventRecycler = findViewById(R.id.eventRecycler);
        eventRecycler.setEmptyView(findViewById(R.id.empty_constraint));
        init();
        playerCount = findViewById(R.id.teamPlayerCount);
        playerCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 2;
                Intent intent = new Intent(DetailTeamActivity.this, TeamPlayersActivity.class);
                intent.putExtra("teamID", teamID1);
                intent.putExtra("intentForm", i);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
            }
        });


        country_map = findViewById(R.id.country_map);
        joinTeam = findViewById(R.id.join);
        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               final DocumentSnapshot documentSnapshot = task.getResult();
               if (documentSnapshot.get("usersTeamID") == null || documentSnapshot.get("usersTeamID").equals("")){
                   joinTeam.setVisibility(View.VISIBLE);
                   joinTeam.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {



                                   String username = documentSnapshot.get("username").toString();
                                   Boolean fieldsPlus = documentSnapshot.getBoolean("fieldsPlus");
                                   PendingMap pendingMap = new PendingMap(user.getUid(), username, fieldsPlus);
                                   db.collection("Teams").document(teamID1).collection("pendingMembers").document(user.getUid()).set(pendingMap);
                                   db.collection("Users").document(uid).update("usersTeamID", "");
                                   joinTeam.setVisibility(View.GONE);
                           Snackbar.make(findViewById(R.id.detaila), getResources().getString(R.string.request_sent), Snackbar.LENGTH_LONG).show();



                       }
                   });
               }
            }
        });


        teamLevel  =findViewById(R.id.teamLevel);
        teamTextName = findViewById(R.id.teamName);
        teamTextCountry = findViewById(R.id.teamCountry);
        teamImage = findViewById(R.id.teamImage);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progress_bar);
        //progressBar.setVisibility(View.VISIBLE);

        countryArray = getResources().getStringArray(R.array.country_list);
        String [] levelArray = getResources().getStringArray(R.array.level_array);

        Bundle info = getIntent().getExtras();
        teamCountry = info.getInt("country");
        teamTextCountry.setText(countryArray[teamCountry]);
        teamLevelInt = info.getInt("level");
        teamLevel.setText(levelArray[teamLevelInt]);
        teamFullName = info.getString("fullname");
        teamTextName.setText(teamFullName);


        teamID1 = info.getString("teamID");

        setTitle(info.getString("name"));






        Query query = db.collection("Teams").document(teamID1).collection("Team's Events");

        FirestoreRecyclerOptions<EventMap> response = new FirestoreRecyclerOptions.Builder<EventMap>()
                .setQuery(query, EventMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<EventMap, DetailTeamActivity.eventHolder>(response) {
            @Override
            public void onBindViewHolder(DetailTeamActivity.eventHolder holder, int position, final EventMap model) {


                eventArray = getResources().getStringArray(R.array.event_type_array);
                eventTypeString = eventArray[model.getEventType()];
                holder.textType.setText(eventTypeString);
                Date dateSave = model.getEventStartDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTF"));
                String date = dateFormat.format(dateSave);
                holder.textDate.setText(date);

                if (!model.getEventField().equals("")) {
                    holder.textPlace.setText(getResources().getString(R.string.event_field, model.getEventField()));
                } else holder.textPlace.setVisibility(View.GONE);
                holder.textTime.setText(getResources().getString(R.string.training_time, model.getEventTimeStart(), model.getEventTimeEnd()));

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


        country_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setPackage("com.google.android.apps.maps");
                intent.setData(Uri.parse("https://www.google.com/maps/search/?api=1&query=" + countryArray[teamCountry]));
                startActivity(intent);
            }
        });

        final StorageReference storageRef = storage.getReference().child("teampics/"+teamID1+".jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                    GlideApp.with(getApplicationContext())
                            .load(uri)

                            .into(teamImage);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                teamImage.setImageDrawable(getResources().getDrawable(R.drawable.team_default));
            }
        });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        eventRecycler.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        eventRecycler.setAdapter(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
        eventRecycler.setAdapter(null);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        eventRecycler.setAdapter(adapter);
        adapter.startListening();
    }
}
