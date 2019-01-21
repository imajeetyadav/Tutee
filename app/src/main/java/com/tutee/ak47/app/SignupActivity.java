package com.tutee.ak47.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private EditText UserName;
    private EditText UserEmail;
    private EditText UserPhone;
    private EditText UserPassword;

    // Sample email  is  for  custom  organization
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[a-zA-Z0-9_.+-]+@(?:(?:[a-zA-Z0-9-]+\\.)?[a-zA-Z]+\\.)?(gla\\.ac)\\.in$", Pattern.CASE_INSENSITIVE);



    private Button loginButton;
    private Button signupButton;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference rootRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tutee.ak47.app.R.layout.activity_signup);

        InitializeFields();

        //Firebase
        mAuth = FirebaseAuth.getInstance();
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
        UserName = findViewById(com.tutee.ak47.app.R.id.input_name);
        UserEmail = findViewById(com.tutee.ak47.app.R.id.input_email);
        UserPhone = findViewById(com.tutee.ak47.app.R.id.input_phone);
        UserPassword = findViewById(com.tutee.ak47.app.R.id.input_password);
        loginButton = findViewById(com.tutee.ak47.app.R.id.btn_login);
        signupButton = findViewById(com.tutee.ak47.app.R.id.link_signup);
        loadingBar = new ProgressDialog(this);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        Matcher matcher=VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return (!TextUtils.isEmpty(email) &&  matcher.find());
    }

    void createNewAccount() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        final String name = UserName.getText().toString();
        final String phone = UserPhone.getText().toString();


         if (TextUtils.isEmpty(name) && name.endsWith(" ")) {
            UserName.setError("Enter Name");
        }
        if (TextUtils.isEmpty(phone) ) {
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
                                sendVerificationEmail();
                                String currentUserID=mAuth.getCurrentUser().getUid();
                                HashMap<String,String> profileMap=new HashMap<>();
                                //profileMap.put("uid",currentUserID);
                                profileMap.put("name",name);
                                profileMap.put("mobile",phone);
                                profileMap.put("numberStatus","false");
                                rootRef.child("Users").child(currentUserID).setValue(profileMap);


                            } else {
                                    //SignUp Fail
                                String message = task.getException().toString();
                                Toast.makeText(SignupActivity.this, "Error : " + message, Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();

                            }

                        }
                    });

        }

    }

    private void sendVerificationEmail() {
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        currentUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            // after email is sent just logout the user and finish this activity
                            Toast.makeText(SignupActivity.this, "Email Sent.... \n Verify Your Email id ", Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                            sendUserToLoginActivity();
                            loadingBar.dismiss();
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do

                            //restart this activity

                            String message = task.getException().toString();
                            Toast.makeText(SignupActivity.this, "Error : User Already Exist or " + message, Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();


                        }
                    }
                });
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(SignupActivity.this, Home.class);

        startActivity(loginIntent);

    }

    private void sendUserToHomeActivity() {
        Intent mainintent = new Intent(SignupActivity.this, Home.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }
}