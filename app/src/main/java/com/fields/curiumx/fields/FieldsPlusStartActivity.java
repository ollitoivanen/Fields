package com.fields.curiumx.fields;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FieldsPlusStartActivity extends AppCompatActivity {
    Button buyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields_plus_start);
        setTitle("");
        buyButton = findViewById(R.id.buy_button);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView levelUp = findViewById(R.id.fields_plus_text);
        String text1 =
                "<font COLOR=\'#FFFFFF\'><b>" + getResources().getString(R.string.level_up) + "</b></font>" + " " +
                        "<font COLOR=\'#3FACFF\'><b>" + getResources().getString(R.string.plus_comma) + "</b></font>";
        levelUp.setText(text1);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
