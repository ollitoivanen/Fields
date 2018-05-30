package com.fields.curiumx.fields;

import android.content.Intent;
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
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FavoriteFieldsActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    StorageReference fieldImageRef;
    EmptyRecyclerView favoriteFieldsRecycler;
    boolean listening = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){


            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        favoriteFieldsRecycler.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_fields);
        setTitle(getResources().getString(R.string.favorite_fields));
        favoriteFieldsRecycler = findViewById(R.id.favorite_fields_recycler);
        favoriteFieldsRecycler.setEmptyView(findViewById(R.id.favorite_fields_empty_view));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findField();
        init();

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

    public void findField() {
        Query query;
        query = db.collection("Users").document(uid).collection("favoriteFields");


        FirestoreRecyclerOptions<FieldMap> response = new FirestoreRecyclerOptions.Builder<FieldMap>()
                .setQuery(query, FieldMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<FieldMap, teamHolder>(response) {
            @Override
            public void onBindViewHolder(final teamHolder holder, int position, final FieldMap model) {
                holder.textName.setText(model.getFieldName());

                fieldImageRef = FirebaseStorage.getInstance()
                        .getReference().child("fieldpics/" + model.getFieldID() + ".jpg");
                fieldImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        GlideApp.with(getApplicationContext())
                                .load(fieldImageRef)
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
        favoriteFieldsRecycler.setAdapter(adapter);
        adapter.startListening();
        listening = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listening) {
            favoriteFieldsRecycler.setAdapter(null);
            adapter.stopListening();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listening) {
            favoriteFieldsRecycler.setAdapter(null);
            adapter.stopListening();
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        favoriteFieldsRecycler.setAdapter(adapter);
        adapter.startListening();
        listening = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }
}
