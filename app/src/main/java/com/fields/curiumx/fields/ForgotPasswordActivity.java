package com.fields.curiumx.fields;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static android.content.ContentValues.TAG;

public class ForgotPasswordActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String emailAddress;
    EditText emailAddressEditText;
    TextInputLayout emailAddressInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button sendButton = findViewById(R.id.sendButton);
        emailAddressEditText = findViewById(R.id.emailAddress);
        emailAddressInput = findViewById(R.id.email_address_input);
        emailAddressEditText.addTextChangedListener(new MyTextWatcher(emailAddressEditText));
        setTitle("");



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAddress = emailAddressEditText.getText().toString();

                if (emailAddress.isEmpty()) {
                    emailAddressInput.setError(getResources().getString(R.string.please_enter_valid_email));
                    emailAddressEditText.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                    emailAddressInput.setError(getResources().getString(R.string.please_enter_valid_email));
                    emailAddressEditText.requestFocus();

                } else {
                    mAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        Snackbar.make(findViewById(R.id.forgot_password_activity),
                                                getResources().getString(R.string.email_sent), Snackbar.LENGTH_LONG)
                                        .show();

                                    }
                                }
                            });
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
            switch (view.getId()) {
                case R.id.emailAddress:
                    emailAddressInput.setErrorEnabled(false);
                    break;
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



