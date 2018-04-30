package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailEventActivity extends Activity implements View.OnClickListener{
    TextView detailType;
    TextView detailPlace;
    TextView detailTime;
    TextView detailDate;
    ImageView crossRed;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    String eventID;
    String teamID;
    ProgressBar progressBar;
    @BindView(R.id.taking_part_recycler)
    EmptyRecyclerView takingPartRecycler;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    String event_member_status_in = "1";
    String event_member_status_out = "2";
    String event_member_status_open = "0";



    Button in;
    Button out;
    Button open;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.in_button:
                Map<String, Object> map = new HashMap<>();
                map.put("member_status", event_member_status_in);
                db.collection("Teams").document(teamID).collection("Team's Events")
                        .document(eventID).collection("Event Members").document(uid).set(map);

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
                db.collection("Teams").document(teamID).collection("Team's Events").document(eventID).collection("Event Members").document(uid).set(map1);

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
                db.collection("Teams").document(teamID).collection("Team's Events").document(eventID).collection("Event Members").document(uid).set(map2);

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

        @BindView(R.id.nameTaking)
        TextView nameTaking;




        public partHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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

        Bundle info = getIntent().getExtras();
        String type  = info.getString("type");
        String place = info.getString("place");
        String dateSave = info.getString("date");
        String timeStart = info.getString("timeStart");
        String timeEnd = info.getString("timeEnd");
        eventID = info.getString("eventID");
        teamID = info.getString("teamID");

        takingPartRecycler = findViewById(R.id.taking_part_recycler);
        init();

        ButterKnife.bind(this);
        Query query = db.collection("Teams").document(teamID).collection("TeamUsers");


        FirestoreRecyclerOptions<MemberMap> response = new FirestoreRecyclerOptions.Builder<MemberMap>()
                .setQuery(query, MemberMap.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<MemberMap, partHolder>(response) {
            @Override
            public void onBindViewHolder(final partHolder holder, int position, final MemberMap model) {
                if (model.getUidMember().equals(user.getUid())) {
                    holder.nameTaking.setText(getResources().getString(R.string.you));

                } else{
                    holder.nameTaking.setText(model.getUsernameMember());
            }



            db.collection("Teams").document(teamID).collection("Team's Events")
                    .document(eventID).collection("Event Members").document(model.uidMember)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (!task.getResult().exists()) {
                        Map<String, Object> mapOpen = new HashMap<>();
                        mapOpen.put("member_status", event_member_status_open);
                        db.collection("Teams").document(teamID).collection("Team's Events")
                                .document(eventID).collection("Event Members").document(model.uidMember).set(mapOpen).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                        }else {
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
        crossRed =findViewById(R.id.crossRed);
        crossRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crossRed.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       DocumentSnapshot ds = task.getResult();
                       String teamID = ds.get("usersTeamID").toString();

                       db.collection("Teams").document(teamID).collection("Team's Events")
                               .document(eventID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               crossRed.setEnabled(true);
                               progressBar.setVisibility(View.GONE);
                               Toast.makeText(getApplicationContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(DetailEventActivity.this, TeamActivity.class);
                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                               startActivity(intent);

                           }
                       });
                    }
                });
            }
        });



        detailType.setText(type);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM", Locale.US);
        try {
            Date dateSaveToDate = dateFormat.parse(dateSave);
            SimpleDateFormat timeFormat = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
            String finalDateSave = timeFormat.format(dateSaveToDate);
            detailDate.setText(finalDateSave);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (place.equals("")){
            detailPlace.setText(getResources().getString(R.string.location_not_given));
        }else {
            detailPlace.setText(place);
        }
        detailTime.setText(timeStart + "- " + timeEnd);


    }
}
