package com.fields.curiumx.fields;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.xml.transform.Result;

public class DeleteUserActivity extends AppCompatActivity {
    TextInputLayout email;
    TextInputLayout password;
    EditText emailEdit;
    EditText passwordEdit;
    Button deleteAccountButton;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid = user.getUid();
    String teamID;
    ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.delete_progress_bar);
        deleteAccountButton = findViewById(R.id.delete_account_button);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
        setTitle(getResources().getString(R.string.delete_account));
        email = findViewById(R.id.delete_account_email_input);
        password = findViewById(R.id.delete_account_password_input);
        emailEdit = findViewById(R.id.delete_account_email);
        passwordEdit = findViewById(R.id.delete_account_password);
        emailEdit.addTextChangedListener(new MyTextWatcher(emailEdit));
        passwordEdit.addTextChangedListener(new MyTextWatcher(passwordEdit));
    }

    public void deleteAccount(){
        deleteAccountButton.setEnabled(false);
        String emailString = emailEdit.getText().toString().trim();
        Log.d("errr", emailString);
        String passwordString = passwordEdit.getText().toString().trim();
        if (emailString.isEmpty()){
            email.setError(getResources().getString(R.string.please_enter_valid_email));
            emailEdit.requestFocus();
            deleteAccountButton.setEnabled(true);
        }else if(!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()){
            email.setError(getResources().getString(R.string.please_enter_valid_email));
            emailEdit.requestFocus();
            deleteAccountButton.setEnabled(true);

        }else if (passwordString.isEmpty()){
            password.setError(getResources().getString(R.string.please_enter_valid_password));
            passwordEdit.requestFocus();
            deleteAccountButton.setEnabled(true);


        }else {
            final String realEmail = user.getEmail();
            if (emailString.equals(realEmail)) {
                AuthCredential credential = EmailAuthProvider.getCredential(realEmail, passwordString);
                user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.VISIBLE);
                        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot ds = task.getResult();
                                if (ds.get("usersTeamID")!=null && !ds.get("usersTeamID").equals("")){
                                    teamID = ds.get("usersTeamID").toString();
                                    db.collection("Teams").document(teamID).collection("TeamUsers").document(uid)
                                            .delete();

                                }

                                db.collection("Friends").whereEqualTo("adder", uid).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                       int size =  task.getResult().getDocuments().size();
                                       for (int i = 0; i<size;){
                                           DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(i);
                                           documentSnapshot.getReference().delete();
                                                   i++;

                                       }
                                    }
                                });

                                db.collection("Friends").whereEqualTo("target", uid).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                int size =  task.getResult().getDocuments().size();
                                                for (int i =0; i<size;){
                                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(i);
                                                    documentSnapshot.getReference().delete();
                                                    i++;

                                                }
                                            }
                                        });

                                db.collection("Users").document(uid).collection("Friends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        int size = task.getResult().size();
                                        for (int i = 0;i<size;){
                                            task.getResult().getDocuments().get(i).getReference().delete();
                                            i++;
                                        }
                                    }
                                });


                                db.collection("Users").document(uid).collection("favoriteFields")
                                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                        int size = queryDocumentSnapshots.size();
                                        for (int i = 0;i<size;) {
                                            queryDocumentSnapshots.getDocuments().get(i).getReference().delete();
                                            i++;

                                        }
                                    }
                                });



                                StorageReference profileImageRef =
                                        FirebaseStorage.getInstance().getReference("profilepics/" + uid + "/" + uid + ".jpg");
                                if (profileImageRef.getDownloadUrl().isSuccessful()) {

                                    profileImageRef.delete();
                                }

                               /* db.collection("Users").document(uid).collection("UserMessages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                                        int size = task.getResult().size();
                                        for (int i = 0;i<size;){
                                            task.getResult().getDocuments().get(i).getReference().delete();

                                            DocumentReference dr = task.getResult().getDocuments().get(i).getReference();
                                            dr.collection("TheseMessages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                                    int size2 = task2.getResult().size();
                                                    for (int j = 0; j<size2;){
                                                        task2.getResult().getDocuments().get(j).getReference().delete();
                                                        j++;

                                                    }
                                                }
                                            });
                                            i++;
                                        }
                                    }
                                });*/



                                if (!ds.get("currentFieldName").equals("")) {
                                    db.collection("Users").document(uid).collection("currentTraining").document(uid).delete();
                                }





                                db.collection("Users").document(uid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {

                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(),
                                                        getResources().getString(R.string.account_deleted), Toast.LENGTH_LONG).show();

                                                startActivity(new Intent(DeleteUserActivity.this, SignUpActivity.class));
                                                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                                                finish();
                                            }
                                        });
                                    }
                                });

                            }
                        });







                    }
                }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), getResources()
                            .getString(R.string.error_email_password), Toast.LENGTH_LONG).show();
                    deleteAccountButton.setEnabled(true);

                }
            });


            }else {
                Toast.makeText(getApplicationContext(), getResources()
                        .getString(R.string.error_email_password), Toast.LENGTH_LONG).show();
                deleteAccountButton.setEnabled(true);

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
                case R.id.delete_account_email:
                    email.setErrorEnabled(false);
                    break;
                case R.id.delete_account_password:
                    password.setErrorEnabled(false);

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }
}
