package com.example.thatgamechat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Account extends AppCompatActivity {
    EditText friendInputName;
    int balance;
    Switch switch1;
    ListView feedView;
    List<String> friendList;
    String friendID;
    FirebaseAuth fAuth;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    String userID;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //get this firebase instance
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();
        //get user ID from firebase account
        userID = fUser.getUid();
        //get username from firebase account
        username = fAuth.getCurrentUser().getDisplayName();
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

        //set TextView variables
        TextView t1;
        t1 = findViewById(R.id.account_text1);
        t1.setText("Username: " + username);

        //initialize sign out button
        Button signOutButton;
        signOutButton = findViewById(R.id.account_signout_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
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
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });


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
                                    if (s.equals("true")) {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        switch1.setChecked(true);
                                        db.collection("users").document(document.getId()).update("Online", "true");
                                    } else {
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
                if (isChecked) {
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
                } else {
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
        feedView = (ListView) findViewById(R.id.feed_view);
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        friendList = new ArrayList<String>();
        feedView.setAdapter(adapter);

        feedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = feedView.getItemAtPosition(position);
                String s = listItem.toString().replace("Your friend: ","")
                        .replace(" is Offline","")
                        .replace(" is Online","");
                Log.v("randytest", "" + s);
                //start private messaging here

                //pass private channelID
                Intent myIntent = new Intent(feedView.getContext(), Messages.class);
                myIntent.putExtra("firstKeyName","OxexCGkvOVKDcjIv");
                startActivity(myIntent);
            }
        });

        //display friends list when opening profile
        //maybe add a refresh check for every 10 sec ago to update friends online
        db.collection("users").document(fUser.getUid()).collection("friends")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.v("randy", ""+ document.getData().get("friendName").toString());
                                friendList.add(document.getData().get("friendName").toString());
                            }
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                    Log.v("randy", ""+ friendList);
                                                    for (int i = 0; i < friendList.size(); i++) {
                                                        Log.v("randy", ""+ document.getData().get("Online").toString());
                                                        if (document.getData().get("Username").toString().equals(friendList.get(i))) {
                                                            if(document.getData().get("Online").toString().equals("true")) {
                                                                adapter.add("Your friend: " + friendList.get(i) + " is Online");
                                                            }
                                                            else{
                                                                adapter.add("Your friend: " + friendList.get(i) + " is Offline");
                                                            }
                                                            }
                                                        }
                                                    }

                                                }

                                        }
                                    });
                        }
                    }
                });

        //add a friend  button
        Button buttonTest = findViewById(R.id.test1);
        friendInputName = findViewById(R.id.friend_name);
            buttonTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseUser fUser = fAuth.getCurrentUser();
                    db.collection("users")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (document.getData().get("Username").toString().equals(friendInputName.getText().toString())) {
                                                friendID = document.getData().get("userID").toString();
                                                Map<String, Object> user = new HashMap<>();
                                                user.put("friendName", friendInputName.getText().toString());
                                                user.put("friendID", friendID);
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("users").document(userID)
                                                        .collection("friends")
                                                        .add(user);

                                            }
                                        }
                                    } else {
                                        Log.v("randytest", "Error getting documents.", task.getException());
                                    }
                                }
                            });





                }
            });
        }

}
