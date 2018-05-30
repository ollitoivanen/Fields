package com.fields.curiumx.fields;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    TextView press_text;
    Spinner roleSpinner;
    Spinner positionSpinner;
    EditText usernameEdit;
    EditText realNameEdit;
    TextView position_text;
    TextView role_text;
    Button saveUserButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    ImageView imageView;
    Uri uriProfileImage;
    String profileImageUrl;
    ProgressBar progressBar;
    private static final int CHOOSE_IMAGE = 101;
    String[] userRoleArray;
    String [] userPositionArray;
    String username;
    String realName;
    int position;
    int role;
    TextInputLayout usernameInput;
    TextInputLayout realNameInput;
    ImageView deleteImage;
    String teamID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        userRoleArray = getResources().getStringArray(R.array.role_spinner);
        userPositionArray = getResources().getStringArray(R.array.position_spinner);
        deleteImage = findViewById(R.id.delete_image);


        press_text = findViewById(R.id.press_text);
        usernameEdit = findViewById(R.id.username_edit);
        realNameEdit = findViewById(R.id.real_name_edit);

        usernameInput = findViewById(R.id.username_input);
        realNameInput = findViewById(R.id.real_name_input);

        usernameEdit.addTextChangedListener(new MyTextWatcher(usernameEdit));
        realNameEdit.addTextChangedListener(new MyTextWatcher(realNameEdit));

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        roleSpinner = findViewById(R.id.role_spinner);
        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.role_spinner, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter1);
        positionSpinner = findViewById(R.id.player_position);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.position_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positionSpinner.setAdapter(adapter);
        imageView = findViewById(R.id.profilePhotoEdit);
        progressBar = findViewById(R.id.progress_bar_edit);
        saveUserButton = findViewById(R.id.save_button);
        setTitle(getResources().getString(R.string.edit_profile_string));

        Bundle info = getIntent().getExtras();
        username = info.getString("username");
        realName = info.getString("realName");
        position = info.getInt("userPosition");
        role = info.getInt("userRole");
        teamID = info.getString("teamID");
        usernameEdit.setText(username);
        realNameEdit.setText(realName);
        roleSpinner.setSelection(role);

        if (position!=-1){
            positionSpinner.setSelection(position);
        }
        saveUserButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveChanges();
                    }
                });
        imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImageChooser();
                    }
                });
        press_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        loadUserInformation();
    }

    public void deleteImage(){
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(null)
                        .build();
                user.updateProfile(profileChangeRequest);
                StorageReference profileImageRef =
                        FirebaseStorage.getInstance().getReference("profilepics/" + uid + ".jpg");
                profileImageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        deleteImage.setVisibility(View.GONE);
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.profile_default));


                    }
                });
            }
        });
    }

    private void loadUserInformation() {

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                GlideApp.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
                deleteImage.setVisibility(View.VISIBLE);
                deleteImage();
            }else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.profile_default));

            }
        }
    }

    public void saveChanges() {
        int playerPosition = positionSpinner.getSelectedItemPosition();
        int userRole = roleSpinner.getSelectedItemPosition();
        final String usernameString = usernameEdit.getText().toString().trim().toLowerCase();
        final String realNameString = realNameEdit.getText().toString().trim();


        if (usernameString.isEmpty()) {
            usernameInput.setError(getResources().getString(R.string.please_enter_username));
            usernameEdit.requestFocus();
        }else if (realNameString.isEmpty()){
            realNameInput.setError(getResources().getString(R.string.please_enter_real_name));
            realNameEdit.requestFocus();
        }else{
                progressBar.setVisibility(View.VISIBLE);
                saveUserButton.setEnabled(false);
                db.collection("Users").document(uid).update("realName", realNameString,
                        "position", playerPosition, "userRole", userRole, "username", usernameString)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    db.collection("Teams").document(teamID).collection("TeamUsers")
                                            .document(uid).update("usernameMember", usernameString)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(usernameString)
                                                    .build();
                                            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                                                    finish();
                                                }
                                            });
                                        }
                                    });


                                } else {
                                    Toast.makeText(getApplicationContext(), getResources()
                                            .getString(R.string.error_occurred_loading_document), Toast.LENGTH_SHORT).show();
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
            uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {

        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("profilepics/" + uid + ".jpg");

        if (user.getPhotoUrl() != null) {
            profileImageRef.delete();

        }
        if (uriProfileImage != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();

                saveUserButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                UploadTask uploadTask = profileImageRef.putBytes(data1);
                uploadTask.addOnSuccessListener(EditProfileActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        saveUserButton.setEnabled(true);
                        profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(profileImageUrl))
                                .build();
                        user.updateProfile(profileChangeRequest);
                        deleteImage.setVisibility(View.VISIBLE);
                        deleteImage();

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                case R.id.username_edit:
                    usernameInput.setErrorEnabled(false);
                    break;
                case R.id.real_name_edit:
                    realNameInput.setErrorEnabled(false);

            }
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
}
