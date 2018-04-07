package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CreateNewFieldActivity extends Activity {
    private static final int CHOOSE_IMAGE = 101;
    ImageView fieldImage;
    Uri uriProfileImage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    Button save_button;
    ProgressBar progressBar;
    String profileImageUrl;
    Spinner field_type;
    Spinner goal_count;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_field);
        setTitle("Create New Field");
        field_type = findViewById(R.id.field_type);
        goal_count = findViewById(R.id.goal_count);
        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.field_type_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        field_type.setAdapter(adapter1);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.goal_count_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goal_count.setAdapter(adapter);
        progressBar = findViewById(R.id.progress_bar_edit1);
        save_button = findViewById(R.id.save_button);
        fieldImage = findViewById(R.id.fieldPhotoEdit);
        fieldImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
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
                fieldImage.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {

        StorageReference fieldImageRef =
                FirebaseStorage.getInstance().getReference("fieldpics/" + uid + ".jpg");
        StorageMetadata storageMetadata = new StorageMetadata.Builder()
                .setCustomMetadata("creatorUid", uid)
                .build();
        if (user.getPhotoUrl() != null) {
            fieldImageRef.delete();

        }
        if (uriProfileImage != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] data1 = baos.toByteArray();

                save_button.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                UploadTask uploadTask = fieldImageRef.putBytes(data1, storageMetadata);
                uploadTask.addOnSuccessListener(CreateNewFieldActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        save_button.setEnabled(true);
                        profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                        }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
