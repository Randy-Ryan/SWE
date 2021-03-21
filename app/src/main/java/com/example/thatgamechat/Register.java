package com.example.thatgamechat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";

    //initialize class variables
    EditText mFullName, mEmail, mPassword;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    String userID;



    //NOTE: We should fix the input types in the xml files so that password
    // is hidden when user types and make the filler text fields disappear

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //link class variables with layout display variables in xml
        mFullName   = findViewById(R.id.register_fullName);
        mEmail      = findViewById(R.id.register_email);
        mPassword   = findViewById(R.id.register_password);
        mRegisterBtn= findViewById(R.id.register_button);
        mLoginBtn   = findViewById(R.id.register_text2);

        fAuth = FirebaseAuth.getInstance();

        //checks if user is already signed in
        //if so, goes directly to home page & ends this activity
        //don't need this at the moment for testing purposes
//        if(fAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(), Home.class));
//            finish();
//        }


        //when create account button is clicked
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                final String fullName = mFullName.getText().toString();


                //check if fields are empty and display the errors

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required for registration.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required for registration.");
                    return;
                }

                if(TextUtils.isEmpty(fullName)){
                    mPassword.setError("Full name is required for registration.");
                    return;
                }

                //check if password is "strong"
                //can further implement this password requirement check
                if(password.length() < 6){
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }


                // register the user in firebase
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // send verification link
                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                }
                            });

                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            //DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("fName",fullName);
                            user.put("email",email);


                            //route to home page after registration is complete
                            //should we add a loading screen to route to in between pages
                            //loading screen could become useful for future implementations
                            startActivity(new Intent(getApplicationContext(), Home.class));

                        }else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        //routes to login page
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

    }
}