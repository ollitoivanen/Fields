package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
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

public class SearchFieldOnlyActivity extends AppCompatActivity implements View.OnClickListener {

    String text;
    TextView textView;
    SearchView search;
    Button field_by_area_button;
    Button field_by_name_button;
    EmptyRecyclerView ep;
    StorageReference fieldImageRef;
    Boolean clicked_area;
    Boolean clicked_name;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    boolean listening;
    boolean fromEvent;
    TextView startTraining;
    Button addField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_field_only);
        textView = findViewById(R.id.textViews_empty);
        search = findViewById(R.id.search1);
        listening = false;
        addField = findViewById(R.id.add_new_field);
        addField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchFieldOnlyActivity.this, CreateNewFieldActivity.class));
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
            }
        });
        startTraining = findViewById(R.id.start_text);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setIconified(false);
            }
        });
        field_by_area_button = findViewById(R.id.field_by_area1);
        field_by_area_button.setOnClickListener(this);
        field_by_name_button = findViewById(R.id.field_by_name1);
        field_by_name_button.setOnClickListener(this);
        ep = findViewById(R.id.fieldRecycler);
        field_by_name_button.callOnClick();
        ep.setEmptyView(textView);
        setTitle(getResources().getString(R.string.search_field));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        Bundle info = getIntent().getExtras();
        fromEvent = info.getBoolean("fromEvent");
        if (fromEvent){
            startTraining.setVisibility(View.GONE);
        }

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
    }

    public void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        ep.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    public class fieldHolder extends EmptyRecyclerView.ViewHolder {
        TextView textName;
        ImageView profileImageSearch;


        public fieldHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.name);
            profileImageSearch = itemView.findViewById(R.id.profile_image_search);
        }
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

        adapter = new FirestoreRecyclerAdapter<FieldMap, fieldHolder>(response) {
            @Override
            public void onBindViewHolder(final fieldHolder holder, int position, final FieldMap model) {
                holder.textName.setText(model.getFieldName());

                fieldImageRef = FirebaseStorage.getInstance()
                        .getReference().child("fieldpics/"+model.getFieldID()+".jpg");
                fieldImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                            GlideApp.with(getApplicationContext())
                                    .load(uri)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(holder.profileImageSearch);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.profileImageSearch.setImageDrawable(getResources().getDrawable(R.drawable.field_default));

                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fromEvent) {
                            Intent intent = new Intent();
                            intent.putExtra("fieldName2", model.getFieldName());
                            intent.putExtra("fieldAddress", model.getFieldAddress());
                            intent.putExtra("fieldArea", model.getFieldArea());
                            intent.putExtra("fieldType", model.getFieldType());
                            intent.putExtra("fieldAccessType", model.getAccessType());
                            intent.putExtra("goalCount", model.getGoalCount());
                            intent.putExtra("fieldID", model.getFieldID());
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                        }else {
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


                    }
                });
            }

            @Override
            public fieldHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.list_item, group, false);
                return new fieldHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        ep.setAdapter(adapter);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.field_by_name1:
                clicked_name = true;
                clicked_area = false;
                search.setQuery("", false);
                field_by_name_button.setTextColor((getResources().getColor(R.color.colorPrimaryDark)));
                field_by_area_button.setTextColor((getResources().getColor(R.color.blacknot)));
                break;
            case R.id.field_by_area1:
                clicked_area = true;
                clicked_name = false;
                search.setQuery("", false);
                field_by_area_button.setTextColor((getResources().getColor(R.color.colorPrimaryDark)));
                field_by_name_button.setTextColor((getResources().getColor(R.color.blacknot)));


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listening) {
            adapter.stopListening();
            ep.setAdapter(null);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listening) {
            adapter.stopListening();
            ep.setAdapter(null);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }
}
