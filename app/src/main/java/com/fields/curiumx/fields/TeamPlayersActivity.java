package com.fields.curiumx.fields;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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



public class TeamPlayersActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    EmptyRecyclerView teamPlayersRecycler;
    Query query;
    String teamID;
    int i;
    StorageReference userImageRef;
    ProgressBar progressBar;
    boolean adapterSet = false;



    public class teamPlayerHolder extends EmptyRecyclerView.ViewHolder {
        TextView teamPlayerName;
        ImageView teamPlayerImage;

        public teamPlayerHolder(View itemView) {
            super(itemView);
                teamPlayerName = itemView.findViewById(R.id.nameTaking);
                teamPlayerImage = itemView.findViewById(R.id.profile_image_list);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setRecycler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_players);
        TextView empty = findViewById(R.id.emptytext);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle info = getIntent().getExtras();
        teamID = info.getString("teamID");
        i = info.getInt("intentForm");
        teamPlayersRecycler = findViewById(R.id.team_players_recycler);
        teamPlayersRecycler.setEmptyView(empty);
        setTitle(getResources().getString(R.string.players));
        progressBar = findViewById(R.id.team_players_progress_bar);
        init();


    }

    public void init(){
        linearLayoutManager = new LinearLayoutManager(TeamPlayersActivity.this, LinearLayoutManager.VERTICAL, false);
        teamPlayersRecycler.setLayoutManager(linearLayoutManager);
    }

    public void setRecycler(){
        init();
        query =  db.collection("Teams").document(teamID).collection("TeamUsers");


        FirestoreRecyclerOptions<MemberMap> response = new FirestoreRecyclerOptions.Builder<MemberMap>()
                .setQuery(query, MemberMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<MemberMap, teamPlayerHolder>(response) {

            @Override
            public teamPlayerHolder onCreateViewHolder (ViewGroup group, int i){
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.user_default_list, group, false);

                return new teamPlayerHolder(view);
            }

            @Override
            public void onBindViewHolder (final teamPlayerHolder holder, int position, final MemberMap model){
                holder.teamPlayerName.setText(model.getUsernameMember());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        db.collection("Users").document(model.getUidMember()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                DocumentSnapshot ds1 = task1.getResult();
                                Intent intent = new Intent(getApplicationContext(), DetailUserActivity.class);
                                intent.putExtra("realName",ds1.get("realName").toString());
                                intent.putExtra("username", ds1.get("username").toString());
                                intent.putExtra("userID", ds1.get("userID").toString());
                                intent.putExtra("currentFieldName", ds1.get("currentFieldName").toString());
                                intent.putExtra("currentFieldID", ds1.get("currentFieldID").toString());
                                if (ds1.get("usersTeamID")!=null) {
                                    intent.putExtra("usersTeamID", ds1.get("usersTeamID").toString());
                                }
                                intent.putExtra("userRole", ds1.getLong("userRole").intValue());
                                intent.putExtra("userReputation", ds1.get("userReputation").toString());
                                intent.putExtra("position", ds1.getLong("position"));
                                if (!ds1.get("currentFieldID").equals("")){
                                    intent.putExtra("timestamp", ds1.getDate("timestamp"));
                                }
                                progressBar.setVisibility(View.GONE);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                    }
                });



                    }
                });

                userImageRef = FirebaseStorage.getInstance()
                        .getReference().child("profilepics/" + model.getUidMember() + "/" + model.getUidMember()+".jpg");
                userImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri uri) {

                            GlideApp.with(getApplicationContext())
                                    .load(uri)
                                    .into(holder.teamPlayerImage);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.teamPlayerImage.setImageDrawable(getResources().getDrawable(R.drawable.profile_default));
                    }
                });
            }

            @Override
            public void onError (FirebaseFirestoreException e){
                Log.e("error", e.getMessage());
            }
        };


        adapterSet = true;
        adapter.notifyDataSetChanged();
        teamPlayersRecycler.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapterSet) {
            adapter.stopListening();
            teamPlayersRecycler.setAdapter(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapterSet) {
            adapter.stopListening();
            teamPlayersRecycler.setAdapter(null);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.startListening();
        teamPlayersRecycler.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
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
}
