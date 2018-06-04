package com.fields.curiumx.fields;


import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



import static java.lang.String.valueOf;

public class DetailFieldActivity extends AppCompatActivity {

    TextView imTrainingHereButton;
    TextView imTrainingHereNoMore;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference reference;
    String uid = user.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView amountOfPeople;
    ProgressBar progressBar;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String fieldID;
    String fieldName;
    String fieldAddress;
    String fieldArea;
    int fieldType;
    int fieldAccessType;
    int goalCount;
    ImageView fieldPhoto;
    TextView fieldLocation;
    LinearLayout area_map;
    TextView fieldTypeView;
    ImageView dropImage;
    ConstraintLayout container;
    EmptyRecyclerView fieldEventRecycler;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    String[] eventArray;
    String eventTypeString;
    boolean favorite;
    NotificationHelper notificationHelper;
    boolean fieldsPlus;

    String[] fieldTypeArray;
    String[] fieldAccessTypeArray;
    String[] fieldGoalCountArray;

    private Menu menu;

    public class fieldEventHolder extends EmptyRecyclerView.ViewHolder {

        TextView fieldEventTime;
        TextView fieldEventDate;
        TextView fieldEventTeam;
        TextView fieldEventType;

        public fieldEventHolder(View itemView) {
            super(itemView);

            fieldEventTime = itemView.findViewById(R.id.event_time_field);
            fieldEventDate = itemView.findViewById(R.id.event_date_field);
            fieldEventType = itemView.findViewById(R.id.event_type_field);
            fieldEventTeam = itemView.findViewById(R.id.event_team_field);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        imTrainingHereNoMore.setEnabled(false);
        fieldEventRecycler.setAdapter(adapter);
        adapter.startListening();
        loadFieldInfo();
        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                imTrainingHereNoMore.setEnabled(true);
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.get("currentFieldID").toString().equals(fieldID)) {
                    imTrainingHereNoMore.setVisibility(View.VISIBLE);
                    imTrainingHereButton.setVisibility(View.GONE);
                }else if (!documentSnapshot.get("currentFieldID").toString().equals("")){
                    imTrainingHereNoMore.setVisibility(View.GONE);
                    imTrainingHereButton.setVisibility(View.GONE);
                } else if (documentSnapshot.get("currentFieldID").toString().equals("")){
                    imTrainingHereButton.setVisibility(View.VISIBLE);
                    imTrainingHereNoMore.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.menu = menu;
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_field, menu);
        inflater.inflate(R.menu.favorite, menu);
        menu.getItem(1).setEnabled(false);
        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot ds = task.getResult();
                fieldsPlus = ds.getBoolean("fieldsPlus");

