package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class WelcomeActivity extends Activity {

    private static final int CHOOSE_IMAGE = 1;
    ImageView profilePic;
    EditText username;
    Button saveButton;
    TextView verify;
    Uri uriProfileImage;
    String profileImageUrl;
    FirebaseAuth mAuth;
    FirebaseUser user;

    private void loadUserInformation() {


        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(profilePic);
            }

            if (user.getDisplayName() != null) {
                username.setText(user.getDisplayName());
            }

            if (user.isEmailVerified()) {
                verify.setText("Email Verified");
            } else {
                verify.setText("Email Not Verified (Click to Verify)");
                verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(WelcomeActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

mAuth = FirebaseAuth.getInstance();
user = mAuth.getCurrentUser();
    profilePic = findViewById(R.id.profilePic);
    username = findViewById(R.id.username);
        saveButton = findViewById(R.id.saveButton);
        verify = findViewById(R.id.verify);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();

            }
        });

        loadUserInformation();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });





    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, SignUpActivity.class));
        }
    }




    private void saveUserInformation() {


        String displayName = username.getText().toString();

        if (displayName.isEmpty()) {
            username.setError("Name required");
            username.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();



            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(WelcomeActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(WelcomeActivity.this, FeedActivity.class));
                            }
                        }
                    });
        }


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                profilePic.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


        private void uploadImageToFirebaseStorage() {
            StorageReference profileImageRef =
                    FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

            if (uriProfileImage != null) {
                profileImageRef.putFile(uriProfileImage)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(WelcomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
