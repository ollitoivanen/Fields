package com.fields.curiumx.fields;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.firebase.analytics.FirebaseAnalytics;

public class DataCollectionActivity extends AppCompatActivity {
    CheckBox check;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collecetion);
        setTitle(getResources().getString(R.string.data_collection));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        check = findViewById(R.id.check_analytics_settings);
        save = findViewById(R.id.saveDataSettings);
        final FirebaseAnalytics f = FirebaseAnalytics.getInstance(DataCollectionActivity.this);
        check.setChecked(true);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check.isChecked()){
                    f.setAnalyticsCollectionEnabled(true);
                }else {
                    f.setAnalyticsCollectionEnabled(false);
                }
                onBackPressed();
            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

}
