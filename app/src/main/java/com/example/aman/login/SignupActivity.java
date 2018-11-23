package com.example.aman.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    private EditText UserName;
    private EditText UserEmail;
    private EditText UserPhone;
    private EditText UserPassword;
    private Button loginButton;
    private Button signupButton;
    private String currentUserID;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference rootRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        InitializeFields();

        //Firebase
        mAuth = FirebaseAuth.getInstance();
       // currentUserID=mAuth.getCurrentUser().getUid();
        rootRef=FirebaseDatabase.getInstance().getReference();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }

        });
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }


    private void InitializeFields() {
        UserName = findViewById(R.id.input_name);
        UserEmail = findViewById(R.id.input_email);
        UserPhone = findViewById(R.id.input_phone);
        UserPassword = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.btn_login);
        signupButton = findViewById(R.id.link_signup);
        loadingBar = new ProgressDialog(this);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    void createNewAccount() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        final String name = UserName.getText().toString();
        final String phone = UserPhone.getText().toString();

         if (TextUtils.isEmpty(name)) {
            UserName.setError("Enter Name");
        }
        if (TextUtils.isEmpty(phone)) {
            UserPhone.setError("Enter Mobile No.");
        }
        if (TextUtils.isEmpty(email)) {
            UserEmail.setError("Enter Email Id!");
        } else if (TextUtils.isEmpty(password)) {
            UserPassword.setError("Enter Password");
        } else if (password.length() < 8) {
            UserPassword.setError("Password length Must Greater the 8 Character");
        } else if (isEmail(UserEmail) == false) {
            UserEmail.setError("Enter valid Email!");
        } else {

            loadingBar.setTitle("Creating  New Account ");
            loadingBar.setMessage("Please Wait");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();



            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String currenUserID=mAuth.getCurrentUser().getUid();
                                HashMap<String,String> profileMap=new HashMap<>();
                                //profileMap.put("uid",currentUserID);
                                profileMap.put("name",name);
                                profileMap.put("mobile",phone);
                                rootRef.child("User").child(currentUserID).setValue(profileMap);



                                //rooRef.child("User").child(currenUserID).setValue("");





                                sendUserToHomeActivity();
                                Toast.makeText(SignupActivity.this, "Account  Created Successfully", Toast.LENGTH_LONG);
                                loadingBar.dismiss();
                            } else {

                                String message = task.getException().toString();
                                Toast.makeText(SignupActivity.this, "Error : " + message, Toast.LENGTH_LONG);
                                loadingBar.dismiss();

                            }

                        }
                    });

        }

    }

    private void sendUserToLoginActivity() {
        Intent loginintent = new Intent(SignupActivity.this, Home.class);

        startActivity(loginintent);

    }

    private void sendUserToHomeActivity() {
        Intent mainintent = new Intent(SignupActivity.this, Home.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
}