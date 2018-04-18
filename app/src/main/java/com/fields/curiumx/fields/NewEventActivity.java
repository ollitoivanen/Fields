package com.fields.curiumx.fields;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NewEventActivity extends Activity {
    Spinner typeSpinner;
    static final int TIME_DIALOG_ID = 1111;
    TextView trainingStartTime;
    TextView trainingStartDate;
    TextView trainingEndTime;
    TextView trainingEndDate;
    Button chooseFieldButton;
    Boolean startBool;
    Boolean endBool;
    Boolean startTime = true;
    Boolean endTime = true;
    final Calendar c = Calendar.getInstance();


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        chooseFieldButton = findViewById(R.id.choose_field_button);
        chooseFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewEventActivity.this, SearchFieldOnlyActivity.class));
            }
        });
        trainingStartTime = findViewById(R.id.training_start_time);
        trainingStartDate = findViewById(R.id.training_start_date);
        trainingEndDate = findViewById(R.id.training_end_date);
        trainingEndTime = findViewById(R.id.training_end_time);
        Date currentTime = Calendar.getInstance().getTime();


        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM");
        trainingStartDate.setText(dateFormat.format(currentTime));
        trainingStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBool = true;
                new DatePickerDialog(NewEventActivity.this, date, c
                        .get(Calendar.YEAR), c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();
                }
        });

        trainingEndDate.setText(dateFormat.format(currentTime));
        trainingEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endBool = true;
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

    public void addButtonClickListener() {

        trainingStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = true;
                createdDialog(1111).show();
            }
        });

        trainingEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTime = true;
                createdDialog(1111).show();
            }
        });
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM");
        if (startBool){
            startBool = false;
            trainingStartDate.setText(dateFormat.format(c.getTime()));
            }else {
            endBool = false;
            trainingEndDate.setText(dateFormat.format(c.getTime()));

        }
    }


    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
// TODO Auto-generated method stub
            hr = hourOfDay;
            min = minutes;
            updateTime(hr, min);
        }
    };

    private static String utilTime(int value) {
        if (value < 10) return "0" + String.valueOf(value);
        else return String.valueOf(value);
    }

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
}
