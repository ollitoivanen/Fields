package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendListActivity extends Activity {
    LinearLayoutManager linearLayoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    FirestoreRecyclerAdapter adapter;



    @BindView(R.id.friendRecycler)
    EmptyRecyclerView friendRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        ButterKnife.bind(this);
        init();
        TextView textView = findViewById(R.id.textview);
        friendRecycler.setEmptyView(textView);


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
        @BindView(R.id.name)
        TextView textName;
        @BindView(R.id.country)
        TextView textCountry;

        public teamHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void findFriends(){

        Query query = db.collection("Users").document(uid).collection("Friends");


        FirestoreRecyclerOptions<UserMap> response = new FirestoreRecyclerOptions.Builder<UserMap>()
                .setQuery(query, UserMap.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<UserMap, teamHolder>(response) {
            @Override
            public void onBindViewHolder(teamHolder holder, int position, final UserMap model) {
                holder.textName.setText(model.getUsername());


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection("Users").document(uid).collection("Friends").document(model.getUserID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot ds = task.getResult();
                                db.collection("Users").document(ds.get("userID").toString()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                        DocumentSnapshot ds1 = task1.getResult();
                                        Intent intent = new Intent(getApplicationContext(), DetailUserActivity.class);
                                        intent.putExtra("displayName",ds1.get("displayName").toString());
                                        intent.putExtra("username", ds1.get("username").toString());
                                        intent.putExtra("userID", ds1.get("userID").toString());
                                        startActivity(intent);
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
                        .inflate(R.layout.list_item, group, false);
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
}
