package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class EditTeamActivity extends AppCompatActivity {
    ImageView teamPhotoEdit;

    Uri uriTeamImage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String teamUsernameText;
    String teamFullNameText;
    int teamCountryText;
    int teamCountryCurrent;
    int levelCurrent;
    String teamID;
    Spinner level;
    Spinner teamCountry;
    EditText teamUsername;
    EditText teamFullName;
    TextInputLayout teamUsernameInput;
    TextInputLayout teamFullNameInput;
    Button saveButton;
    ProgressBar progressBar;
    TextView pressText;
    StorageReference teamImageRef;
    private static final int CHOOSE_IMAGE = 101;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        pressText = findViewById(R.id.press_text);
        progressBar = findViewById(R.id.progressbar);

        teamUsername = findViewById(R.id.edit_username);
        teamFullName = findViewById(R.id.edit_full_name);
        teamUsername.setFilters(new InputFilter[]{filter});

        teamUsernameInput = findViewById(R.id.edit_username_input);
        teamFullNameInput = findViewById(R.id.edit_full_name_input);
        teamUsername.addTextChangedListener(new MyTextWatcher(teamUsername));
        teamFullName.addTextChangedListener(new MyTextWatcher(teamFullName));

        saveButton = findViewById(R.id.saveTeamButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTeam();
            }
        });
        level = findViewById(R.id.level);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.level_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        level.setAdapter(adapter);

        teamCountry = findViewById(R.id.teamCountry);
        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource
                (this, R.array.country_list, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamCountry.setAdapter(adapter1);

        Bundle info = getIntent().getExtras();
        teamUsernameText = info.getString("teamName");
        teamID = info.getString("teamID");
        teamCountryCurrent = info.getInt("teamCountry");
        teamFullNameText = info.getString("teamFullName");
        teamCountry.setSelection(teamCountryCurrent);
        levelCurrent = info.getInt("level");
        level.setSelection(levelCurrent);
        teamPhotoEdit = findViewById(R.id.teamPhotoEdit);
        teamUsername.setText(teamUsernameText);
        teamFullName.setText(teamFullNameText);

        pressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        teamPhotoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        teamImageRef = FirebaseStorage.getInstance().getReference().child("teampics/" +teamID+".jpg");
        teamImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    GlideApp.with(EditTeamActivity.this)
                            .load(teamImageRef)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(teamPhotoEdit);
                }else {
                    teamPhotoEdit.setImageDrawable(getResources().getDrawable(R.drawable.team_basic));
                }

            }
        });
    }

    public void saveTeam(){

        saveButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
         teamUsernameText = teamUsername.getText().toString().toLowerCase().trim();
         teamFullNameText = teamFullName.getText().toString().trim();
         teamCountryText = teamCountry.getSelectedItemPosition();

        if (teamUsernameText.isEmpty()) {
            teamUsernameInput.setError(getResources().getString(R.string.error_team_username));
            teamUsername.requestFocus();
            saveButton.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }else if (teamFullNameText.isEmpty()){
            teamFullNameInput.setError(getResources().getString(R.string.error_team_full_name));
            teamFullName.requestFocus();
            saveButton.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }else {
            updateData();
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
            uriTeamImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriTeamImage);
                teamPhotoEdit.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {

        teamImageRef = FirebaseStorage.getInstance().getReference().child("teampics/"+teamID+".jpg");


        if (uriTeamImage != null) {
            if (teamImageRef.getDownloadUrl().isSuccessful()){
                teamImageRef.delete();
            }
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriTeamImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();

                saveButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                UploadTask uploadTask = teamImageRef.putBytes(data1);
                uploadTask.addOnSuccessListener(EditTeamActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        saveButton.setEnabled(true);

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateData(){
        db.collection("Teams").document(teamID).update("teamUsernameText", teamUsernameText,
                "teamFullNameText", teamFullNameText,
                "teamCountryText", teamCountryText, "level", level.getSelectedItemPosition());


        db.collection("Users").document(uid).update(
                "usersTeam", teamUsernameText)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(EditTeamActivity.this, TeamActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                            finish();
                        } else {
                            String error = getResources().getString(R.string.error_occurred_loading_document);
                            Snackbar.make(findViewById(R.id.editteam), error, Snackbar.LENGTH_LONG).show();
                            saveButton.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

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
                case R.id.edit_username:
                    teamUsernameInput.setErrorEnabled(false);
                    break;
                case R.id.edit_full_name:
                    teamFullNameInput.setErrorEnabled(false);

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                finish();
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

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
