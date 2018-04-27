package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditProfileActivity extends Activity {

    TextView press_text;
    TextView realname_text;
    Spinner roleSpinner;
    Spinner positionSpinner;
    EditText displayNameChange;
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
    TextView customizeButton;
    private static final int CHOOSE_IMAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        press_text = findViewById(R.id.press_text);
        realname_text = findViewById(R.id.realname_text);
        displayNameChange = findViewById(R.id.display_name_change1);
        position_text = findViewById(R.id.position_text);
        role_text = findViewById(R.id.role_text);


        customizeButton = findViewById(R.id.customize_button);
        customizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfileActivity.this, CustomizeProfileActivity.class));
            }
        });
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
        saveUserButton.setEnabled(false);
        setTitle("Edit Profile");



        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.GONE);
                press_text.setVisibility(View.VISIBLE);
                realname_text.setVisibility(View.VISIBLE);
                displayNameChange.setVisibility(View.VISIBLE);
                position_text.setVisibility(View.VISIBLE);
                role_text.setVisibility(View.VISIBLE);
                customizeButton.setVisibility(View.VISIBLE);
                roleSpinner.setVisibility(View.VISIBLE);
                positionSpinner.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                saveUserButton.setVisibility(View.VISIBLE);
                saveUserButton.setEnabled(true);
                DocumentSnapshot ds = task.getResult();
                int comparePosition = ds.getLong("position").intValue();
                int compareRole = ds.getLong("userRole").intValue();
                positionSpinner.setSelection(comparePosition);
                roleSpinner.setSelection(compareRole);

                Bundle info = getIntent().getExtras();
                String displayName = info.getString("DisplayName");

                displayNameChange.setText(displayName);
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

                loadUserInformation();

            }
        });
    }

    private void loadUserInformation() {

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.profileim));

            }
        }
    }

    public void saveChanges() {
        int playerPosition = positionSpinner.getSelectedItemPosition();
        int userRole = roleSpinner.getSelectedItemPosition();

        final String displayNameString = displayNameChange.getText().toString().trim();

        if (displayNameString.isEmpty()) {
            displayNameChange.setError("Please enter valid name");
            displayNameChange.requestFocus();
        }else{
                progressBar.setVisibility(View.VISIBLE);
                saveUserButton.setEnabled(false);
                db.collection("Users").document(uid).update("displayName", displayNameString,
                        "position", playerPosition, "userRole", userRole)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(displayNameString)
                                            .build();
                                    user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);

                                            finish();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
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
        StorageMetadata storageMetadata = new StorageMetadata.Builder()
                .setCustomMetadata("uid", uid)
                .build();
        if (user.getPhotoUrl() != null) {
            profileImageRef.delete();

        }
        if (uriProfileImage != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 1, baos);
                byte[] data1 = baos.toByteArray();

                saveUserButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                UploadTask uploadTask = profileImageRef.putBytes(data1, storageMetadata);
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
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
