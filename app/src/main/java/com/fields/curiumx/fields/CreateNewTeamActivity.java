package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateNewTeamActivity extends Activity {
    ImageView teamImage;
    Uri uriFieldImage;
    ProgressBar progressBar;
    String teamImageUrl;

    EditText teamName;
    Spinner teamCountry;
    Button saveTeamButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    Spinner level;
    String teamID;
    private static final int CHOOSE_IMAGE = 101;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_team);
        teamImage = findViewById(R.id.teamPhoto);
        teamImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        teamName = findViewById(R.id.teamName);
        teamCountry  = findViewById(R.id.teamCountry);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        saveTeamButton = findViewById(R.id.saveTeamButton);
        saveTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTeam();
            }
        });
        progressBar = findViewById(R.id.pr1);

        level = findViewById(R.id.level);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.level_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level.setAdapter(adapter);

        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource
                (this, R.array.country_list, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamCountry.setAdapter(adapter1);

    }



    public void saveTeam(){
        final String teamNameText = teamName.getText().toString();
        final String teamCountryText = teamCountry.getSelectedItem().toString();
        final String username = user.getDisplayName();

        if (teamNameText.isEmpty()) {
            teamName.setError("Team name is required");
            teamName.requestFocus();

        }else if (teamNameText.equals(" ")){
            teamName.setError("Team name is required");
            teamName.requestFocus();

            //Check if team with the same name already exists
        }else {
            db.collection("Teams").document(teamNameText).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()) {
                        teamName.setError("Team with same name already exists");
                        teamName.requestFocus();

                    } else {
                        //If team with same name doesn't exist, save it
                        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot documentSnapshot = task.getResult();


                                //Check if user is already in a team
                                if (documentSnapshot.get("usersTeam") == null) {
                                    teamID = UUID.randomUUID().toString();
                                    if (uriFieldImage != null){
                                        uploadImageToFirebaseStorage();
                                    }
                                    TeamMap data1 = new TeamMap(teamNameText, teamCountryText,
                                           teamID , level.getSelectedItemPosition());
                                    db.collection("Teams").document(teamID).set(data1);

                                   MemberMap memberMap = new MemberMap(username, user.getUid());

                                    db.collection("Teams").document(teamID).collection("TeamUsers").document(uid).set(memberMap);

                                    db.collection("Users").document(uid).update("usersTeamID", teamID);
                                    db.collection("Users").document(uid).update("usersTeam", teamNameText)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent intent = new Intent(CreateNewTeamActivity.this, TeamActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(getApplicationContext(), "Team created successfully", Toast.LENGTH_LONG).show();
                                                        finish();

                                                    }
                                                }
                                            });

                                } else {
                                    Toast.makeText(getApplicationContext(), "You are already in a team", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriFieldImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriFieldImage);
                teamImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {

        StorageReference fieldImageRef =
                FirebaseStorage.getInstance().getReference("teampics/" + teamID + ".jpg");
        StorageMetadata storageMetadata = new StorageMetadata.Builder()
                .setCustomMetadata("creatorUid", uid)
                .setCustomMetadata("fieldID", teamID)
                .build();

        if (uriFieldImage != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriFieldImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] data1 = baos.toByteArray();

                saveTeamButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                UploadTask uploadTask = fieldImageRef.putBytes(data1, storageMetadata);
                uploadTask.addOnSuccessListener(CreateNewTeamActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        saveTeamButton.setEnabled(true);
                        teamImageUrl = taskSnapshot.getDownloadUrl().toString();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
