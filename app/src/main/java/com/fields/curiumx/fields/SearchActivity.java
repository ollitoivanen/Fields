package com.fields.curiumx.fields;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{
    TextView textView;
    SearchView search;
    String text;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    Button usersButton;
    Button teamsButton;
    Button fieldsButton;
    TextView addNewField;
    @BindView(R.id.teamRecycler)
    EmptyRecyclerView teamRecycler;
    LinearLayout area_name_thing;
    Button fieldsByName;
    Button fieldsByArea;
    Boolean clicked_name;
    Boolean clicked_area;
    Boolean clicked_fields;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        init();

        clicked_fields = false;

        fieldsByArea = findViewById(R.id.field_by_area);
        fieldsByName = findViewById(R.id.field_by_name);
        fieldsByArea.setOnClickListener(this);
        fieldsByName.setOnClickListener(this);
        fieldsByName.callOnClick();
        area_name_thing = findViewById(R.id.area_name_thing);
        addNewField = findViewById(R.id.add_new_field);
        addNewField.setOnClickListener(this);
        usersButton = findViewById(R.id.users_button);
        usersButton.setOnClickListener(this);
        teamsButton = findViewById(R.id.teams_button);
        teamsButton.setOnClickListener(this);
        fieldsButton = findViewById(R.id.fields_button);
        fieldsButton.setOnClickListener(this);
        search = findViewById(R.id.search);
        textView = findViewById(R.id.textViews);
        fieldsButton.callOnClick();
        teamRecycler.setEmptyView(textView);

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


        Query query = db.collection("Teams").whereEqualTo("teamUsernameText", text);

        FirestoreRecyclerOptions<TeamMap> response = new FirestoreRecyclerOptions.Builder<TeamMap>()
                .setQuery(query, TeamMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<TeamMap, teamHolder>(response) {
            @Override
            public void onBindViewHolder(teamHolder holder, int position, final TeamMap model) {
                holder.textName.setText(model.getTeamUsernameText());
                //holder.textCountry.setText(model.getTeamCountryText());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), DetailTeamActivity.class);
                        intent.putExtra("name", model.getTeamUsernameText());
                        intent.putExtra("fullname", model.getTeamFullNameText());
                        intent.putExtra("country", model.getTeamCountryText());
                        intent.putExtra("teamID", model.getTeamID());

                        intent.putExtra("level", model.getLevel());
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


    public void findUser(){
        text = search.getQuery().toString();



        Query query = db.collection("Users").whereEqualTo("username", text);

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
                        Intent intent = new Intent(getApplicationContext(), DetailUserActivity.class);
                        intent.putExtra("username", model.getUsername());
                        intent.putExtra("username", model.getUsername());
                        intent.putExtra("userID", model.getUserID());
                        intent.putExtra("currentFieldName", model.getCurrentFieldName());
                        intent.putExtra("currentFieldID", model.getCurrentFieldID());
                        intent.putExtra("usersTeam", model.getUsersTeam());
                        intent.putExtra("usersTeamID", model.getUsersTeamID());
                        intent.putExtra("userRole", model.getUserRole());
                        intent.putExtra("userReputation", model.getUserReputation());
                        intent.putExtra("position", model.getPosition());
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


    public void findField(){
        text = search.getQuery().toString();
        Query query;
        if (clicked_name){
            query = db.collection("Fields").whereEqualTo("fieldName", text);
            }else {
            query = db.collection("Fields").whereEqualTo("fieldArea", text);
        }

        FirestoreRecyclerOptions<FieldMap> response = new FirestoreRecyclerOptions.Builder<FieldMap>()
                .setQuery(query, FieldMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<FieldMap, teamHolder>(response) {
            @Override
            public void onBindViewHolder(teamHolder holder, int position, final FieldMap model) {
                holder.textName.setText(model.getFieldName());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), DetailFieldActivity.class);
                        intent.putExtra("fieldName", model.getFieldName());
                        intent.putExtra("fieldAddress", model.getFieldAddress());
                        intent.putExtra("fieldArea", model.getFieldArea());
                        intent.putExtra("fieldType", model.getFieldType());
                        intent.putExtra("fieldAccessType", model.getAccessType());
                        intent.putExtra("goalCount", model.getGoalCount());
                        intent.putExtra("fieldID", model.getFieldID());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void animateThing(){
        area_name_thing = findViewById(R.id.area_name_thing);
        ObjectAnimator animator = ObjectAnimator.ofFloat(area_name_thing, "y", 200f )
                .setDuration(100);
        animator.addListener(new Animator.AnimatorListener(){
            @Override
            public void onAnimationStart(Animator animator) {
                area_name_thing.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                area_name_thing.setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                area_name_thing.post(new Runnable() {
                    @Override
                    public void run() {
                        area_name_thing.setLayerType(View.LAYER_TYPE_NONE, null);


                    }
                });
            }
        });
        animator.start();
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.users_button:
                search.setQuery("", false);
                area_name_thing.setVisibility(View.GONE);
                addNewField.setVisibility(View.GONE);
                clicked_fields = false;

                usersButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                teamsButton.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                fieldsButton.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));

                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        findUser();
                        adapter.startListening();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
                break;

            case R.id.teams_button:
                area_name_thing.setVisibility(View.GONE);
                addNewField.setVisibility(View.GONE);
                clicked_fields = false;
                search.setQuery("", false);
                teamsButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                usersButton.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                fieldsButton.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));

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
                break;

            case R.id.fields_button:
                search.setQuery("", false);
                area_name_thing.setVisibility(View.VISIBLE);

                addNewField.setVisibility(View.VISIBLE);
                fieldsButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                usersButton.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                teamsButton.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        findField();
                        adapter.startListening();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });

                break;

            case R.id.add_new_field:
                startActivity(new Intent(SearchActivity.this, CreateNewFieldActivity.class));
                break;

            case R.id.field_by_name:
                clicked_name = true;
                clicked_area = false;
                fieldsByName.setTextColor((getResources().getColor(R.color.colorPrimaryDark)));
                fieldsByArea.setTextColor((getResources().getColor(R.color.blacknot)));
                break;
            case R.id.field_by_area:
                clicked_area = true;
                clicked_name = false;
                fieldsByArea.setTextColor((getResources().getColor(R.color.colorPrimaryDark)));
                fieldsByName.setTextColor((getResources().getColor(R.color.blacknot)));



        }

    }








}
