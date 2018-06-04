package com.fields.curiumx.fields;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ReputationActivity extends AppCompatActivity {
    TextView reputationText;
    long reputationInt;
    ImageView badgeImage;
    long nextBadge;
    long diff;
    TextView untillBadgeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reputation);
        setTitle(getResources().getString(R.string.reputation_text));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reputationText = findViewById(R.id.reputation_text);
        badgeImage = findViewById(R.id.badge_image);
        untillBadgeText = findViewById(R.id.until_text);

        Bundle info = getIntent().getExtras();
        reputationInt = info.getLong("reputation");
        reputationText.setText(getResources().getString(R.string.reputation, Long.toString(reputationInt)));

        if (reputationInt < 500) {
            nextBadge = 500;

            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_bronze_1));
        } else if (reputationInt < 1500) {
            nextBadge = 1500;
            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_silver_2));
        } else if (reputationInt < 3000) {
            nextBadge = 3000;

            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_gold_3));
        } else if (reputationInt < 6000) {
            nextBadge = 6000;

            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_4));
        } else if (reputationInt < 10000) {
            nextBadge = 10000;

            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_5));
        } else if (reputationInt < 15000) {
            nextBadge = 15000;

            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_6));
        } else if (reputationInt < 21000) {
            nextBadge = 21000;

            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_7));
        } else if (reputationInt < 28000) {
            nextBadge = 28000;

            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_8));
        } else if (reputationInt < 38000) {
            nextBadge = 38000;

            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_9));
        } else if (reputationInt < 48000) {
            nextBadge = 48000;

            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_10));
        } else if (reputationInt < 58000) {
            nextBadge = 58000;

            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_11));
        } else if (reputationInt < 70000) {
            nextBadge = 70000;

            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_12));
        } else if (reputationInt < 85000) {
            nextBadge = 85000;

            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_13));
        } else if (reputationInt >= 85000) {
            nextBadge = -100;

            badgeImage.setImageDrawable(getResources().getDrawable(R.drawable.badge_14));
        }

        if (nextBadge!=-100){
            diff = nextBadge - reputationInt;
            untillBadgeText.setText(getResources().getString(R.string.until_badge, Long.toString(diff)));
        }else {
            untillBadgeText.setText(getResources().getString(R.string.max_badge));

        }


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
