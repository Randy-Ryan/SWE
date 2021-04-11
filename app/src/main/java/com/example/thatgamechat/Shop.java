package com.example.thatgamechat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Shop extends AppCompatActivity {
    int balance;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        fAuth = FirebaseAuth.getInstance();
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
                                    TextView balanceText;
                                    balanceText = findViewById(R.id.shop_balance);
                                    balanceText.setText(username + "'s balance is: " + balance + " pts");
                                    Log.v("randytest", "" +  s);
                                    Log.v("randytest", "" + Integer.parseInt(s));
                                }
                            }
                        } else {
                            Log.w("randytest", "Error getting documents.", task.getException());
                        }
                    }
                });


        //home button
        Button homeButton;
        homeButton = findViewById(R.id.shop_homebutton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Home.class));
            }
        });


        //accessory 1
        Button button1;
        button1 = findViewById(R.id.shop_button1);

        //linked with 25 pt button
        //remove 25 pts from balance and give user accessory 2
        //still need to implement giving the accessory to user
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (balance>25) {
                    balance = balance - 25;
                    TextView balanceText;
                    balanceText = findViewById(R.id.shop_balance);
                    findViewById(R.id.shop).setBackgroundColor(Color.BLUE);
                    balanceText.setText(username + "'s balance is: " + balance + " pts");
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
                                                db.collection("users").document(document.getId()).update("Balance", balance);
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

        //accessory 2
        Button button2;
        button2 = findViewById(R.id.shop_button2);
        Log.v("testtest1", "" + button2.isShown());

        //linked with 50 pt button
        //remove 50 pts from balance and give user accessory 1
        //still need to implement giving the accessory to user
        //check if user has enough balance
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (balance > 50) {
                    balance = balance - 50;
                    findViewById(R.id.shop).setBackgroundColor(Color.YELLOW);
                    TextView balanceText;
                    balanceText = findViewById(R.id.shop_balance);
                    balanceText.setText(username + "'s balance is: " + balance + " pts");
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
                                                db.collection("users").document(document.getId()).update("Balance", balance);
                                            }
                                        }
                                    } else {
                                        Log.w("randytest", "Error getting documents.", task.getException());
                                    }
                                }
                            });
                }
            }
        });

    }
    }

