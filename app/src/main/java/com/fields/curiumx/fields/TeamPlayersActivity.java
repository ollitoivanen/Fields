package com.fields.curiumx.fields;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TeamPlayersActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    @BindView(R.id.team_players_recycler)
    EmptyRecyclerView teamPlayersRecycler;
    Query query;
    String teamID;
    int i;


    public class teamPlayerHolder extends EmptyRecyclerView.ViewHolder {
        @BindView(R.id.teamPlayerName)
        TextView teamPlayerName;

        public teamPlayerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
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
        ButterKnife.bind(this);
        TextView empty = findViewById(R.id.emptytext);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle info = getIntent().getExtras();
        teamID = info.getString("teamID");
        i = info.getInt("intentForm");
        teamPlayersRecycler = findViewById(R.id.team_players_recycler);
        teamPlayersRecycler.setEmptyView(empty);
        setTitle(getResources().getString(R.string.players));
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
                        .inflate(R.layout.team_player_list, group, false);

                return new teamPlayerHolder(view);
            }

            @Override
            public void onBindViewHolder (teamPlayerHolder holder, int position, final MemberMap model){
                holder.teamPlayerName.setText(model.getUsernameMember());
            }

            @Override
            public void onError (FirebaseFirestoreException e){
                Log.e("error", e.getMessage());
            }
        };


        adapter.notifyDataSetChanged();
        teamPlayersRecycler.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        teamPlayersRecycler.setAdapter(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
        teamPlayersRecycler.setAdapter(null);
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
