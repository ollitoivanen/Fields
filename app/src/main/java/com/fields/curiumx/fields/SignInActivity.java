package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

public class SignInActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth mAuth;
    EditText emailSignIn, passwordSignIn;
    TextInputLayout emailSignInInput, passwordSignInInput;
    Button signInButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.FieldsThemeAppCompatWelcome);
        setContentView(R.layout.activity_sign_in);
        super.onCreate(savedInstanceState);

        emailSignIn = findViewById(R.id.emailSignIn);
        emailSignIn.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        passwordSignIn = findViewById(R.id.passwordSignIn);
        signInButton = findViewById(R.id.signInButton);
        TextView signUpTextView = findViewById(R.id.signUpTextView);
        TextView forgotPassword = findViewById(R.id.forgotPassword);

        emailSignInInput = findViewById(R.id.emailSignIn_input);
        passwordSignInInput = findViewById(R.id.passwordSignIn_input);
        emailSignIn.addTextChangedListener(new MyTextWatcher(emailSignIn));
        passwordSignIn.addTextChangedListener(new MyTextWatcher(passwordSignIn));

        //Check if user is logged in
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(SignInActivity.this, FeedActivity.class));
                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                }
            }
        });
        signInButton.setOnClickListener(this);
        signUpTextView.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }

    public void userSignIn() {

        String email = emailSignIn.getText().toString().trim();
        String password = passwordSignIn.getText().toString().trim();
        signInButton.setEnabled(false);


        if (email.isEmpty()) {
            emailSignInInput.setError(getResources().getString(R.string.please_enter_valid_email));
            emailSignIn.requestFocus();
            signInButton.setEnabled(true);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailSignInInput.setError(getResources().getString(R.string.please_enter_valid_email));
            emailSignIn.requestFocus();
            signInButton.setEnabled(true);
        } else if (password.isEmpty()) {
            passwordSignInInput.setError(getResources().getString(R.string.please_enter_valid_password));
            passwordSignIn.requestFocus();
            signInButton.setEnabled(true);
        } else if (password.length() < 6) {
            passwordSignInInput.setError(getResources().getString(R.string.error_password_length));
            passwordSignIn.requestFocus();
            signInButton.setEnabled(true);
        } else {

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        finish();
                        Intent intent = new Intent(SignInActivity.this, FeedActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

                    } else {
                        Snackbar.make(findViewById(R.id.login_activity), getResources()
                                .getString(R.string.error_email_password), Snackbar.LENGTH_SHORT).show();
                        signInButton.setEnabled(true);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.signInButton:
                userSignIn();
                break;

            case R.id.signUpTextView:
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                break;

            case R.id.forgotPassword:
                startActivity(new Intent(SignInActivity.this, ForgotPasswordActivity.class));
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
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
                case R.id.emailSignIn:
                    emailSignInInput.setErrorEnabled(false);
                    break;
                case R.id.passwordSignIn:
                    passwordSignInInput.setErrorEnabled(false);
                    break;

            }
        }
    }
}