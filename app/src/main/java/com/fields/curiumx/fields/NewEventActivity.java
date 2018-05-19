package com.fields.curiumx.fields;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class NewEventActivity extends AppCompatActivity {
    Spinner typeSpinner;
    static final int TIME_DIALOG_ID = 1111;
    TextView trainingStartTime;
    TextView trainingStartDate;
    String trainingStartDateSave;
    TextView trainingEndTime;
    TextView error;
    TextView error2;
    Button chooseFieldButton;
    Button publishButton;
    TextView chosenFieldText;
    Boolean startTime = true;
    Boolean endTime = true;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    final Calendar c = Calendar.getInstance();
    final Calendar cEnd = Calendar.getInstance();
    String chosenFieldNameIntent;
    ProgressBar progressBar12;
    String eventTimeStart;
    String eventTimeEnd;
    String fieldID;
    ImageView deleteEventField;



    private int hr;
    private int min;

    protected Dialog createdDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                if (!DateFormat.is24HourFormat(this)){
                    return new TimePickerDialog(this, timePickerListener, hr, min, false);
                }else
            return new TimePickerDialog(this, timePickerListener, hr, min, true);

        }
        return null;
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            cEnd.set(Calendar.YEAR, year);
            cEnd.set(Calendar.MONTH, monthOfYear);
            cEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        chooseFieldButton = findViewById(R.id.choose_field_button);
        error = findViewById(R.id.error_message);
        error2 = findViewById(R.id.error_message2);
        setTitle(getResources().getString(R.string.create_new_event));
        deleteEventField = findViewById(R.id.delete_event_field);
        progressBar12 = findViewById(R.id.progress_bar1);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        chooseFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(NewEventActivity.this, SearchFieldOnlyActivity.class), 1);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                }
        });


        publishButton = findViewById(R.id.publish_button);
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (c.getTimeInMillis()-System.currentTimeMillis() < 0 ) {
                    error.setVisibility(View.VISIBLE);
                }else if (cEnd.getTimeInMillis() < c.getTimeInMillis()){
                    error2.setVisibility(View.VISIBLE);

                }else {
                    progressBar12.setVisibility(View.VISIBLE);
                    publishButton.setEnabled(false);
                    db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot ds = task.getResult();
                            final String ref = ds.get("usersTeamID").toString();
                            final String teamName = ds.get("usersTeam").toString();
                            final String eventID = Long.toString(c.getTimeInMillis());
                            eventTimeStart = trainingStartTime.getText().toString();
                            eventTimeEnd = trainingEndTime.getText().toString();


                            if (chosenFieldNameIntent == null) {
                                chosenFieldNameIntent = "";
                                fieldID = "";
                            }else{
                                FieldEventMap fieldEventMap = new FieldEventMap(eventTimeStart,
                                        eventTimeEnd, c.getTime(), typeSpinner.getSelectedItemPosition(), teamName);
                                db.collection("Fields").document(fieldID).collection("fieldEvents")
                                        .document(Long.toString(c.getTimeInMillis())).set(fieldEventMap);
                            }

                            EventMap eventMap = new EventMap(eventID,
                                    typeSpinner.getSelectedItemPosition(),
                                    eventTimeStart, eventTimeEnd, chosenFieldNameIntent, fieldID,
                                    c.getTime(), c.getTimeInMillis());
                            db.collection("Teams").document(ref).collection("Team's Events")
                                    .document(Long.toString(c.getTimeInMillis()))
                                    .set(eventMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    progressBar12.setVisibility(View.GONE);
                                    publishButton.setEnabled(true);
                                    Intent intent = new Intent(NewEventActivity.this, TeamActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                                    finish();
                                }
                            });
                        }
                    });
                }
            }
        });
        trainingStartTime = findViewById(R.id.training_start_time);
        trainingStartDate = findViewById(R.id.training_start_date);
        trainingEndTime = findViewById(R.id.training_end_time);
        chosenFieldText = findViewById(R.id.chosen_field_name);
        chosenFieldText.setVisibility(View.GONE);

        Date currentTime = Calendar.getInstance().getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
        SimpleDateFormat dateFormatSave = new SimpleDateFormat("EEE dd MMM", Locale.US);

        trainingStartDate.setText(dateFormat.format(currentTime));
        trainingStartDateSave = dateFormatSave.format(currentTime);
        trainingStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error.setVisibility(View.GONE);
                error2.setVisibility(View.GONE);

                new DatePickerDialog(NewEventActivity.this, date, c
                        .get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();
                }
        });



        hr = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        updateTime(hr, min);
        addButtonClickListener();


        typeSpinner = findViewById(R.id.type_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.event_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                chosenFieldText.setVisibility(View.VISIBLE);
                deleteEventField.setVisibility(View.VISIBLE);
                chosenFieldNameIntent = data.getStringExtra("fieldName2");
                fieldID = data.getStringExtra("fieldID");
              chosenFieldText.setText(chosenFieldNameIntent);

              deleteEventField.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      chosenFieldNameIntent = null;
                      chosenFieldText.setVisibility(View.GONE);
                      deleteEventField.setVisibility(View.GONE);
                  }
              });
            }
        }
    }


    public void addButtonClickListener() {

        trainingStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error.setVisibility(View.GONE);
                error2.setVisibility(View.GONE);


                startTime = true;
                createdDialog(1111).show();
            }
        });

        trainingEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                error.setVisibility(View.GONE);
                error2.setVisibility(View.GONE);


                endTime = true;
                createdDialog(1111).show();
            }
        });
    }

    private void updateLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM", Locale.getDefault());
        SimpleDateFormat dateFormatSave = new SimpleDateFormat("EEE dd MMM", Locale.US);

        trainingStartDate.setText(dateFormat.format(c.getTime()));
        trainingStartDateSave = dateFormatSave.format(c.getTime());


    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hr = hourOfDay;
            min = minutes;

            if (startTime){
                c.set(Calendar.MINUTE, minutes);
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            }else {
                cEnd.set(Calendar.MINUTE, minutes);
                cEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
            }

            updateTime(hr, min);
        }
    };

    private void updateTime(int hours, int mins) {
        String timeSet = "";
        String minutes = "";
        if (!DateFormat.is24HourFormat(this)) {
            if (hours > 12) {
                hours -= 12;
                timeSet = "PM";
            } else if (hours == 0) {
                hours += 12;
                timeSet = "AM";
            } else if (hours == 12)
                timeSet = "PM";
            else
                timeSet = "AM";
            if (mins < 10)
                minutes = "0" + mins;
            else
                minutes = String.valueOf(mins);
            String aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();
            if (startTime && endTime){
                startTime = false;
                endTime = false;
                trainingStartTime.setText(aTime);
                trainingEndTime.setText(aTime);
                }
           else if (startTime){
                startTime = false;
                trainingStartTime.setText(aTime);
                }else {
                endTime = false;
                trainingEndTime.setText(aTime);
                }
        }else {
            if (mins < 10){
                minutes = "0" + mins;
                }else
                    minutes = String.valueOf(mins);

            String aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();
            if (startTime && endTime){
                startTime = false;
                endTime = false;
                trainingStartTime.setText(aTime);
                trainingEndTime.setText(aTime);
            }
            else if (startTime){
                startTime = false;
                trainingStartTime.setText(aTime);
                }else {
                endTime = false;
                trainingEndTime.setText(aTime);
                }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
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
}
