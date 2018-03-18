package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends Activity {
    TextView textView;
    SearchView search;
    String text;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    @BindView(R.id.teamRecycler)
    EmptyRecyclerView teamRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        init();

        search = findViewById(R.id.search);
        textView = findViewById(R.id.textViews);

        teamRecycler.setEmptyView(textView);


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                    findTeam();
                adapter.startListening();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }







    public void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        teamRecycler.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
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


    public void findTeam(){
        text = search.getQuery().toString();


        Query query = db.collection("Teams").whereEqualTo("teamNameText", text);

        FirestoreRecyclerOptions<TeamMap> response = new FirestoreRecyclerOptions.Builder<TeamMap>()
                .setQuery(query, TeamMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<TeamMap, teamHolder>(response) {
            @Override
            public void onBindViewHolder(teamHolder holder, int position, final TeamMap model) {
                holder.textName.setText(model.getTeamNameText());
                holder.textCountry.setText(model.getTeamCountryText());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), DetailTeamActivity.class);
                        intent.putExtra("name", model.getTeamNameText());
                        intent.putExtra("country", model.getTeamCountryText());
                        startActivity(intent);
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
        teamRecycler.setAdapter(adapter);
    }





    @Override
    public void onStart() {
        super.onStart();

    }




    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}
