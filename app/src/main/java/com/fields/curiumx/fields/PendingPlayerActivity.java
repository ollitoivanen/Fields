package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PendingPlayerActivity extends AppCompatActivity {
    @BindView(R.id.pending_recycler)
    EmptyRecyclerView pendingRecycler;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String teamID;
    String teamName;


    public void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        pendingRecycler.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }


    public class pendingHolder extends EmptyRecyclerView.ViewHolder {


        @BindView(R.id.pending_player_name)
        TextView pendingPlayerName;
        @BindView(R.id.accept)
        ImageView accept;
        @BindView(R.id.decline)
        ImageView decline;




        public pendingHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_player);
        pendingRecycler = findViewById(R.id.pending_recycler);


init();


        Bundle info = getIntent().getExtras();
        teamID = info.getString("teamID");
        teamName = info.getString("teamName");


        Query query = db.collection("Teams").document(teamID).collection("Pending Members");

        FirestoreRecyclerOptions<PendingMap> response = new FirestoreRecyclerOptions.Builder<PendingMap>()
                .setQuery(query, PendingMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PendingMap, pendingHolder>(response) {
            @Override
            public void onBindViewHolder(pendingHolder holder, int position, final PendingMap model) {
                    holder.pendingPlayerName.setText(model.userNamePending);
                    holder.accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            db.collection("Teams").document(teamID).
                                    collection("Pending Members").document(model.getUserID())
                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    MemberMap memberMap = new MemberMap(model.getUserNamePending(), model.getUserID());


                                    Bundle info = getIntent().getExtras();
                                    teamID = info.getString("teamID");
                                    teamName = info.getString("teamName");
                                    db.collection("Users").document(model.getUserID()).update("usersTeam", teamName);
                                    db.collection("Users").document(model.getUserID()).update("usersTeamID", teamID);

                                    db.collection("Teams").document(teamID)
                                            .collection("TeamUsers").document(model.getUserID())
                                            .set(memberMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Accepted", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });

                    holder.decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            db.collection("Teams").document(teamID).
                                    collection("Pending Members").document(model.getUserID())
                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(),"Declined", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });
            }

            @Override
            public pendingHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.pending_player_list, group, false);
                return new pendingHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        pendingRecycler.setAdapter(adapter);
        adapter.startListening();

    }
}
