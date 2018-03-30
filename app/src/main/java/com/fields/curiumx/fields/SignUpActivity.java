package com.fields.curiumx.fields;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.transition.TransitionManager;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

import org.w3c.dom.Text;

import java.nio.file.Path;

public class SignUpActivity extends Activity implements View.OnClickListener{

    EditText emailSignUp, passwordSignUp, username, realName;
    TextView signInTextView;
    TextView welcome;
    Button signUpButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ConstraintLayout container1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("");





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
        final ImageView logo = findViewById(R.id.logo);


        ObjectAnimator animator = ObjectAnimator.ofFloat(logo, "y", 1000f, 0f )
                .setDuration(7000);
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
                                             // See http://stackoverflow.com/a/19615205/321697
                                             logo.post(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     logo.setLayerType(View.LAYER_TYPE_NONE, null);

                                                     container1.setVisibility(View.VISIBLE);
                                                     container1.setAlpha(0.0f);
                                                     container1.animate().alpha(1.0f).setDuration(2000).setListener(new AnimatorListenerAdapter() {
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



                container1 = findViewById(R.id.container1);
        welcome = findViewById(R.id.welcome);
        realName = findViewById(R.id.real_name);
        username = findViewById(R.id.username);
        username.setFilters(new InputFilter[]{filter});
        emailSignUp = findViewById(R.id.emailSignUp);
        emailSignUp.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        passwordSignUp = findViewById(R.id.passwordSignUp);
        signUpButton = findViewById(R.id.signUpButton);
        signInTextView = findViewById(R.id.signInTextView);

        signUpButton.setOnClickListener(this);
        signInTextView.setOnClickListener(this);

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
        final String usernameString = username.getText().toString().trim();
        final String realNameString = realName.getText().toString().trim();

        if (realNameString.isEmpty()){
            realName.setError("Please give real name");
            realName.requestFocus();
        }

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
                                Toast.makeText(getApplicationContext(), "Sign up successful", Toast.LENGTH_SHORT)
                                        .show();

                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();

                                UserMap userMap = new UserMap(usernameString, uid, "Not at any field",
                                        "Not at any field", null, realNameString);
                                db.collection("Users").document(uid).set(userMap);

                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(realNameString)
                                        .build();
                                user.updateProfile(profileChangeRequest);

                                Intent intent = new Intent(SignUpActivity.this, FeedActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
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
                    }else{
                    username.setError("This username is taken");
                    username.requestFocus();
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

            case R.id.signInTextView:
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        }
    }
}
