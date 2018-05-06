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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class EditFieldActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    Uri uriFieldImage;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    ImageView fieldImage11;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    String fieldImageUrl;

    ProgressBar progressBar;


    String fieldName;
    String fieldID;
    String fieldType;
    String fieldAddress;
    String fieldAccessType;
    String fieldArea;
    String fieldGoalCount;

    TextView fieldNameT;
    TextView fieldAddressT;
    TextView fieldAreaT;
    Spinner fieldTypeT;
    Spinner fieldAccesTypeT;
    Spinner fieldGoalCountT;

    Button saveButton1;

    TextView mapText;
    ImageView map;
    LayoutInflater layoutInflater;
    ScrollView editFieldActivity;
    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_field);

        setTitle("Edit Field Information");

        Bundle data = getIntent().getExtras();
        fieldName  = data.getString("fieldName");
        fieldID = data.getString("fieldID");
        fieldType = data.getString("fieldType");
        fieldAddress = data.getString("fieldAddress");
        fieldAccessType = data.getString("fieldAccessType");
        fieldArea = data.getString("fieldArea");
        fieldGoalCount = data.getString("goalCount");
        fieldID = data.getString("fieldID");

        fieldImage11 = findViewById(R.id.fieldPhotoEdit11);
        fieldImage11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        final StorageReference storageRef = storage.getReference().child("fieldpics/"+fieldID+".jpg");
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Glide.with(getApplicationContext())
                            .load(storageRef)
                            .into(fieldImage11);
                }else {
                    fieldImage11.setImageDrawable(getResources().getDrawable(R.drawable.field_photo3));
                }

            }
        });

        progressBar = findViewById(R.id.progressBar);
        fieldNameT = findViewById(R.id.display_name_change11);
        fieldAddressT = findViewById(R.id.field_address1);
        fieldAreaT = findViewById(R.id.field_area1);
        fieldTypeT = findViewById(R.id.field_type1);
        fieldAccesTypeT = findViewById(R.id.access_type1);
        fieldGoalCountT  =findViewById(R.id.goal_count1);
        saveButton1 = findViewById(R.id.save_button1);

        saveButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fieldNametext = fieldNameT.getText().toString().trim();
                String fieldAreaText = fieldAreaT.getText().toString().trim();
                String fieldAdressText = fieldAddressT.getText().toString().trim();

                if (fieldNametext.isEmpty()){
                    fieldNameT.setError("Please enter valid field name");
                    fieldNameT.requestFocus();
                }else if (fieldAreaText.isEmpty()){
                    fieldAreaT.setError("Please enter the city this field is located at");
                    fieldAreaT.requestFocus();
                }else if (fieldAdressText.isEmpty()){
                    fieldAddressT.setError("Please enter the address of this field");
                }else {


                    if (uriFieldImage != null){
                        uploadImageToFirebaseStorage();
                    }

                    final FieldMap fieldMap = new FieldMap(fieldNametext, fieldAreaText, fieldAdressText,
                            fieldID, fieldGoalCountT.getSelectedItem().toString(),
                            fieldTypeT.getSelectedItem().toString(), fieldAccesTypeT.getSelectedItem().toString(), uid, user.getDisplayName());
                    db.collection("Fields").document(fieldID).set(fieldMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Field created successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditFieldActivity.this, DetailFieldActivity.class);
                            intent.putExtra("fieldName2", fieldMap.getFieldName());
                            intent.putExtra("fieldAddress", fieldMap.getFieldAddress());
                            intent.putExtra("fieldArea", fieldMap.getFieldArea());
                            intent.putExtra("fieldType", fieldMap.getFieldType());
                            intent.putExtra("fieldAccessType", fieldMap.getAccessType());
                            intent.putExtra("goalCount", fieldMap.getGoalCount());
                            intent.putExtra("creator", fieldMap.getCreator());
                            intent.putExtra("creatorName", fieldMap.getCreatorName());
                            intent.putExtra("fieldID", fieldMap.getFieldID());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(intent);
                            finish();


                        }
                    });

                }
            }
        });



        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        editFieldActivity = findViewById(R.id.edit_field_activity);
        final ConstraintLayout popUp = findViewById(R.id.popup);
        final ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.map_info_popup, popUp);
        popupWindow = new PopupWindow(container, 1000, 600, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);

        popupWindow.setElevation(100);

        mapText = findViewById(R.id.maptext1);
        map = findViewById(R.id.map1);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.showAtLocation(editFieldActivity, Gravity.CENTER, 0,0 );
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
                popupWindow.showAtLocation(editFieldActivity, Gravity.CENTER, 0,0 );
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




        final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource
                (this, R.array.field_access_type_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldAccesTypeT.setAdapter(adapter2);
        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource
                (this, R.array.field_type_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldTypeT.setAdapter(adapter1);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.goal_count_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fieldGoalCountT.setAdapter(adapter);


        int spinnerPosition = adapter.getPosition(fieldAccessType);
        fieldAccesTypeT.setSelection(spinnerPosition);
        int spinnerPosition1 = adapter.getPosition(fieldGoalCount);
        fieldGoalCountT.setSelection(spinnerPosition1);
        int spinnerPosition2 = adapter.getPosition(fieldType);
        fieldTypeT.setSelection(spinnerPosition2);

        fieldNameT.setText(fieldName);
        fieldAddressT.setText(fieldAddress);
        fieldAreaT.setText(fieldArea);






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
                fieldImage11.setImageBitmap(bitmap);
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 1, baos);
                byte[] data1 = baos.toByteArray();

                saveButton1.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                UploadTask uploadTask = fieldImageRef.putBytes(data1, storageMetadata);
                uploadTask.addOnSuccessListener(EditFieldActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        saveButton1.setEnabled(true);
                        fieldImageUrl = taskSnapshot.getDownloadUrl().toString();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
