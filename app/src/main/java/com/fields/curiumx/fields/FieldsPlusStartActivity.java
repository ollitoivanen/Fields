package com.fields.curiumx.fields;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseData;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.anjlab.android.iab.v3.PurchaseState;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class FieldsPlusStartActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler{
    Button buyButton;
    BillingProcessor bp;
    ImageView fp;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid = user.getUid();
    String teamID;


    String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhP70LSlF/j2XxzB5EERbyj1J/N8l6EJS8tCWLtbaB7a72Rr7uYWex6CgtQ2gGsRSpInGa1dOyjT9cV+JvKNVTv/WyhIEpcFQJiI2rlQcAkAWNivaffsBxUfODq6Xp2urNdgQ/35CTp/wYm75oHxE9nnqpI4X0Jk1iUKKBew8DIo2JUh9ezjruk2b+txmFTyDi0Fdm6yLmLUL0eed0mU5KrQO0FO5OHI990bCfQPIoZGKA7FPbiWSS09rn36j3HinD4fc2L52LgIwvz4vcWyMRmCioWygxpMnyUs+TP0C3mXrdJiZkrmYig5T1zgtdy4wru5EOtW6qYwSYsj64WAS1wIDAQAB";








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fields_plus_start);
        setTitle("");
        buyButton = findViewById(R.id.buy_button);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bp = new BillingProcessor(this, key, this);
        bp.initialize(); // binds
        fp = findViewById(R.id.fields_plus_art);

                buyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isAvailable = BillingProcessor.isIabServiceAvailable(getApplicationContext());
                        if(isAvailable) {
                            buyButton.setEnabled(false);
                            boolean isSubsUpdateSupported = bp.isSubscriptionUpdateSupported();
                            if(isSubsUpdateSupported) {
                                bp.subscribe(FieldsPlusStartActivity.this, "fields_plus");


                            }

                        }

                    }
                });








        TextView levelUp = findViewById(R.id.fields_plus_text);
        String text1 =
                "<font COLOR=\'#FFFFFF\'><b>" + getResources().getString(R.string.level_up) + "</b></font>" + " " +
                        "<font COLOR=\'#3FACFF\'><b>" + getResources().getString(R.string.plus_comma) + "</b></font>";
        levelUp.setText(Html.fromHtml(text1))   ;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

        db.collection("Users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

        @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        DocumentSnapshot ds = task.getResult();
        teamID = ds.get("usersTeamID").toString();

        db.collection("Users").document(uid).update("fieldsPlus", true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (teamID != null) {
                    db.collection("Teams").document(teamID).collection("TeamUsers").document(uid)
                            .update("memberFieldsPlus", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            buyButton.setEnabled(true);
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(FieldsPlusStartActivity.this);
                            View mView = getLayoutInflater().inflate(R.layout.fields_plus_thank_you_popup, null);
                            Button con = mView.findViewById(R.id.continue_button);
                            con.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    finish();
                                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                                }
                            });


                            alertDialog.setView(mView);
                            final AlertDialog dialog = alertDialog.create();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }
                    });


                }
            }
        });
    }
});



        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        buyButton.setEnabled(true);

        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }


}

