package com.fields.curiumx.fields;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    Button addNewField;
    EmptyRecyclerView searchRecycler;
    LinearLayout area_name_thing;
    Button fieldsByName;
    Button fieldsByArea;
    Boolean clicked_name;
    Boolean clicked_area;
    Boolean clicked_fields;
    StorageReference teamImageRef;
    StorageReference fieldImageRef;
    StorageReference userImageRef;
    Boolean listening;
    String teamID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        clicked_fields = false;
        listening = false;
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
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setIconified(false);
            }
        });
        textView = findViewById(R.id.textViews);
        fieldsButton.callOnClick();
        searchRecycler = findViewById(R.id.search_recycler);
        searchRecycler.setEmptyView(textView);
        init();



    }

    public void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        searchRecycler.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    public class teamHolder extends EmptyRecyclerView.ViewHolder {
        TextView textName;
        ImageView profileImageSearch;


        public teamHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.name);
            profileImageSearch = itemView.findViewById(R.id.profile_image_search);
        }
    }

    public void findTeam(){
        text = search.getQuery().toString().toLowerCase().trim();

        Query query = db.collection("Teams").whereEqualTo("teamUsernameText", text);

        FirestoreRecyclerOptions<TeamMap> response = new FirestoreRecyclerOptions.Builder<TeamMap>()
                .setQuery(query, TeamMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<TeamMap, teamHolder>(response) {
            @Override
            public void onBindViewHolder(final teamHolder holder, int position, final TeamMap model) {
                holder.textName.setText(model.getTeamUsernameText());
                teamID = model.getTeamID();

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
                                    .into(holder.profileImageSearch);
                        }else {
                            holder.profileImageSearch.setImageDrawable(getResources().getDrawable(R.drawable.team_default));
                        }
                    }
                    });
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
                        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

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
        searchRecycler.setAdapter(adapter);
    }

    public void findUser(){
        text = search.getQuery().toString().toLowerCase().trim();



        Query query = db.collection("Users").whereEqualTo("username", text);

        FirestoreRecyclerOptions<UserMap> response = new FirestoreRecyclerOptions.Builder<UserMap>()
                .setQuery(query, UserMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<UserMap, teamHolder>(response) {
            @Override
            public void onBindViewHolder(final teamHolder holder, int position, final UserMap model) {
                holder.textName.setText(model.getUsername());
                holder.profileImageSearch.setImageDrawable(null);




                userImageRef = FirebaseStorage.getInstance()
                        .getReference().child("profilepics/"+model.getUserID()+".jpg");
                userImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {


                            GlideApp.with(getApplicationContext())
                                    .load(userImageRef)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(holder.profileImageSearch);
                        }else {
                            holder.profileImageSearch.setImageDrawable(getResources().getDrawable(R.drawable.profile_default));
                        }
                    }
                });




               holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), DetailUserActivity.class);
                        intent.putExtra("username", model.getUsername());
                        intent.putExtra("realName", model.getRealName());
                        intent.putExtra("userID", model.getUserID());
                        intent.putExtra("currentFieldName", model.getCurrentFieldName());
                        intent.putExtra("currentFieldID", model.getCurrentFieldID());
                        intent.putExtra("usersTeam", model.getUsersTeam());
                        intent.putExtra("usersTeamID", model.getUsersTeamID());
                        intent.putExtra("userRole", model.getUserRole());
                        intent.putExtra("userReputation", model.getUserReputation());
                        intent.putExtra("position", model.getPosition());
                        if (!model.getCurrentFieldName().equals("")){
                            intent.putExtra("timestamp", model.getTimestamp());
                        }
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

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
        searchRecycler.setAdapter(adapter);
    }

    public void findField(){
        text = search.getQuery().toString().trim().toLowerCase();
        Query query;
        if (clicked_name){
            query = db.collection("Fields").whereEqualTo("fieldNameLowerCase", text);
            }else {
            query = db.collection("Fields").whereEqualTo("fieldAreaLowerCase", text);
        }

        FirestoreRecyclerOptions<FieldMap> response = new FirestoreRecyclerOptions.Builder<FieldMap>()
                .setQuery(query, FieldMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<FieldMap, teamHolder>(response) {
            @Override
            public void onBindViewHolder(final teamHolder holder, int position, final FieldMap model) {
                holder.textName.setText(model.getFieldName());

                fieldImageRef = FirebaseStorage.getInstance()
                        .getReference().child("fieldpics/"+model.getFieldID()+".jpg");
                fieldImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            GlideApp.with(getApplicationContext())
                                    .load(fieldImageRef)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(holder.profileImageSearch);
                        }else {
                            holder.profileImageSearch.setImageDrawable(getResources().getDrawable(R.drawable.field_default));
                        }
                    }
                });

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
                        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

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
        searchRecycler.setAdapter(adapter);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.users_button:
                search.setQuery("", false);
                area_name_thing.setVisibility(View.GONE);
                addNewField.setVisibility(View.GONE);
                clicked_fields = false;

                usersButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                teamsButton.setTextColor(getResources().getColor(R.color.blacknot));
                fieldsButton.setTextColor(getResources().getColor(R.color.blacknot));

                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        findUser();
                        adapter.startListening();
                        listening = true;
                        return false;
                    }
                });
                break;

            case R.id.teams_button:
                area_name_thing.setVisibility(View.GONE);
                addNewField.setVisibility(View.GONE);
                clicked_fields = false;
                search.setQuery("", false);
                teamsButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                usersButton.setTextColor(getResources().getColor(R.color.blacknot));
                fieldsButton.setTextColor(getResources().getColor(R.color.blacknot));

                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        findTeam();
                        adapter.startListening();
                        listening = true;
                        return false;
                    }
                });
                break;

            case R.id.fields_button:
                search.setQuery("", false);
                area_name_thing.setVisibility(View.VISIBLE);

                addNewField.setVisibility(View.VISIBLE);
                fieldsButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                usersButton.setTextColor(getResources().getColor(R.color.blacknot));
                teamsButton.setTextColor(getResources().getColor(R.color.blacknot));
                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        findField();
                        adapter.startListening();
                        listening = true;
                        return false;
                    }

                });

                break;

            case R.id.add_new_field:
                startActivity(new Intent(SearchActivity.this, CreateNewFieldActivity.class));
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

    public void onProfileClick(View view){
        LinearLayout activityBar = findViewById(R.id.activityBar);
        Intent intent = new Intent(this, ProfileActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, activityBar, "bar");
        startActivity(intent, options.toBundle());


    }

    public void onFeedClick(View view){
        LinearLayout activityBar = findViewById(R.id.activityBar);
        Intent intent = new Intent(this, FeedActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, activityBar, "bar");
        startActivity(intent, options.toBundle());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listening) {
            adapter.stopListening();
            searchRecycler.setAdapter(null);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listening) {
            adapter.stopListening();
            searchRecycler.setAdapter(null);
        }

    }
}
