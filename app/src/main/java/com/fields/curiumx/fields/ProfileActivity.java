package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public class ProfileActivity extends Activity {
    ImageButton profileButton;
    TextView testCurrentField;
    DocumentReference reference;
    String currentField;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileButton = findViewById(R.id.profile_button);
        profileButton.setImageDrawable(getResources().getDrawable(R.drawable.person_green));

        testCurrentField = findViewById(R.id.testCurrentField);


        String uid = user.getUid();
        reference = FirebaseFirestore.getInstance().collection("Users").document(uid);

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()){
                        currentField = documentSnapshot.getData().toString();
                        testCurrentField.setText(currentField);
                        Log.d(TAG, "document:" + documentSnapshot.getData());
                    }else {
                        Log.d(TAG, "no such file");
                    }
                }else {
                    Log.d(TAG, "get failed with" + task.getException());
                }
            }
        });





    }

    public void onExploreClick(View view) {
        Intent intent = new Intent(this, ExploreActivity.class);
        startActivity(intent);
    }

    public void onProfileClick(View view){
        recreate();
    }

    

    public void onFeedClick(View view){
        LinearLayout activityBar = findViewById(R.id.activityBar);
        Intent intent = new Intent(this, FeedActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, activityBar, "bar");
        startActivity(intent, options.toBundle());


    }
}
