package com.example.thatgamechat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TicTacToe extends AppCompatActivity {
    TextView status;
    String s;
    String otherUsername = "";
    FirebaseAuth fAuth;
    ArrayList list;
    boolean gameActive = false;
    Map<String, Object> game;
    String myUsername;
    String myID;
    int balance;
    boolean myTurn = true;
    private Timer timer;

    Button receiveUpdateButton;
    // Player representation
    // 0 - X
    // 1 - O
    int activePlayer = 2;
    int[] gameState = {2, 2, 2, 2, 2, 2, 2, 2, 2};

    // State meanings:
    //    0 - X
    //    1 - O
    //    2 - Null
    // put all win positions in a 2D array
    int[][] winPositions = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}};
    public static int counter = 0;

    // this function will be called every time a
    // players tap in an empty box of the grid
    public void playerTap(View view) {
        ImageView img = (ImageView) view;
        int tappedImage = Integer.parseInt(img.getTag().toString());

        // game reset function will be called
        // if someone wins or the boxes are full
//        if (!gameActive) {
//            gameReset(view);
//        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("games")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("whoseTurn").equals(myUsername)) {
                                    myTurn = true;
                                } else {
                                    myTurn = false;
                                }
                            }
                        }
                    }
                });


        // if the tapped image is empty
        if (gameState[tappedImage] == 2 && myTurn) {
            // increase the counter
            // after every tap
            counter++;
            myTurn = false;

            //check if status == my turn through firebase

            db.collection("games").document("game1").update("whoseTurn", otherUsername);

            gameState[tappedImage] = activePlayer;

            // this will give a motion
            // effect to the image
            img.setTranslationY(-1000f);


            list = new ArrayList<>();
            for (int i = 0; i < gameState.length; i++) {
                list.add(gameState[i]);
            }
            db.collection("games").document("game1").update("moves", list);
            //

            // check if its the last box
//            if (counter == 9) {
//                // reset the game
//                gameActive = false;
//            }

            // mark this position


            // change the active player
            // from 0 to 1 or 1 to 0
            if (activePlayer == 0) {
                // set the image of x
                img.setImageResource(R.drawable.x);
                //  activePlayer = 1;
                TextView status = findViewById(R.id.status);

                // change the status
                // status.setText("O's Turn - Tap to play");
            } else if (activePlayer == 1) {
                // set the image of o
                img.setImageResource(R.drawable.o);
                //    activePlayer = 0;
                TextView status = findViewById(R.id.status);

                // change the status
                // status.setText("X's Turn - Tap to play");
            }

            img.animate().translationYBy(1000f).setDuration(300);
        }


        int flag = 0;
        // Check if any player has won
        for (int[] winPosition : winPositions) {
            if (gameState[winPosition[0]] == gameState[winPosition[1]] &&
                    gameState[winPosition[1]] == gameState[winPosition[2]] &&
                    gameState[winPosition[0]] != 2) {
                flag = 1;

                // Somebody has won! - Find out who!
                String winnerStr;

                // game reset function be called
                // gameActive = false;
                if (gameState[winPosition[0]] == 0) {
                    winnerStr = "X has won";
                    Log.v("randytest1", "bal: " + balance);
                    if (activePlayer == 0) {
                        db.collection("users")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document.getData().get("Username").toString().equals(myUsername)) {
                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                    Log.v("randytest", "bal: " + balance);
                                                    balance = Integer.parseInt(document.getData().get("Balance").toString());
                                                    db.collection("users").document(document.getId()).update("Balance", balance + 50);
                                                }
                                            }
                                        } else {
                                            Log.w("randytest", "Error getting documents.", task.getException());
                                        }
                                    }
                                });
                    }
                    //if this is me - add balance
                } else {
                    winnerStr = "O has won";
                    Log.v("randytest1", "bal: " + balance);
                    //update this users balance
                    if (activePlayer == 1) {
                        db.collection("users")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document.getData().get("Username").toString().equals(myUsername)) {
                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                    Log.v("randytest", "bal: " + balance);
                                                    balance = Integer.parseInt(document.getData().get("Balance").toString());
                                                    db.collection("users").document(document.getId()).update("Balance", balance + 50);
                                                }
                                            }
                                        } else {
                                            Log.w("randytest", "Error getting documents.", task.getException());
                                        }
                                    }
                                });
                    }
                }
                //if this is me - add balance
                // Update the status bar for winner announcement
                TextView status = findViewById(R.id.status);
                status.setText(winnerStr);
            }
        }
        // set the status if the match draw
        if (counter == 9 && flag == 0) {
            TextView status = findViewById(R.id.status);
            status.setText("Match Draw");
        }
    }

    // reset the game
    public void gameReset(View view) {
        gameActive = true;
        activePlayer = 0;
        for (int i = 0; i < gameState.length; i++) {
            gameState[i] = 2;
        }
        // remove all the images from the boxes inside the grid
        ((ImageView) findViewById(R.id.imageView0)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView1)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView2)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView3)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView4)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView5)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView6)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView7)).setImageResource(0);
        ((ImageView) findViewById(R.id.imageView8)).setImageResource(0);

        status.setText("X's Turn - Tap to play");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tictactoe);
        status = findViewById(R.id.status);
        //home button
        Button homeButton;

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        receiveUpdateButton.performClick();
                    }
                });
            }
        }, 30000, 5000);

        homeButton = findViewById(R.id.ttt_homebutton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Home.class));
            }
        });


        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = fAuth.getCurrentUser();
        myID = fUser.getUid();


