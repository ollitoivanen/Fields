package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends Activity implements View.OnClickListener{

    FirebaseAuth mAuth;
    EditText emailSignIn, passwordSignIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_sign_in);
        super.onCreate(savedInstanceState);

        emailSignIn = findViewById(R.id.emailSignIn);
        emailSignIn.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        passwordSignIn = findViewById(R.id.passwordSignIn);
        Button signInButton = findViewById(R.id.signInButton);
        TextView signUpTextView = findViewById(R.id.signUpTextView);
        TextView forgotPassword = findViewById(R.id.forgotPassword);

        //Check if user is logged in
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(SignInActivity.this, FeedActivity.class));
                }
            }
        });
        signInButton.setOnClickListener(this);
        signUpTextView.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }

    public void userSignIn(){

        String email = emailSignIn.getText().toString().trim();
        String password = passwordSignIn.getText().toString().trim();

        if (email.isEmpty()) {
        emailSignIn.setError("Email is required");
        emailSignIn.requestFocus();
        return;
    }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailSignIn.setError("Please enter a valid email");
            emailSignIn.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordSignIn.setError("Password is required");
            passwordSignIn.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordSignIn.setError("Minimum length of password should be 6");
            passwordSignIn.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    finish();
                    Intent intent = new Intent(SignInActivity.this, FeedActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(SignInActivity.this, "Sign in successful", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    Snackbar.make(findViewById(R.id.login_activity), task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.signInButton:
                userSignIn();
                break;

            case R.id.signUpTextView:
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                break;

            case R.id.forgotPassword:
                startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
                break;
        }
    }
}