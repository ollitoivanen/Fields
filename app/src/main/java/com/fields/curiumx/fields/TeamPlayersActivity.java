package com.fields.curiumx.fields;

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
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

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
                        .inflate(R.layout.taking_part_list, group, false);

                return new teamPlayerHolder(view);
            }

            @Override
            public void onBindViewHolder (final teamPlayerHolder holder, int position, final MemberMap model){
                holder.teamPlayerName.setText(model.getUsernameMember());

                userImageRef = FirebaseStorage.getInstance()
                        .getReference().child("profilepics/"+model.getUidMember()+".jpg");
                userImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri uri) {

                            GlideApp.with(getApplicationContext())
                                    .load(uri)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
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
