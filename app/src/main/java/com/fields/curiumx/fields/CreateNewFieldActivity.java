package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class CreateNewFieldActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    ImageView fieldImage;
    Uri uriFieldImage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    Button save_button;
    ProgressBar progressBar;
    String fieldImageUrl;
    Spinner field_type;
    Spinner field_access_type;
    Spinner goal_count;
    EditText field_name;
    EditText field_area;
    EditText field_address;
    TextView add_text;
    String fieldID;
    TextView mapText;
    ImageView map;
    LayoutInflater layoutInflater;
    ConstraintLayout create_new_field_activity;
    PopupWindow popupWindow;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_field);
        setTitle("Create New Field");

        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        create_new_field_activity = findViewById(R.id.create_field);
        final ConstraintLayout popUp = findViewById(R.id.popup);
        final ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.map_info_popup, popUp);
        popupWindow = new PopupWindow(container, 1000, 600, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);

        popupWindow.setElevation(100);

        mapText = findViewById(R.id.maptext);
        map = findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               popupWindow.showAtLocation(create_new_field_activity, Gravity.CENTER, 0,0 );
               Button okButton1 = container.findViewById(R.id.okButton1);
               okButton1.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent = new Intent();
                       intent.setAction(Intent.ACTION_VIEW);
                       intent.setPackage("com.google.android.apps.maps");



                       startActivity(intent);
                       popupWindow.dismiss();
                   }
               });
            }
        });
        mapText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAtLocation(create_new_field_activity, Gravity.CENTER, 0,0 );
                Button okButton1 = container.findViewById(R.id.okButton1);
                okButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setPackage("com.google.android.apps.maps");

                        startActivity(intent);
                        popupWindow.dismiss();
                    }
                });

            }
        });
        add_text = findViewById(R.id.press_text);
        field_type = findViewById(R.id.field_type);
        goal_count = findViewById(R.id.goal_count);
        field_access_type = findViewById(R.id.access_type);
        final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource
                (this, R.array.field_access_type_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        field_access_type.setAdapter(adapter2);
        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource
                (this, R.array.field_type_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        field_type.setAdapter(adapter1);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.goal_count_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goal_count.setAdapter(adapter);
        progressBar = findViewById(R.id.progress_bar_edit1);
        field_name = findViewById(R.id.display_name_change1);
        field_area = findViewById(R.id.field_area);
        field_address = findViewById(R.id.field_address);
        save_button = findViewById(R.id.save_button);
        fieldImage = findViewById(R.id.fieldPhotoEdit);
        fieldImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fieldNametext = field_name.getText().toString().trim();
                String fieldAreaText = field_area.getText().toString().trim();
                String fieldAdressText = field_address.getText().toString().trim();

                if (fieldNametext.isEmpty()){
                    field_name.setError("Please enter valid field name");
                    field_name.requestFocus();
                }else if (fieldAreaText.isEmpty()){
                    field_area.setError("Please enter the city this field is located at");
                    field_area.requestFocus();
                }else if (fieldAdressText.isEmpty()){
                    field_address.setError("Please enter the address of this field");
                }else {
                    fieldID = UUID.randomUUID().toString();
                    if (uriFieldImage != null){
                        uploadImageToFirebaseStorage();
                    }

                    final FieldMap fieldMap = new FieldMap(fieldNametext, fieldAreaText, fieldAdressText,
                            fieldID, goal_count.getSelectedItem().toString(),
                            field_type.getSelectedItem().toString(), field_access_type.getSelectedItem().toString(), uid, user.getDisplayName());
                    db.collection("Fields").document(fieldID).set(fieldMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Field created successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateNewFieldActivity.this, DetailFieldActivity.class);
                            intent.putExtra("fieldName2", fieldMap.getFieldName());
                            intent.putExtra("fieldAddress", fieldMap.getFieldAddress());
                            intent.putExtra("fieldArea", fieldMap.getFieldArea());
                            intent.putExtra("fieldType", fieldMap.getFieldType());
                            intent.putExtra("fieldAccessType", fieldMap.getAccessType());
                            intent.putExtra("goalCount", fieldMap.getGoalCount());
                            intent.putExtra("creator", fieldMap.getCreator());
                            intent.putExtra("creatorName", fieldMap.getCreatorName());
                            intent.putExtra("fieldID", fieldMap.getFieldID());
                            startActivity(intent);
                            finish();
                        }
                    });

                }
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
            uriFieldImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriFieldImage);
                fieldImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {

        StorageReference fieldImageRef =
                FirebaseStorage.getInstance().getReference("fieldpics/" + fieldID + ".jpg");
        StorageMetadata storageMetadata = new StorageMetadata.Builder()
                .setCustomMetadata("creatorUid", uid)
                .setCustomMetadata("fieldID", fieldID)
                .build();

        if (uriFieldImage != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriFieldImage);
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
                        fieldImageUrl = taskSnapshot.getDownloadUrl().toString();
                        }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
