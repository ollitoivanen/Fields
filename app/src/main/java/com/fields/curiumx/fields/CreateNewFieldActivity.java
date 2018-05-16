package com.fields.curiumx.fields;

import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
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
    Spinner field_type;
    Spinner field_access_type;
    Spinner goal_count;
    EditText field_name;
    EditText field_area;
    EditText field_address;
    TextView add_text;
    String fieldID;
    ImageView map;
    TextInputLayout fieldNameInput;
    TextInputLayout fieldAreaInput;
    TextInputLayout fieldAddressInput;
    String fieldNameText;
    String fieldAreaText;
    String fieldAddressText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_field);
        setTitle(getResources().getString(R.string.create_new_field));
        field_name = findViewById(R.id.field_name);
        field_area = findViewById(R.id.field_area);
        field_address = findViewById(R.id.field_address);
        fieldNameInput = findViewById(R.id.field_name_input);
        fieldAreaInput = findViewById(R.id.field_area_input);
        fieldAddressInput = findViewById(R.id.field_address_input);
        field_name.addTextChangedListener(new MyTextWatcher(field_name));
        field_area.addTextChangedListener(new MyTextWatcher(field_area));
        field_address.addTextChangedListener(new MyTextWatcher(field_address));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        map = findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
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

        save_button = findViewById(R.id.save_button);
        add_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
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
                fieldNameText = field_name.getText().toString().trim();
                fieldAreaText = field_area.getText().toString().trim();
                fieldAddressText = field_address.getText().toString().trim();
                save_button.setEnabled(false);

                if (fieldNameText.isEmpty()){
                    fieldNameInput.setError(getResources().getString(R.string.please_enter_fields_name));
                    field_name.requestFocus();
                    save_button.setEnabled(true);

                }else if (fieldAreaText.isEmpty()){
                    fieldAreaInput.setError(getResources().getString(R.string.please_enter_field_city));
                    field_area.requestFocus();
                    save_button.setEnabled(true);

                }else if (fieldAddressText.isEmpty()){
                    fieldAreaInput.setError(getResources().getString(R.string.please_enter_field_address));
                    field_address.requestFocus();
                    save_button.setEnabled(true);
                }else {
                    fieldID = UUID.randomUUID().toString().substring(24);
                    if (uriFieldImage != null){
                        uploadImageToFirebaseStorage();
                    }else {
                        updateData();
                    }



                }
            }
        });
    }

    public void updateData(){
        progressBar.setVisibility(View.VISIBLE);
        final FieldMap fieldMap = new FieldMap(fieldNameText, fieldAreaText, fieldAddressText,
                fieldID, goal_count.getSelectedItemPosition(),
                field_type.getSelectedItemPosition(), field_access_type.getSelectedItemPosition());
        db.collection("Fields").document(fieldID).set(fieldMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), getResources()
                            .getString(R.string.field_created_successfully), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateNewFieldActivity.this, DetailFieldActivity.class);
                    intent.putExtra("fieldName", fieldMap.getFieldName());
                    intent.putExtra("fieldAddress", fieldMap.getFieldAddress());
                    intent.putExtra("fieldArea", fieldMap.getFieldArea());
                    intent.putExtra("fieldType", fieldMap.getFieldType());
                    intent.putExtra("fieldAccessType", fieldMap.getAccessType());
                    intent.putExtra("goalCount", fieldMap.getGoalCount());
                    intent.putExtra("fieldID", fieldMap.getFieldID());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                    finish();
                }else {
                    Snackbar.make(findViewById(R.id.scroll), getResources()
                                    .getString(R.string.error_occurred_creating_field),
                            Snackbar.LENGTH_SHORT).show();
                    save_button.setEnabled(true);
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
                case R.id.field_address:
                    fieldAddressInput.setErrorEnabled(false);
                    break;
                case R.id.field_area:
                    fieldAreaInput.setErrorEnabled(false);
                    break;
                case R.id.field_name:
                    fieldNameInput.setErrorEnabled(false);

            }
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
                fieldImage.setImageBitmap(bitmap);
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

                save_button.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                UploadTask uploadTask = fieldImageRef.putBytes(data1);
                uploadTask.addOnSuccessListener(CreateNewFieldActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        save_button.setEnabled(true);
                        updateData();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(CreateNewFieldActivity.this, SearchActivity.class));
                finish();
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
