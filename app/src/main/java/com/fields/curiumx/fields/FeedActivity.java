package com.fields.curiumx.fields;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FeedActivity extends AppCompatActivity {

    ImageButton feedButton;
    TextView textView;
    Button logoutButton;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    CardView teamCardView;
    CardView teamCardViewNoTeam;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressBar progressBar;
    StorageReference teamImageRef;
    StorageReference userImageRef;
    boolean adapterSet = false;
    TextView yourTeamText;
    ConstraintLayout base;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    EmptyRecyclerView feedRecycler;
    LinearLayoutManager linearLayoutManager;
    FirestoreRecyclerAdapter adapter;
    String uid;

    public class feedHolder extends EmptyRecyclerView.ViewHolder {
        TextView currentField;
        ImageView userPhoto;
        TextView username;


        public feedHolder(View itemView) {
            super(itemView);
            currentField = itemView.findViewById(R.id.currently_at_feed);
            userPhoto = itemView.findViewById(R.id.user_image_feed);
            username = itemView.findViewById(R.id.user_name_feed);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                startActivity(new Intent(FeedActivity.this, SearchActivity.class));
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void loadData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();


        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.GONE);
                yourTeamText.setVisibility(View.VISIBLE);
                DocumentSnapshot ds = task.getResult();
                if (ds.get("usersTeam")==null){
                    ConstraintSet constraintSet = new ConstraintSet();
                    base = findViewById(R.id.base_activity);
                    constraintSet.clone(base);
                    constraintSet.connect(R.id.actions_text,ConstraintSet.TOP,R.id.teamCardNoTeam,ConstraintSet.BOTTOM,12);
                    constraintSet.applyTo(base);

                    teamCardViewNoTeam.setVisibility(View.VISIBLE);
                    feedRecycler.setVisibility(View.VISIBLE);
                    teamCardViewNoTeam.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(FeedActivity.this, NoTeamActivity.class));
                            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                            finish();

                        }
                    });
                }else {

                    teamCardView.setVisibility(View.VISIBLE);
                    feedRecycler.setVisibility(View.VISIBLE);
                    String teamName = ds.get("usersTeam").toString();
                    String teamID = ds.get("usersTeamID").toString();
                    final ImageView teamImageFeed = findViewById(R.id.team_image_feed);

                    teamImageRef = FirebaseStorage.getInstance()
                            .getReference().child("teampics/"+teamID+".jpg");
                    teamImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                GlideApp.with(getApplicationContext())
                                        .load(teamImageRef)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(teamImageFeed);
                            } else {
                                teamImageFeed.setImageDrawable(getResources().getDrawable(R.drawable.team_basic));

                            }
                        }
                    });

                    TextView teamNameFeed = findViewById(R.id.team_name_feed);
                    teamNameFeed.setText(teamName);
                    teamCardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(FeedActivity.this, TeamActivity.class);

                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                        }
                    });

                }

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        setTitle(getResources().getString(R.string.feed));
        feedRecycler = findViewById(R.id.actions_recycler);
        progressBar = findViewById(R.id.progress_bar_feed);
        teamCardView = findViewById(R.id.teamCard);
        teamCardViewNoTeam = findViewById(R.id.teamCardNoTeam);
        yourTeamText = findViewById(R.id.your_team_text);
        init();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()==null){
            startActivity(new Intent(FeedActivity.this, SignUpActivity.class));
            finish();
        }else {
            loadData();
            setRecycler();

        }


        feedButton = findViewById(R.id.feed_button);
        feedButton.setImageDrawable(getResources().getDrawable(R.drawable.home_green));
        textView = findViewById(R.id.textview);

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser()==null){
            startActivity(new Intent(FeedActivity.this, SignUpActivity.class));
            finish();
        }else {
            loadData();
            feedRecycler.setAdapter(adapter);
            adapter.startListening();
        }

    }

    public void onProfileClick(View view){
        LinearLayout activityBar = findViewById(R.id.activityBar);
        Intent intent = new Intent(this, ProfileActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, activityBar, "bar");
        startActivity(intent, options.toBundle());


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

    public void setRecycler() {

        Query query = db.collection("Users").document(uid).collection("Friends");

        FirestoreRecyclerOptions<FriendMap> response = new FirestoreRecyclerOptions.Builder<FriendMap>()
                .setQuery(query, FriendMap.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<FriendMap, feedHolder>(response) {
            @Override
            public void onBindViewHolder(final feedHolder holder, int position, final FriendMap model) {

                db.collection("Users").document(model.getUserID()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            holder.username.setText(model.getUserName());

                            userImageRef = FirebaseStorage.getInstance()
                                    .getReference().child("userpics/"+model.getUserID()+".jpg");
                            userImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        GlideApp.with(getApplicationContext())
                                                .load(userImageRef)
                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .skipMemoryCache(true)
                                                .into(holder.userPhoto);
                                    } else {
                                        holder.userPhoto.setImageDrawable(getResources().
                                                getDrawable(R.drawable.team_basic));

                                    }
                                }
                            });

                        String currentField = task.getResult().get("currentFieldName").toString();
                        if (!currentField.equals("")) {
                            holder.currentField.setText(currentField);
                        }else {
                            holder.currentField.setText(getResources().getString(R.string.not_at_any_field));
                        }
                    }else {
                            Snackbar.make(findViewById(R.id.team_activity),
                                    getResources()
                                    .getString(R.string.error_occurred_loading_document),
                                    Snackbar.LENGTH_LONG).show();

                        }

                    }
                });

               /* holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        db.collection("Users").document(uid).collection("Friends")
                                .document(model.getUserID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                final DocumentSnapshot ds = task.getResult();
                                db.collection("Users").document(ds.get("userID").toString())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                });*/
            }

            @Override
            public feedHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.feed_list, group, false);
                return new feedHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };
        adapterSet = true;
        adapter.notifyDataSetChanged();
        feedRecycler.setAdapter(adapter);
        adapter.startListening();





    }
    public void init(){
        linearLayoutManager = new LinearLayoutManager(FeedActivity.this, LinearLayoutManager.VERTICAL, false);
        feedRecycler.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapterSet) {
            adapter.stopListening();
            feedRecycler.setAdapter(null);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapterSet) {
            adapter.stopListening();

            feedRecycler.setAdapter(null);
        }
    }
}








