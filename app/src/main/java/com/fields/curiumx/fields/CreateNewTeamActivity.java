package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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

public class CreateNewTeamActivity extends AppCompatActivity {
    ImageView teamImage;
    Uri uriFieldImage;
    ProgressBar progressBar;
    String teamImageUrl;
    EditText teamUsername;
    EditText teamFullName;
    TextView press_text;
    Spinner teamCountry;
    Button saveTeamButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    Spinner level;
    String teamID;
    ConstraintLayout root;
    TextInputLayout teamUsernameInput;
    TextInputLayout teamFullNameInput;
    private static final int CHOOSE_IMAGE = 101;
    String teamUsernameText;
    String teamFullNameText;
    int teamCountryText;
    String username;
    Boolean memberFieldsPlus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_team);
        teamImage = findViewById(R.id.teamPhoto);
        press_text = findViewById(R.id.press_text_team);
        setTitle(getResources().getString(R.string.create_new_team));
        press_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        teamImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        teamUsername = findViewById(R.id.teamUsername);
        teamFullName = findViewById(R.id.teamFullName);
        teamCountry  = findViewById(R.id.teamCountry);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        saveTeamButton = findViewById(R.id.saveTeamButton);
        root = findViewById(R.id.createNewTeamActivity);
        teamUsernameInput = findViewById(R.id.teamUsernameInput);
        teamFullNameInput = findViewById(R.id.teamFullNameInput);

        teamUsername.addTextChangedListener(new MyTextWatcher(teamUsername));
        teamFullName.addTextChangedListener(new MyTextWatcher(teamFullName));

        saveTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTeamButton.setEnabled(false);
                saveTeam();
            }
        });
        progressBar = findViewById(R.id.pr1);

        //Filters spaces from username
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String filtered = "";
                for (int i = start; i < end; i++) {
                    char character = source.charAt(i);
                    if (!Character.isWhitespace(character)) {
                        filtered += character;
                    }
                }
                return filtered;
            }
        };
        teamUsername.setFilters(new InputFilter[]{filter});

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
         teamUsernameText = teamUsername.getText().toString().toLowerCase().trim();
         teamFullNameText = teamFullName.getText().toString().trim();
         teamCountryText = teamCountry.getSelectedItemPosition();
         username = user.getDisplayName();

        if (teamUsernameText.isEmpty()) {
            teamUsernameInput.setError(getResources().getString(R.string.error_team_username));
            teamUsername.requestFocus();
            saveTeamButton.setEnabled(true);
        }else if (teamFullNameText.isEmpty()){
            teamFullNameInput.setError(getResources().getString(R.string.error_team_full_name));
            teamFullName.requestFocus();
            saveTeamButton.setEnabled(true);
        }else {
            //If team with same name doesn't exist, save it
            db.collection("Users").document(uid).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();

                            //Check if user is already in a team
                            if (documentSnapshot.get("usersTeamID") == null) {
                                progressBar.setVisibility(View.VISIBLE);
                                teamID = UUID.randomUUID().toString().substring(24);
                                memberFieldsPlus = documentSnapshot.getBoolean("fieldsPlus");
                                if (uriFieldImage != null){
                                    uploadImageToFirebaseStorage();
                                }else {
                                    updateData();
                                    }
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.you_are_in_team), Toast.LENGTH_LONG).show();
                                startActivity(new Intent(CreateNewTeamActivity.this, FeedActivity.class));
                                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                                finish();
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

        final StorageReference fieldImageRef =
                FirebaseStorage.getInstance().getReference("teampics/" + teamID + ".jpg");


        if (uriFieldImage != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriFieldImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();

                saveTeamButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                UploadTask uploadTask = fieldImageRef.putBytes(data1);
                uploadTask.addOnSuccessListener(CreateNewTeamActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        updateData();
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
                startActivity(new Intent(CreateNewTeamActivity.this, NoTeamActivity.class));
                finish();
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CreateNewTeamActivity.this, NoTeamActivity.class));
        finish();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()){
                case R.id.teamUsername:
                    teamUsernameInput.setErrorEnabled(false);
                    break;
                case R.id.teamFullName:
                    teamFullNameInput.setErrorEnabled(false);

            }
        }
    }

    public void updateData(){
        TeamMap data1 = new TeamMap(teamUsernameText, teamFullNameText, teamCountryText,
                teamID , level.getSelectedItemPosition(), false);
        db.collection("Teams").document(teamID).set(data1);

        MemberMap memberMap = new MemberMap(username, user.getUid(), memberFieldsPlus);

        db.collection("Teams").document(teamID).collection("TeamUsers").document(uid).set(memberMap);

        db.collection("Users").document(uid).update("usersTeamID", teamID)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(CreateNewTeamActivity.this, TeamActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.team_created_successfully), Toast.LENGTH_LONG).show();
                            finish();

                        }else {
                            String error = getResources().getString(R.string.error_occurred_creating_team);
                            Snackbar.make(root ,error, Snackbar.LENGTH_LONG).show();
                            saveTeamButton.setEnabled(true);


                        }
                    }
                });
    }


}
