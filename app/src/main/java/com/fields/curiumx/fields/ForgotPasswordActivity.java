package com.fields.curiumx.fields;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Button sendButton = findViewById(R.id.sendButton);
        emailAddressEditText = findViewById(R.id.emailAddress);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAddress = emailAddressEditText.getText().toString();

                if (emailAddress.isEmpty()) {
                    emailAddressEditText.setError("Email is required");
                    emailAddressEditText.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                    emailAddressEditText.setError("Please enter a valid email");
                    emailAddressEditText.requestFocus();

                } else {
                    mAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        Snackbar.make(findViewById(R.id.forgot_password_activity), "Email has been sent", Snackbar.LENGTH_LONG)
                                        .show();

                                    }
                                }
                            });
                }
            }


        });
    }
}



