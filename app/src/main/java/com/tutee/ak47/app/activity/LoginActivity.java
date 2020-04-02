package com.tutee.ak47.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText UserEmail;
    private EditText UserPassword;
    private Button Login;
    private Button Signup;
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[a-zA-Z0-9_.+-]+@(?:(?:[a-zA-Z0-9-]+\\.)?[a-zA-Z]+\\.)?(gla\\.ac)\\.in$", Pattern.CASE_INSENSITIVE);

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(com.tutee.ak47.app.R.layout.activity_login);

        InitializeFields();
        mAuth = FirebaseAuth.getInstance();
        usersRef=FirebaseDatabase.getInstance().getReference().child("Users");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginAccount();
            }
        });


        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToSignUpActivity();
            }

        });
    }


    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return (!TextUtils.isEmpty(email) && matcher.find());
    }


    void LoginAccount() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            UserEmail.setError("Enter Email Id!");
        } else if (TextUtils.isEmpty(password)) {
            UserPassword.setError("Enter Password");
        } else if (!isEmail(UserEmail)) {
            UserEmail.setError("Enter valid Email!");
        } else {

            loadingBar.setTitle("SignIn ");
            loadingBar.setMessage("Please Wait For Login");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {


                                checkIfEmailVerified();

                            } else {

                                String message = Objects.requireNonNull(task.getException()).toString();
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_LONG).show();
                                UserEmail.setError("Enter valid Email!");
                                UserPassword.setError("Enter valid Password");


                            }
                        }
                    });
        }

    }


    private void InitializeFields() {

        UserEmail = findViewById(com.tutee.ak47.app.R.id.input_email);
        UserPassword = findViewById(com.tutee.ak47.app.R.id.input_password);
        Login = findViewById(com.tutee.ak47.app.R.id.btn_login);
        Signup = findViewById(com.tutee.ak47.app.R.id.link_signup);
        loadingBar = new ProgressDialog(this);
    }

    private void sendToSignUpActivity() {
        Intent intent;
        intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }


    private void sendUserToHomeActivity() {
        Intent mainintent = new Intent(LoginActivity.this, MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        if (user.isEmailVerified()) {

         /*   String currentUserId=mAuth.getCurrentUser().getUid();
            String deviceToken =FirebaseInstanceId.getInstance().getToken();
            usersRef.child(currentUserId).child("device_token").
                    setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        // user is verified
                        sendUserToHomeActivity();
                        loadingBar.dismiss();
                        finish();
                        Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();

                    }

                }
            }); */


            // user is verified
            sendUserToHomeActivity();
            loadingBar.dismiss();
            finish();
            Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();

        } else {
            // email is not verified, so just prompt the message to the user and restart this activity.
            Toast.makeText(LoginActivity.this, "Email Id Not verified", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
            FirebaseAuth.getInstance().signOut();

            //restart this activity

        }
    }


}
