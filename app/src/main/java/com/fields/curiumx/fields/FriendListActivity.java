package com.fields.curiumx.fields;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendListActivity extends AppCompatActivity {
    LinearLayoutManager linearLayoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    FirestoreRecyclerAdapter adapter;
    @BindView(R.id.friendRecycler)
    EmptyRecyclerView friendRecycler;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        ButterKnife.bind(this);
        progressBar = findViewById(R.id.progress_bar_friends_list);
        init();
        setTitle(getResources().getString(R.string.friends));
        friendRecycler.setEmptyView(findViewById(R.id.empty_friend_list));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    @Override
    protected void onStart() {
        super.onStart();
        findFriends();

    }

    public void init(){
        linearLayoutManager = new LinearLayoutManager(FriendListActivity.this, LinearLayoutManager.VERTICAL, false);
        friendRecycler.setLayoutManager(linearLayoutManager);
    }

    public class teamHolder extends EmptyRecyclerView.ViewHolder {
        @BindView(R.id.teamPlayerName)
        TextView name;


        public teamHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void findFriends(){

        Query query = db.collection("Users").document(uid).collection("Friends");


        FirestoreRecyclerOptions<FriendMap> response = new FirestoreRecyclerOptions.Builder<FriendMap>()
                .setQuery(query, FriendMap.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<FriendMap, teamHolder>(response) {
            @Override
            public void onBindViewHolder(teamHolder holder, int position, final FriendMap model) {
                holder.name.setText(model.getUserName());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        db.collection("Users").document(uid).collection("Friends").document(model.getUserID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                final DocumentSnapshot ds = task.getResult();
                                db.collection("Users").document(ds.get("userID").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                        DocumentSnapshot ds1 = task1.getResult();
                                        Intent intent = new Intent(getApplicationContext(), DetailUserActivity.class);
                                        intent.putExtra("realName",ds1.get("realName").toString());
                                        intent.putExtra("username", ds1.get("username").toString());
                                        intent.putExtra("userID", ds1.get("userID").toString());
                                        intent.putExtra("currentFieldName", ds1.get("currentFieldName").toString());
                                        intent.putExtra("currentFieldID", ds1.get("currentFieldID").toString());
                                        intent.putExtra("usersTeam", ds1.get("usersTeam").toString());
                                        intent.putExtra("usersTeamID", ds1.get("usersTeamID").toString());
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


                    }
                });
            }

            @Override
            public teamHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.team_player_list, group, false);
                return new teamHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };
        adapter.notifyDataSetChanged();
        friendRecycler.setAdapter(adapter);
        adapter.startListening();


    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        friendRecycler.setAdapter(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
        friendRecycler.setAdapter(null);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        friendRecycler.setAdapter(adapter);
        adapter.startListening();
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
