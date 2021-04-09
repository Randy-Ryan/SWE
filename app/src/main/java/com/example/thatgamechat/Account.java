package com.example.thatgamechat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Account extends AppCompatActivity {
    EditText friendInputName;
    int balance;
    Switch switch1;
    String friendName;
    String friendName2;
    FirebaseAuth fAuth;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //get this firebase instance
        fAuth = FirebaseAuth.getInstance();

        //get username from firebase account
        final String username = fAuth.getCurrentUser().getDisplayName();

        //loads the current user's balance
        //issue where it takes a few seconds to first load - maybe just pass this variable
        //instead of doing this in the on create
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("Username").toString().equals(username)) {
                                    String s = document.getData().get("Balance").toString();
                                    balance = Integer.parseInt(s);
                                    TextView bal;
                                    bal = findViewById(R.id.account_balance);
                                    bal.setText("Balance: " + balance + " pts");
                                }
                            }
                        } else {
                            Log.v("randytest", "Error getting documents.", task.getException());
                        }
                    }
                });



            //photoUrl =  fAuth.getCurrentUser().getPhotoUrl();
            //uid =  fAuth.getCurrentUser().getUid();
            String email =  fAuth.getCurrentUser().getEmail();
            boolean emailVerified =  fAuth.getCurrentUser().isEmailVerified();

            //set TextView variables
            TextView t1;
            t1 = findViewById(R.id.account_text1);
            t1.setText("Username: " + username);



        //initialize home button
        Button homeButton;
        homeButton = findViewById(R.id.account_homebutton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //route to home
                startActivity(new Intent(getApplicationContext(), Home.class));
            }
        });

        switch1 = findViewById(R.id.account_switch1);
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("Username").toString().equals(username)) {
                                    String s = document.getData().get("Online").toString();
                                    if(s.equals("true")){
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        switch1.setChecked(true);
                                        db.collection("users").document(document.getId()).update("Online", "true");
                                    }
                                    else{
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("users").document(document.getId()).update("Online", "false");
                                        switch1.setChecked(false);
                                    }
                                }
                            }
                        } else {
                            Log.v("randytest", "Error getting documents.", task.getException());
                        }
                    }
                });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (document.getData().get("Username").toString().equals(username)) {
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("users").document(document.getId()).update("Online", "true");
                                            }
                                        }
                                    } else {
                                        Log.v("randytest", "Error getting documents.", task.getException());
                                    }
                                }
                            });
                }
                else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (document.getData().get("Username").toString().equals(username)) {
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("users").document(document.getId()).update("Online", "false");
                                            }
                                        }
                                    } else {
                                        Log.v("randytest", "Error getting documents.", task.getException());
                                    }
                                }
                            });
                }
            }
        });

        arrayList = new ArrayList<String>();
        //view feed
        ListView feedView = (ListView) findViewById(R.id.feed_view);
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);

        feedView.setAdapter(adapter);

        //this code is very messy and the query can probably be written MUCH easier.
        //display friends list when opening profile
        db.collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                if (document.getData().get("friendName").toString() != null && !document.getData().get("friendName").toString().equals("")) {
                                    if (document.getData().get("Username").toString().equals(username)) {
                                        friendName = document.getData().get("friendName").toString();
                                        Log.v("randytest1", "" + friendName);
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("users")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            //seems like theres an issue when seeting the friendName variable
                                                            //took me a while testing and I couldnt figure it out
                                                            //only works for one profile and then the adapter screws up after that
                                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                                friendName2 = document.getData().get("Username").toString();
                                                                if (document.getData().get("Username").toString().equals(friendName)) {
                                                                    if (document.getData().get("Online").toString().equals("true")) {
                                                                        Log.v("randytest", "" + document.getData().get("Username").toString());
                                                                        adapter.add("Your friend: " + document.getData().get("Username").toString() + " is Online");
                                                                    }
                                                                    else if (document.getData().get("Online").toString().equals("false")){
                                                                        Log.v("randytest", "" + document.getData().get("Username").toString());
                                                                        adapter.add("Your friend: " + document.getData().get("Username").toString() + " is Offline");
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                        );
                                    }
                                }
                            }
                        }
                    }
                });

        //input text
        friendInputName = findViewById(R.id.friend_name);


        //add a friend  button
        Button buttonTest = findViewById(R.id.test1);
        if (!friendInputName.getText().toString().equals("")) {
            buttonTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> user = new HashMap<>();
                    user.put("friendName", friendInputName.getText().toString());
                    db.collection("users").document(username).collection("friends")
                            .add(user);


//
//                            .get()
//                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                    if (task.isSuccessful()) {
//                                        for (QueryDocumentSnapshot document : task.getResult()) {
//                                            if (document.getData().get("Username").toString().equals(username)) {
//                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                                FirebaseUser fUser = fAuth.getCurrentUser();
//                                                Map<String, Object> user = new HashMap<>();
//                                                user.put("Username", username);
//                                                user.put("userID", fUser.getUid());
//                                                user.put("friendName", friendInputName.getText().toString());
//
//
//                                                // Add a new document with a generated ID
//                                                //creating new data instance in cloud firestore
//                                                db.collection("users").get
//                                                        .add(user)
//                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                                            @Override
//                                                            public void onSuccess(DocumentReference documentReference) {
//                                                                Log.v("randytest", "DocumentSnapshot added with ID: " + documentReference.getId());
//                                                            }
//                                                        })
//                                                        .addOnFailureListener(new OnFailureListener() {
//                                                            @Override
//                                                            public void onFailure(@NonNull Exception e) {
//                                                                Log.v("randytest", "Error adding document", e);
//                                                            }
//                                                        });
//                                            }
//                                        }
//                                    } else {
//                                        Log.v("randytest", "Error getting documents.", task.getException());
//                                    }
//                                }
//                            });
//                }
//            });

                }
            });
        }
    }
}
