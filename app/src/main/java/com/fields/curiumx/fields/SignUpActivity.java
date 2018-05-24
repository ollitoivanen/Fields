package com.fields.curiumx.fields;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Patterns;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    EditText emailSignUp, passwordSignUp, username, realName;
    Spinner roleSpinner;
    TextView signInTextView;
    TextView welcome;
    Button signUpButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ConstraintLayout container1;
    ProgressBar progressBar;
    TextInputLayout usernameInput;
    TextInputLayout realNameInput;
    TextInputLayout emailInput;
    TextInputLayout passwordInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.FieldsThemeAppCompatWelcome);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);
        animateLogo();




        usernameInput = findViewById(R.id.username_input);
        realNameInput = findViewById(R.id.real_name_input);
        emailInput = findViewById(R.id.email_address_input);
        passwordInput = findViewById(R.id.password_input);




        roleSpinner = findViewById(R.id.role);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.role_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);
        welcome = findViewById(R.id.welcome);
        realName = findViewById(R.id.real_name_edit_signup);
        username = findViewById(R.id.username_edit_signup);
        emailSignUp = findViewById(R.id.email_edit_signup);
        emailSignUp.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        passwordSignUp = findViewById(R.id.password_edit_signup);
        signUpButton = findViewById(R.id.signUpButton);
        signInTextView = findViewById(R.id.signInTextView);
        container1 = findViewById(R.id.cont);
        progressBar = findViewById(R.id.progress);

        username.addTextChangedListener(new MyTextWatcher(username));
        realName.addTextChangedListener(new MyTextWatcher(realName));
        emailSignUp.addTextChangedListener(new MyTextWatcher(emailSignUp));
        passwordSignUp.addTextChangedListener(new MyTextWatcher(passwordSignUp));




        signUpButton.setOnClickListener(this);
        signInTextView.setOnClickListener(this);

        //Filters spaces from username
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String filtered = "";
                for (int i = start; i < end; i++) {
                    char character = source.charAt(i);
                    if (!Character.isWhitespace(character)) {
                        filtered += character;
                    }
                }
                return filtered;
            }
        };
        username.setFilters(new InputFilter[]{filter});

        //Sends user to FeedActivity if user is not null
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(SignUpActivity.this, FeedActivity.class));
                }
            }
        });
    }

    public void registerUser() {
        final String emailString = emailSignUp.getText().toString().trim();
        final String passwordString = passwordSignUp.getText().toString().trim();
        final String usernameString = username.getText().toString().trim().toLowerCase();
        final String realNameString = realName.getText().toString().trim();
        final int userRole = roleSpinner.getSelectedItemPosition();
        signUpButton.setEnabled(false);

        if (realNameString.isEmpty()) {
            realNameInput.setError(getResources().getString(R.string.please_enter_real_name));
            realName.requestFocus();
            signUpButton.setEnabled(true);
        } else if (emailString.isEmpty()) {
            emailInput.setError(getResources().getString(R.string.please_enter_valid_email));
            emailSignUp.requestFocus();
            signUpButton.setEnabled(true);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            emailInput.setError(getResources().getString(R.string.please_enter_valid_email));
            emailSignUp.requestFocus();
            signUpButton.setEnabled(true);
        } else if (passwordString.isEmpty()) {
            passwordInput.setError(getResources().getString(R.string.please_enter_valid_password));
            passwordSignUp.requestFocus();
            signUpButton.setEnabled(true);
        } else if (passwordString.length() < 6) {
            passwordInput.setError(getResources().getString(R.string.error_password_length));
            passwordSignUp.requestFocus();
        } else if (usernameString.isEmpty()) {
            usernameInput.setError(getResources().getString(R.string.please_enter_username));
            username.requestFocus();
        } else {


            //Checks if user with same username already already exists
            db.collection("Users").whereEqualTo("username", usernameString).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().isEmpty()) {
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Sign up successful", Toast.LENGTH_SHORT)
                                            .show();

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String uid = user.getUid();

                                    UserMap userMap = new UserMap(usernameString, uid, "",
                                            "", null, realNameString, userRole, "0", -1, null, 0, false, null);
                                    db.collection("Users").document(uid).set(userMap);

                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(usernameString)
                                            .build();
                                    user.updateProfile(profileChangeRequest);

                                    Intent intent = new Intent(SignUpActivity.this, FeedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                                    finish();

                                } else {

                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    } else {
                        username.setError("This username is taken");
                        username.requestFocus();
                    }
                }
            });
        }
    }
    public void animateLogo(){
        final ImageView logo = findViewById(R.id.logo);
        ObjectAnimator animator = ObjectAnimator.ofFloat(logo, "y", 1000f, 0f )
                .setDuration(2000);
        animator.addListener(new Animator.AnimatorListener(){
            @Override
            public void onAnimationStart(Animator animator) {
                logo.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                logo.setLayerType(View.LAYER_TYPE_NONE, null);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                logo.post(new Runnable() {
                    @Override
                    public void run() {
                        logo.setLayerType(View.LAYER_TYPE_NONE, null);
                        container1.setVisibility(View.VISIBLE);
                        container1.setAlpha(0.0f);
                        container1.animate().alpha(1.0f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                            }
                        });
                    }
                });
            }
        });
        animator.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signUpButton:
                registerUser();
                break;

            case R.id.signInTextView:
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);

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
                case R.id.username_edit_signup:
                    usernameInput.setErrorEnabled(false);
                    break;
                case R.id.real_name_edit_signup:
                    realNameInput.setErrorEnabled(false);
                    break;
                case R.id.email_edit_signup:
                    emailInput.setErrorEnabled(false);
                    break;
                case R.id.password_edit_signup:
                    passwordInput.setErrorEnabled(false);
                    break;

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }
}