                if (fieldsPlus){

                    db.collection("Users").document(uid).collection("favoriteFields").document(fieldID)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                favorite = true;
                                menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_star));
                                menu.getItem(1).setEnabled(true);

                            } else {
                                favorite = false;
                                menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_star_outline));
                                menu.getItem(1).setEnabled(true);

                            }
                        }
                    });
                }else {
                    menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_star_outline));
                    menu.getItem(1).setEnabled(true);


                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_detail);
        notificationHelper = new NotificationHelper(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fieldEventRecycler = findViewById(R.id.field_event_recycler);
        fieldEventRecycler.setEmptyView(findViewById(R.id.empty_view_field));
        init();
        imTrainingHereButton = findViewById(R.id.imTrainingHereButton);
        imTrainingHereNoMore = findViewById(R.id.imTrainingHereNoMoreButton);
        amountOfPeople = findViewById(R.id.amountOfPeople);
        progressBar = findViewById(R.id.progress_bar);
        fieldPhoto = findViewById(R.id.FieldImage);
        fieldLocation = findViewById(R.id.field_area1);
        area_map = findViewById(R.id.area_map);
        fieldTypeView = findViewById(R.id.fieldType);
        dropImage = findViewById(R.id.dropdown);
        container = findViewById(R.id.gradient2);


        Bundle info = getIntent().getExtras();
        fieldName = info.getString("fieldName");
        fieldAddress = info.getString("fieldAddress");
        fieldArea = info.getString("fieldArea");
        fieldType = info.getInt("fieldType");
        fieldAccessType = info.getInt("fieldAccessType");
        goalCount = info.getInt("goalCount");
        fieldID = info.getString("fieldID");
        fieldLocation.setText(fieldArea);
        fieldTypeArray = getResources().getStringArray(R.array.field_type_array);
        fieldAccessTypeArray = getResources().getStringArray(R.array.field_access_type_array);
        fieldGoalCountArray = getResources().getStringArray(R.array.goal_count_array);
        String fieldTypeText = fieldTypeArray[fieldType];
        fieldTypeView.setText(getResources().getString(R.string.field_type_info, fieldTypeText));

        area_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setPackage("com.google.android.apps.maps");
                intent.setData(Uri.parse("https://www.google.com/maps/search/?api=1&query=" + fieldName));
                startActivity(intent);
            }
        });

        dropImage.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             final AlertDialog.Builder alertDialog = new AlertDialog
                                                     .Builder(DetailFieldActivity.this );
                                             View mView = getLayoutInflater().inflate(R.layout.field_detail_popup, null);
                                             TextView goalCountDrop = mView.findViewById(R.id.goalCountText);
                                             TextView accessDrop = mView.findViewById(R.id.access);
                                             TextView addressDrop = mView.findViewById(R.id.address);
                                             String fieldAccessTypeText = fieldAccessTypeArray[fieldAccessType];
                                             String fieldGoalCountText = fieldGoalCountArray[goalCount];
                                             goalCountDrop.setText(getResources().getString(R.string.goals, fieldGoalCountText));
                                             accessDrop.setText(getResources().getString(R.string.access_type, fieldAccessTypeText));
                                             addressDrop.setText(getResources().getString(R.string.address, fieldAddress));
                                             alertDialog.setView(mView);
                                             final AlertDialog dialog = alertDialog.create();
                                             dialog.show();
                                         }
                                     });





        setTitle(fieldName);
        TextView fieldName = findViewById(R.id.fieldName);
        fieldName.setText(this.fieldName);



        imTrainingHereNoMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressImHereNoMore();
            }
        });

        imTrainingHereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressImHere();
            }
        });

        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.get("currentFieldID").toString().equals(fieldID)) {
                    imTrainingHereNoMore.setVisibility(View.VISIBLE);
                    imTrainingHereButton.setVisibility(View.GONE);
                }else if (!documentSnapshot.get("currentFieldID").toString().equals("")){
                    imTrainingHereNoMore.setVisibility(View.GONE);
                    imTrainingHereButton.setVisibility(View.GONE);
                } else if (documentSnapshot.get("currentFieldID").toString().equals("")){
                    imTrainingHereButton.setVisibility(View.VISIBLE);
                    imTrainingHereNoMore.setVisibility(View.GONE);
                }
            }
        });

        db.collection("Users").whereEqualTo("currentFieldID", fieldID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String text0 = valueOf(task.getResult().size());
                String text = getResources().getString(R.string.people_here, text0);
                amountOfPeople.setText(text);
                amountOfPeople.setVisibility(View.VISIBLE);
                container.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

        setRecycler();
        loadFieldInfo();
    }

    public void loadFieldInfo(){
        final StorageReference storageRef = storage.getReference().child("fieldpics/"+fieldID+".jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                    GlideApp.with(getApplicationContext())
                            .load(uri)
                            .into(fieldPhoto);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fieldPhoto.setImageDrawable(getResources().getDrawable(R.drawable.field_default));

            }
        });
    }

    public void onButtonPressImHere() {

        progressBar.setVisibility(View.VISIBLE);
        final Intent intent = new Intent(DetailFieldActivity.this, TrainingActivity.class);
        intent.putExtra("fieldName", fieldName);
        imTrainingHereButton.setVisibility(View.GONE);
        imTrainingHereNoMore.setEnabled(false);
        imTrainingHereNoMore.setVisibility(View.VISIBLE);
        UserTrainingMap userTrainingMap = new UserTrainingMap(fieldName, user.getDisplayName(), uid);
        db.collection("Users").document(uid).collection("currentTraining").document(uid)
                .set(userTrainingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                db.collection("Users").document(uid).update("currentFieldID", fieldID);
                db.collection("Users").document(uid).update("currentFieldName", fieldName);
                db.collection("Users").document(uid).update("timestamp", FieldValue.serverTimestamp())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sendNotification("Current Training", fieldName);
                                progressBar.setVisibility(View.GONE);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                                imTrainingHereNoMore.setEnabled(true);

                            }
                        });
            }
        });



    }

    public void onButtonPressImHereNoMore(){

        final Intent intent = new Intent(DetailFieldActivity.this, TrainingActivity.class);
        intent.putExtra("fieldName", fieldName);

        imTrainingHereButton.setEnabled(false);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        imTrainingHereButton.setEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            onBackPressed();
            return true;
            case R.id.edit_field:
                Intent intent = new Intent(DetailFieldActivity.this, EditFieldActivity.class);
                intent.putExtra("fieldName", fieldName);
                intent.putExtra("fieldID", fieldID);
                intent.putExtra("fieldType", fieldType);
                intent.putExtra("fieldAddress", fieldAddress);
                intent.putExtra("fieldArea", fieldArea);
                intent.putExtra("fieldAccessType", fieldAccessType);
                intent.putExtra("goalCount", goalCount);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                break;
            case R.id.favorite:


                        if (fieldsPlus){
                            if (!favorite) {



                                menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_star));
                                menu.getItem(1).setEnabled(false);


                                FieldMap fieldMap = new FieldMap(fieldName, null, fieldArea, null,
                                        fieldAddress, fieldID, goalCount, fieldType, fieldAccessType);
                                db.collection("Users").document(uid).collection("favoriteFields")
                                        .document(fieldID).set(fieldMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        favorite = true;
                                        Snackbar.make(findViewById(R.id.detail_field_actvity),
                                                getResources().getString(R.string.added_to_favorites),
                                                Snackbar.LENGTH_SHORT).show();
                                        menu.getItem(1).setEnabled(true);

                                    }
                                });
                            }else {
                                menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.ic_star_outline));
                                menu.getItem(1).setEnabled(false);
                                db.collection("Users").document(uid).collection("favoriteFields")
                                        .document(fieldID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Snackbar.make(findViewById(R.id.detail_field_actvity),
                                                getResources().getString(R.string.removed_from_favorites),
                                                Snackbar.LENGTH_SHORT).show();
                                        favorite = false;
                                        menu.getItem(1).setEnabled(true);
                                    }
                                });
                            }


                        }else {
                            startActivity(new Intent(DetailFieldActivity.this, FieldsPlusStartActivity.class));
                            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                        }


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

    public void onProfileClick(View view) {
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

    public void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        fieldEventRecycler.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    public void setRecycler(){
        Query query = db.collection("Fields").document(fieldID).collection("fieldEvents");

        final FirestoreRecyclerOptions<FieldEventMap> response = new FirestoreRecyclerOptions.Builder<FieldEventMap>()
                .setQuery(query, FieldEventMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<FieldEventMap, fieldEventHolder>(response) {
            @Override
            public void onBindViewHolder(@NonNull fieldEventHolder holder, int position, @NonNull final FieldEventMap model) {

                if (model.getEventStartDateMillis() - System.currentTimeMillis() < 0) {

                    db.collection("Teams").document(model.getTeamID())
                            .collection("teamEvents")
                            .document(model.getEventID()).delete();

                    db.collection("Fields").document(fieldID).collection("fieldEvents")
                    .document(model.getEventID()).delete();


                }


                    eventArray = getResources().getStringArray(R.array.event_type_array);
                    eventTypeString = eventArray[model.getFieldEventType()];
                    holder.fieldEventType.setText(eventTypeString);

                    Date dateSave = model.getFieldEventDate();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTF"));
                    final String date = dateFormat.format(dateSave);
                    holder.fieldEventDate.setText(date);
                    holder.fieldEventTeam.setText(model.getFieldEventTeam());

                    holder.fieldEventTime.setText(getResources().
                            getString(R.string.training_time, model.getFieldEventTimeStart(), model.getFieldEventTimeEnd()));

                }


            @Override
            public fieldEventHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.field_event_list, group, false);
                return new fieldEventHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }

        };

        adapter.notifyDataSetChanged();
        fieldEventRecycler.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        fieldEventRecycler.setAdapter(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
        fieldEventRecycler.setAdapter(null);
    }

    public void sendNotification(String title, String message){
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(title, message);
        notificationHelper.getManager().notify(1, nb.build());
    }
}