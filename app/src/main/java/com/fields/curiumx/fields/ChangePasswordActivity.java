package com.fields.curiumx.fields;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    EditText oldPassword;
    EditText newPassword;
    String oldPasswordString;
    String newPasswordString;
    Button changePassword;
    TextInputLayout oldPasswordInput;
    TextInputLayout newPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        oldPasswordInput = findViewById(R.id.old_password_input);
        newPasswordInput = findViewById(R.id.new_password_input);
        oldPassword = findViewById(R.id.old_password);
        newPassword = findViewById(R.id.new_password);
        oldPassword.addTextChangedListener(new MyTextWatcher(oldPassword));
        newPassword.addTextChangedListener(new MyTextWatcher(newPassword));
        setTitle(getResources().getString(R.string.change_password));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        changePassword = findViewById(R.id.change_password_button);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
        }

    public void changePassword() {
        oldPasswordString = oldPassword.getText().toString().trim();
        newPasswordString = newPassword.getText().toString().trim();
        if (oldPasswordString.isEmpty()) {
            oldPasswordInput.setError(getResources().getString(R.string.please_enter_old_password));
            oldPassword.requestFocus();
        } else if (newPasswordString.isEmpty()) {
            newPasswordInput.setError(getResources().getString(R.string.please_enter_new_password));
            newPassword.requestFocus();
        }else if (newPasswordString.length()<6){
            newPasswordInput.setError(getResources().getString(R.string.error_password_length));
        } else {
            final String email = user.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPasswordString);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPasswordString).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Snackbar snackbar_fail = Snackbar
                                            .make(findViewById(R.id.root_change_password),
                                                    getResources().getString(R.string.error_occurred_loading_document), Snackbar.LENGTH_LONG);
                                    snackbar_fail.show();
                                } else {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.password_changed),
                                            Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                                    finish();
                                }
                            }
                        });
                    } else {
                        Snackbar snackbar_su = Snackbar
                                .make(findViewById(R.id.root_change_password), getResources()
                                        .getString(R.string.old_password_is_wrong), Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }
                }
            });
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
                case R.id.old_password:
                    oldPasswordInput.setErrorEnabled(false);
                    break;
                case R.id.new_password:
                    newPasswordInput.setErrorEnabled(false);

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
                onBackPressed();
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
