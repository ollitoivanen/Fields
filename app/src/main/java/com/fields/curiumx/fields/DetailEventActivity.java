package com.fields.curiumx.fields;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ProgressBar;
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



import java.util.HashMap;
import java.util.Map;



public class DetailEventActivity extends AppCompatActivity implements View.OnClickListener{
    TextView detailType;
    TextView detailPlace;
    TextView detailTime;
    TextView detailDate;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    String eventID;
    String teamID;
    ProgressBar progressBar;
    EmptyRecyclerView takingPartRecycler;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    String event_member_status_in = "1";
    String event_member_status_out = "2";
    String event_member_status_open = "0";
    StorageReference profileImageRef;


    Button in;
    Button out;
    Button open;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.delete_event, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_event:
                progressBar.setVisibility(View.VISIBLE);
                db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot ds = task.getResult();
                        String teamID = ds.get("usersTeamID").toString();

                        db.collection("Teams").document(teamID).collection("teamEvents")
                                .document(eventID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DetailEventActivity.this, TeamActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                            }
                                });
                    }
                });
                break;
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.in_button:
                Map<String, Object> map = new HashMap<>();
                map.put("member_status", event_member_status_in);
                db.collection("Teams").document(teamID).collection("teamEvents")
                        .document(eventID).collection("eventMembers").document(uid).set(map);


                        in.setBackground(getResources().getDrawable(R.drawable.rounded_view_green));
                        in.setTextColor(getResources().getColor(R.color.cardview_light_background));

                        out.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                        out.setTextColor(getResources().getColor(R.color.red_out));

                        open.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                        open.setTextColor(getResources().getColor(R.color.blacknot));


                break;
            case R.id.out_button:
                Map<String, Object> map1 = new HashMap<>();
                map1.put("member_status", event_member_status_out);
                db.collection("Teams").document(teamID).collection("teamEvents")
                        .document(eventID).collection("eventMembers").document(uid).set(map1);

                                in.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                                in.setTextColor(getResources().getColor(R.color.colorPrimary));

                                out.setBackground(getResources().getDrawable(R.drawable.rounded_view_red));
                                out.setTextColor(getResources().getColor(R.color.cardview_light_background));

                                open.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                                open.setTextColor(getResources().getColor(R.color.blacknot));

                break;
            case R.id.half_button:
                Map<String, Object> map2 = new HashMap<>();
                map2.put("member_status", event_member_status_open);
                db.collection("Teams").document(teamID).collection("teamEvents")
                        .document(eventID).collection("eventMembers").document(uid).set(map2);

                                in.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                                in.setTextColor(getResources().getColor(R.color.colorPrimary));

                                out.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                                out.setTextColor(getResources().getColor(R.color.red_out));

                                open.setBackground(getResources().getDrawable(R.drawable.rounded_view_gray));
                                open.setTextColor(getResources().getColor(R.color.cardview_light_background));

                break;
        }
    }

    public class partHolder extends EmptyRecyclerView.ViewHolder {

        TextView nameTaking;
        ImageView profileImage;

        public partHolder(View itemView) {
            super(itemView);
            nameTaking = itemView.findViewById(R.id.nameTaking);
            profileImage = itemView.findViewById(R.id.profile_image_list);
        }
    }

    public void init(){
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        takingPartRecycler.setLayoutManager(linearLayoutManager);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        in = findViewById(R.id.in_button);
        in.setOnClickListener(this);
        out = findViewById(R.id.out_button);
        out.setOnClickListener(this);
        open = findViewById(R.id.half_button);
        open.setOnClickListener(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.event_details));

        Bundle info = getIntent().getExtras();
        String type  = info.getString("type");
        String place = info.getString("place");
        String date = info.getString("date");
        String timeStart = info.getString("timeStart");
        String timeEnd = info.getString("timeEnd");
        eventID = info.getString("eventID");
        teamID = info.getString("teamID");

        takingPartRecycler = findViewById(R.id.taking_part_recycler);
        init();

        Query query = db.collection("Teams").document(teamID).collection("TeamUsers");


        FirestoreRecyclerOptions<MemberMap> response = new FirestoreRecyclerOptions.Builder<MemberMap>()
                .setQuery(query, MemberMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<MemberMap, partHolder>(response) {
            @Override
            public void onBindViewHolder(final partHolder holder, int position, final MemberMap model) {
                if (model.getUidMember().equals(user.getUid())) {
                    holder.nameTaking.setText(getResources().getString(R.string.you));

                    db.collection("Teams").document(teamID).collection("teamEvents")
                            .document(eventID).collection("eventMembers").document(model.getUidMember())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (!task.getResult().exists()) {
                                Map<String, Object> mapOpen = new HashMap<>();
                                mapOpen.put("member_status", event_member_status_open);
                                db.collection("Teams").document(teamID).collection("teamEvents")
                                        .document(eventID).collection("eventMembers")
                                        .document(model.getUidMember()).set(mapOpen)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            } else {
                                DocumentSnapshot ds = task.getResult();

                                    if (ds.get("member_status").equals("1")) {
                                        holder.nameTaking.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        in.setBackground(getResources().getDrawable(R.drawable.rounded_view_green));
                                        in.setTextColor(getResources().getColor(R.color.cardview_light_background));

                                        out.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                                        out.setTextColor(getResources().getColor(R.color.red_out));

                                        open.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                                        open.setTextColor(getResources().getColor(R.color.blacknot));
                                    } else if (ds.get("member_status").equals("2")) {
                                        holder.nameTaking.setTextColor(getResources().getColor(R.color.red_out));
                                        in.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                                        in.setTextColor(getResources().getColor(R.color.colorPrimary));

                                        out.setBackground(getResources().getDrawable(R.drawable.rounded_view_red));
                                        out.setTextColor(getResources().getColor(R.color.cardview_light_background));

                                        open.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                                        open.setTextColor(getResources().getColor(R.color.blacknot));
                                    } else {
                                        in.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                                        in.setTextColor(getResources().getColor(R.color.colorPrimary));

                                        out.setBackground(getResources().getDrawable(R.drawable.rounded_view));
                                        out.setTextColor(getResources().getColor(R.color.red_out));

                                        open.setBackground(getResources().getDrawable(R.drawable.rounded_view_gray));
                                        open.setTextColor(getResources().getColor(R.color.cardview_light_background));
                                    }

                            }
                        }
                    });


                } else{
                    holder.nameTaking.setText(model.getUsernameMember());

                    db.collection("Teams").document(teamID).collection("teamEvents")
                            .document(eventID).collection("eventMembers").document(model.getUidMember())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (!task.getResult().exists()) {
                                Map<String, Object> mapOpen = new HashMap<>();
                                mapOpen.put("member_status", event_member_status_open);
                                db.collection("Teams").document(teamID).collection("teamEvents")
                                        .document(eventID).collection("eventMembers")
                                        .document(model.getUidMember()).set(mapOpen)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            } else {
                                DocumentSnapshot ds = task.getResult();

                                if (ds.get("member_status").equals("1")) {
                                    holder.nameTaking.setTextColor(getResources().getColor(R.color.colorPrimary));

                                } else if (ds.get("member_status").equals("2")) {
                                    holder.nameTaking.setTextColor(getResources().getColor(R.color.red_out));

                                }

                            }
                        }
                    });


            }

                profileImageRef = FirebaseStorage.getInstance()
                        .getReference().child("profilepics/"+model.getUidMember()+".jpg");
                profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                            GlideApp.with(getApplicationContext())
                                    .load(uri)
                                    .into(holder.profileImage);




                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.profileImage.setImageDrawable(getResources().
                                getDrawable(R.drawable.profile_default));
                    }
                });





                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                    }
                });
            }

            @Override
            public partHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.taking_part_list, group, false);
                return new partHolder(view);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };

        adapter.notifyDataSetChanged();
        takingPartRecycler.setAdapter(adapter);
        adapter.startListening();


        detailType = findViewById(R.id.eventTypeDetail);
        detailDate = findViewById(R.id.eventDateDetail);
        detailPlace = findViewById(R.id.eventPlaceDetail);
        detailTime = findViewById(R.id.eventTimeDetail);
        progressBar = findViewById(R.id.prgress_delete);



        detailType.setText(type);
        detailDate.setText(date);

        if (place.equals("")){
            detailPlace.setText(getResources().getString(R.string.location_not_given));
        }else {
            detailPlace.setText(place);
        }
        detailTime.setText(getResources().getString(R.string.training_time, timeStart, timeEnd));


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.startListening();
        takingPartRecycler.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        adapter.stopListening();
        takingPartRecycler.setAdapter(null);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        adapter.stopListening();
        takingPartRecycler.setAdapter(null);
        super.onDestroy();
    }


}
