package com.fields.curiumx.fields;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AllTrainingsActivity extends AppCompatActivity {
    Boolean fieldsPlus;
    ConstraintLayout noFieldsPlusConstraint;
    TextView levelUp;
    FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query query;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    EmptyRecyclerView trainingsRecycler;
    long placeHolder;
    String placeHolder2;
    long placeHolder0;


    public class trainingHolder extends EmptyRecyclerView.ViewHolder {
        TextView trainingDate;
        TextView trainingDuration;
        TextView trainingReputation;
        TextView trainingField;


        private trainingHolder(View view) {
            super(view);
            trainingDate = view.findViewById(R.id.trainingDate);
            trainingDuration = view.findViewById(R.id.training_duration);
            trainingReputation = view.findViewById(R.id.training_reputation);
            trainingField = view.findViewById(R.id.training_field);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trainings);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle info = getIntent().getExtras();
        fieldsPlus = info.getBoolean("fieldsPlus");

        if (fieldsPlus){
            trainingsFieldsPlus();
        }else {
            trainingsNoFieldsPlus();
        }
    }

    public void init() {
        trainingsRecycler = findViewById(R.id.trainings_recycler);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        trainingsRecycler.setLayoutManager(linearLayoutManager);

    }



    public void trainingsFieldsPlus(){
        setTitle(getResources().getString(R.string.training_history_title));
        setRecycler();
    }

    public void trainingsNoFieldsPlus(){
        noFieldsPlusConstraint = findViewById(R.id.no_fields_plus_constraint_training);
        levelUp = findViewById(R.id.level_up_text2_training);
        String text = "<font COLOR=\'#FFFFFF\'><b>" + getResources().getString(R.string.level_up_training) + "</b></font>" + " " +
                "<font COLOR=\'#3bd774\'><b>" + getResources().getString(R.string.training_history) + "</b></font>" + " " +
                "<font COLOR=\'#FFFFFF\'><b>" + getResources().getString(R.string.level_up_training2) + "</b></font>" + " " +
                "<font COLOR=\'#3FACFF\'><b>" + getResources().getString(R.string.plus_comma) + "</b></font>";
        levelUp.setText(Html.fromHtml(text));
        noFieldsPlusConstraint.setVisibility(View.VISIBLE);
        setTitle("");
        Button discoverButton = findViewById(R.id.discover_training);
        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllTrainingsActivity.this, FieldsPlusStartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

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

   public void setRecycler(){
        init();

        query = db.collection("Users").document(uid).collection("Trainings");



       FirestoreRecyclerOptions<TrainingMap> response = new FirestoreRecyclerOptions.Builder<TrainingMap>()
               .setQuery(query, TrainingMap.class)
               .build();

       adapter = new FirestoreRecyclerAdapter<TrainingMap, trainingHolder>(response) {

           @Override
           public trainingHolder onCreateViewHolder (ViewGroup group, int i){
               View view = LayoutInflater.from(group.getContext())
                       .inflate(R.layout.training_list, group, false);
               return new trainingHolder(view);
           }

           @Override
           public void onBindViewHolder (trainingHolder holder, int position, final TrainingMap model){
               if (!DateFormat.is24HourFormat(getApplicationContext())){
                   SimpleDateFormat dateFormatPm = new SimpleDateFormat("EEE dd MMM hh:mm a", Locale.getDefault());
                   String trainingDate1 = dateFormatPm.format(model.getTrainingDate());
                   holder.trainingDate.setText(trainingDate1);
               }else {
                   SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM HH:mm ", Locale.getDefault());
                   String trainingDate1 = dateFormat.format(model.getTrainingDate());
                   holder.trainingDate.setText(trainingDate1);
               }

               long diff = model.getDuration();
               long seconds = diff / 1000;
               long minutes = seconds / 60;
               long hours = minutes / 60;

           if (hours < 1) {
               placeHolder = minutes;
               placeHolder2 = placeHolder + " " + getResources().getString(R.string.minutes);
           } else{
               placeHolder = hours;
               placeHolder0 = minutes - hours*60;
               placeHolder2 = placeHolder + " " + getResources().getString(R.string.h) + " " + placeHolder0 + " " + getResources().getString(R.string.min);
           }
           holder.trainingDuration.setText(placeHolder2);
           holder.trainingReputation.setText(getResources().getString(R.string.reputation, Long.toString(model.getReputation())));
           holder.trainingField.setText(model.getField());

           }

           @Override
           public void onError (FirebaseFirestoreException e){
               Log.e("error", e.getMessage());
           }
       };

       adapter.notifyDataSetChanged();
       trainingsRecycler.setAdapter(adapter);
       adapter.startListening();

   }
}
