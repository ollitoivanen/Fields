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
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
    TextView pressText;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ProgressBar progressBar;
    String fieldName;
    String fieldID;
    int fieldType;
    String fieldAddress;
    int fieldAccessType;
    String fieldArea;
    int fieldGoalCount;
    TextView fieldNameT;
    TextView fieldAddressT;
    TextView fieldAreaT;
    Spinner fieldTypeT;
    Spinner fieldAccesTypeT;
    Spinner fieldGoalCountT;
    Button saveButton1;
    ImageView map;
    TextInputLayout fieldNameInput;
    TextInputLayout fieldAreaInput;
    TextInputLayout fieldAddressInput;
    String[] fieldTypeArray;
    String[] fieldAccessTypeArray;
    String[] fieldGoalCountArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_field);
        setTitle(getResources().getString(R.string.edit_field));

        Bundle data = getIntent().getExtras();
        fieldName  = data.getString("fieldName");
        fieldID = data.getString("fieldID");
        fieldType = data.getInt("fieldType");
        fieldAddress = data.getString("fieldAddress");
        fieldAccessType = data.getInt("fieldAccessType");
        fieldArea = data.getString("fieldArea");
        fieldGoalCount = data.getInt("goalCount");
        fieldID = data.getString("fieldID");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fieldTypeArray = getResources().getStringArray(R.array.field_type_array);
        fieldAccessTypeArray = getResources().getStringArray(R.array.field_access_type_array);
        fieldGoalCountArray = getResources().getStringArray(R.array.goal_count_array);


        fieldImage11 = findViewById(R.id.fieldPhotoEdit11);
        fieldImage11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
        pressText = findViewById(R.id.press_text1);
        pressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        fieldNameInput = findViewById(R.id.field_name_input1);
        fieldAreaInput = findViewById(R.id.field_area_input1);
        fieldAddressInput = findViewById(R.id.field_address_input1);





        final StorageReference storageRef = storage.getReference().child("fieldpics/"+fieldID+".jpg");
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    GlideApp.with(getApplicationContext())
                            .load(storageRef)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(fieldImage11);
                }else {
                    fieldImage11.setImageDrawable(getResources().getDrawable(R.drawable.field_default));
                }

            }
        });

        progressBar = findViewById(R.id.progressBar);
        fieldNameT = findViewById(R.id.field_name1);
        fieldAddressT = findViewById(R.id.field_address1);
        fieldAreaT = findViewById(R.id.field_area1);
        fieldTypeT = findViewById(R.id.field_type1);
        fieldAccesTypeT = findViewById(R.id.access_type1);
        fieldGoalCountT  =findViewById(R.id.goal_count1);
        saveButton1 = findViewById(R.id.save_button1);


        fieldNameT.addTextChangedListener(new MyTextWatcher(fieldNameT));
        fieldAreaT.addTextChangedListener(new MyTextWatcher(fieldAreaT));
        fieldAddressT.addTextChangedListener(new MyTextWatcher(fieldAddressT));

        saveButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String fieldNametext = fieldNameT.getText().toString().trim();
                final String fieldAreaText = fieldAreaT.getText().toString().trim();
                final String fieldAdressText = fieldAddressT.getText().toString().trim();

                if (fieldNametext.isEmpty()){
                    fieldNameInput.setError(getResources().getString(R.string.please_enter_fields_name));
                    fieldNameT.requestFocus();
                }else if (fieldAreaText.isEmpty()){
                    fieldAreaInput.setError(getResources().getString(R.string.please_enter_field_city));
                    fieldAreaT.requestFocus();
                }else if (fieldAdressText.isEmpty()){
                    fieldAreaInput.setError(getResources().getString(R.string.please_enter_field_address));
                    fieldAddressT.requestFocus();
                }else {


                   // if (uriFieldImage != null){
                     //   uploadImageToFirebaseStorage();
                    //}
                    saveButton1.setEnabled(false);
                    db.collection("Fields").document(fieldID).update("fieldName", fieldNametext,
                            "fieldNameLowerCase", fieldNametext.toLowerCase(),"fieldArea", fieldAreaText,
                            "fieldAreaLowerCase", fieldAreaText.toLowerCase(),
                            "fieldAddress", fieldAdressText, "goalCount", fieldGoalCountT.getSelectedItemPosition(),
                            "fieldType", fieldTypeT.getSelectedItemPosition(),
                            "accessType", fieldAccesTypeT.getSelectedItemPosition()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(EditFieldActivity.this, DetailFieldActivity.class);
                                intent.putExtra("fieldName", fieldNametext);
                                intent.putExtra("fieldAddress", fieldAdressText);
                                intent.putExtra("fieldArea", fieldAreaText);
                                intent.putExtra("fieldType", fieldTypeT.getSelectedItemPosition());
                                intent.putExtra("fieldAccessType", fieldAccesTypeT.getSelectedItemPosition());
                                intent.putExtra("goalCount", fieldGoalCountT.getSelectedItemPosition());
                                intent.putExtra("fieldID", fieldID);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();

                            }else {
                                Snackbar.make(findViewById(R.id.edit_field_activity),
                                        getResources()
                                                .getString(R.string.error_occurred_creating_field),
                                        Snackbar.LENGTH_SHORT).show();
                                saveButton1.setEnabled(true);
                            }
                        }
                    });

                }
            }
        });

        map = findViewById(R.id.map1);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
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
        fieldTypeT.setSelection(fieldType);
        fieldAccesTypeT.setSelection(fieldAccessType);
        fieldGoalCountT.setSelection(fieldGoalCount);

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
                uploadImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {

        StorageReference fieldImageRef =
                FirebaseStorage.getInstance().getReference("fieldpics/" + fieldID + ".jpg");


        if (uriFieldImage != null) {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriFieldImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();

                saveButton1.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                UploadTask uploadTask = fieldImageRef.putBytes(data1);
                uploadTask.addOnSuccessListener(EditFieldActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        saveButton1.setEnabled(true);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                case R.id.field_address1:
                    fieldAddressInput.setErrorEnabled(false);
                    break;
                case R.id.field_area1:
                    fieldAreaInput.setErrorEnabled(false);
                    break;
                case R.id.field_name1:
                    fieldNameInput.setErrorEnabled(false);

            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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