//        //create a firestore tic tac toe game
        game = new HashMap<>();
        game.put("gameID", "123431233431");
        game.put("finished", "false");
        game.put("moves", list);
        game.put("whoseTurn", "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getData().get("userID").equals(myID)) {
                                    myUsername = document.getData().get("Username").toString();
                                }
                            }
                        }
                    }
                });


        //db.collection("games").document("game1").set(game);
        receiveUpdateButton = findViewById(R.id.ttt_receive_updateButton);

        receiveUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update status
                //  status.setText("Updating");
                // myTurn = true;

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("games")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.getData().get("player1ID").equals(myUsername)) {
                                            otherUsername = document.getData().get("player2ID").toString();
                                            activePlayer = 1;
                                        } else if (document.getData().get("player2ID").equals(myUsername)) {
                                            otherUsername = document.getData().get("player1ID").toString();
                                            activePlayer = 0;
                                        }
                                        gameActive = true;

                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        s = document.getData().get("moves").toString();
                                        s = s.replace(" ", "")
                                                .replace(",", "")
                                                .replace("[", "")
                                                .replace("]", "");

                                        status.setText("" + document.getData().get("whoseTurn").toString() + " move");

                                        for (int i = 0; i < gameState.length; i++) {
                                            String test = String.valueOf(s.charAt(i));
                                            gameState[i] = Integer.parseInt(test);
                                        }
                                        if (s.charAt(0) == '2') {
                                            ((ImageView) findViewById(R.id.imageView0)).setImageResource(0);
                                        }
                                        if (s.charAt(1) == '2') {
                                            ((ImageView) findViewById(R.id.imageView1)).setImageResource(0);
                                        }
                                        if (s.charAt(2) == '2') {
                                            ((ImageView) findViewById(R.id.imageView2)).setImageResource(0);
                                        }
                                        if (s.charAt(3) == '2') {
                                            ((ImageView) findViewById(R.id.imageView3)).setImageResource(0);
                                        }
                                        if (s.charAt(4) == '2') {
                                            ((ImageView) findViewById(R.id.imageView4)).setImageResource(0);
                                        }
                                        if (s.charAt(5) == '2') {
                                            ((ImageView) findViewById(R.id.imageView5)).setImageResource(0);
                                        }
                                        if (s.charAt(6) == '2') {
                                            ((ImageView) findViewById(R.id.imageView6)).setImageResource(0);
                                        }
                                        if (s.charAt(7) == '2') {
                                            ((ImageView) findViewById(R.id.imageView7)).setImageResource(0);
                                        }
                                        if (s.charAt(8) == '2') {
                                            ((ImageView) findViewById(R.id.imageView8)).setImageResource(0);
                                        }
                                        if (s.charAt(0) == '1') {
                                            ((ImageView) findViewById(R.id.imageView0)).setImageResource(R.drawable.o);
                                        }
                                        if (s.charAt(1) == '1') {
                                            ((ImageView) findViewById(R.id.imageView1)).setImageResource(R.drawable.o);
                                        }
                                        if (s.charAt(2) == '1') {
                                            ((ImageView) findViewById(R.id.imageView2)).setImageResource(R.drawable.o);
                                        }
                                        if (s.charAt(3) == '1') {
                                            ((ImageView) findViewById(R.id.imageView3)).setImageResource(R.drawable.o);
                                        }
                                        if (s.charAt(4) == '1') {
                                            ((ImageView) findViewById(R.id.imageView4)).setImageResource(R.drawable.o);
                                        }
                                        if (s.charAt(5) == '1') {
                                            ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.o);
                                        }
                                        if (s.charAt(6) == '1') {
                                            ((ImageView) findViewById(R.id.imageView6)).setImageResource(R.drawable.o);
                                        }
                                        if (s.charAt(7) == '1') {
                                            ((ImageView) findViewById(R.id.imageView7)).setImageResource(R.drawable.o);
                                        }
                                        if (s.charAt(8) == '1') {
                                            ((ImageView) findViewById(R.id.imageView8)).setImageResource(R.drawable.o);
                                        }
                                        if (s.charAt(0) == '0') {
                                            ((ImageView) findViewById(R.id.imageView0)).setImageResource(R.drawable.x);
                                        }
                                        if (s.charAt(1) == '0') {
                                            ((ImageView) findViewById(R.id.imageView1)).setImageResource(R.drawable.x);
                                        }
                                        if (s.charAt(2) == '0') {
                                            ((ImageView) findViewById(R.id.imageView2)).setImageResource(R.drawable.x);
                                        }
                                        if (s.charAt(3) == '0') {
                                            ((ImageView) findViewById(R.id.imageView3)).setImageResource(R.drawable.x);
                                        }
                                        if (s.charAt(4) == '0') {
                                            ((ImageView) findViewById(R.id.imageView4)).setImageResource(R.drawable.x);
                                        }
                                        if (s.charAt(5) == '0') {
                                            ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.x);
                                        }
                                        if (s.charAt(6) == '0') {
                                            ((ImageView) findViewById(R.id.imageView6)).setImageResource(R.drawable.x);
                                        }
                                        if (s.charAt(7) == '0') {
                                            ((ImageView) findViewById(R.id.imageView7)).setImageResource(R.drawable.x);
                                        }
                                        if (s.charAt(8) == '0') {
                                            ((ImageView) findViewById(R.id.imageView8)).setImageResource(R.drawable.x);
                                        }
                                    }
                                }
                            }
                        });
            }
        });


        Button startButton;
        startButton = findViewById(R.id.ttt_startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update status

                status.setText("Searching for game...");
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("games").document("game1").set(game);

                list = new ArrayList<>();
                for (int i = 0; i < gameState.length; i++) {
                    list.add(gameState[i]);
                }

                db.collection("games").document("game1").update("moves", list);
                db.collection("games").document("game1").update("player1ID", myUsername);
                activePlayer = 1;
                //update status when game found


            }
        });


        Button joinButton;
        joinButton = findViewById(R.id.ttt_joinButton);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a firestore tic tac toe game
                //update status

                status.setText("Joining a game...");
                gameActive = true;
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("games").document("game1").update("player2ID", myUsername);

                db.collection("games")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        if (document.getData().get("player1ID").equals(myUsername)) {
                                            otherUsername = document.getData().get("player2ID").toString();
                                            activePlayer = 1;
                                        } else if (document.getData().get("player2ID").equals(myUsername)) {
                                            otherUsername = document.getData().get("player1ID").toString();
                                            activePlayer = 0;
                                        }
                                        db.collection("games").document("game1").update("whoseTurn", otherUsername);

//                                        }
                                    }
                                }

                            }

                        });


            }
        });
    }

    }