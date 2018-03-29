package com.fields.curiumx.fields;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.ContentValues.TAG;

public class SignUpActivity extends Activity implements View.OnClickListener{

    EditText emailSignUp, passwordSignUp, username;
    String teamName = null;

    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 2;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.username);
        username.setFilters(new InputFilter[]{filter});
        emailSignUp = findViewById(R.id.emailSignUp);
        emailSignUp.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        passwordSignUp = findViewById(R.id.passwordSignUp);
        Button signUpButton = findViewById(R.id.signUpButton);
        TextView signInTextView = findViewById(R.id.signInTextView);
        SignInButton googleSignInButton = findViewById(R.id.googleSignIn);
        setGooglePlusButtonText(googleSignInButton, "Sign up with Google account");

        googleSignInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        signInTextView.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
        final String usernameString =  username.getText().toString().trim();



        if (emailString.isEmpty()) {
            emailSignUp.setError("Email is required");
            emailSignUp.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            emailSignUp.setError("Please enter a valid email");
            emailSignUp.requestFocus();
            return;
        }

        if (passwordString.isEmpty()) {
            passwordSignUp.setError("Password is required");
            passwordSignUp.requestFocus();
            return;
        }

        if (passwordString.length() < 6) {
            passwordSignUp.setError("Minimum length of password should be 6");
            passwordSignUp.requestFocus();
            return;
        }

        if (usernameString.isEmpty()){
            username.setError("Name is required");
            username.requestFocus();
            return;
        }

        //Checks if user with same username already already exists
        db.collection("Users").whereEqualTo("username", usernameString).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().isEmpty()){
                    mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT)
                                        .show();

                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();

                                UserMap userMap = new UserMap(usernameString, uid, "Not at any field",
                                        "Not at any field", null, null);
                                db.collection("Users").document(uid).set(userMap);
                                startActivity(new Intent(SignUpActivity.this, FeedActivity.class));

                            } else {

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                                    } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    }else{
                    username.setError("This username is taken");
                    username.requestFocus();
                }
            }
        });
        }


    //Google signup stuff
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        } else Toast.makeText(SignUpActivity.this, "Auth went wrong", Toast.LENGTH_SHORT);
        }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            final String uid = user.getUid();
                            db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (!task.getResult().exists()){

                                        String displayName = user.getDisplayName().toLowerCase().trim();
                                        String displayName1 = displayName.replace(" ", "");
                                        UserMap userMap = new UserMap(displayName1, uid,
                                                "Not at any field",
                                                "Not at any field", null, user.getDisplayName());

                                        db.collection("Users").document(uid).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(SignUpActivity.this, "Sign Up successful", Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(SignUpActivity.this, "Sign In successful", Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.signUpActivity), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.signUpButton:
                registerUser();
                break;

            case R.id.googleSignIn:
                signIn();
                break;

            case R.id.signInTextView:
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        }
    }

    //Set the text of Google sign in button
    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }
    //Set the text of Google sign in button
}
