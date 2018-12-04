package com.example.aman.login;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText UserEmail;
    private EditText UserPassword;
    private Button Login;
    private Button Signup;
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitializeFields();
        mAuth = FirebaseAuth.getInstance();

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
        } else if (isEmail(UserEmail) == false) {
            UserEmail.setError("Enter valid Email!");
        } else {

            loadingBar.setTitle("SignIn ");
            loadingBar.setMessage("Please Wait For Login");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

        /*    mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                sendUserToHomeActivity();
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG);
                                loadingBar.dismiss();

                            } else {

                                String message = task.getException().toString();
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_LONG);
                                UserEmail.setError("Enter valid Email!");
                                UserPassword.setError("Enter valid Password");


                            }
                        }
                    });*/
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                checkIfEmailVerified();

                            } else {

                                String message = task.getException().toString();
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_LONG);
                                UserEmail.setError("Enter valid Email!");
                                UserPassword.setError("Enter valid Password");


                            }
                        }
                    });
        }

    }


    private void InitializeFields() {

        UserEmail = findViewById(R.id.input_email);
        UserPassword = findViewById(R.id.input_password);
        Login = findViewById(R.id.btn_login);
        Signup = findViewById(R.id.link_signup);
        loadingBar = new ProgressDialog(this);
    }

    private void sendToSignUpActivity() {
        Intent intent;
        intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }


    private void sendUserToHomeActivity() {
        Intent mainintent = new Intent(LoginActivity.this, Home.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified()) {
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
