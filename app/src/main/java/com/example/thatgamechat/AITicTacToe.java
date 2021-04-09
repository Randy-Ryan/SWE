package com.example.thatgamechat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.thatgamechat.R.id.board;

public class AITicTacToe extends AppCompatActivity {

    private BoardView boardView;
    private Board gameEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_i_tic_tac_toe);
        boardView = (BoardView) findViewById(board);
        gameEngine = new Board();
        boardView.setGameEngine(gameEngine);
        boardView.setMainActivity(this);

        Button homeButton;
        homeButton = findViewById(R.id.ttt_homebutton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Home.class));
            }
        });
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.layout.activity_a_i_tic_tac_toe, layout);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new_game) {
            newGame();
        }

        return super.onOptionsItemSelected(item);
    }

    public void gameEnded(char c) {
        String msg = (c == 'T') ? "Game Ended. Tie" : "GameEnded. " + c + " win";

        new AlertDialog.Builder(this).setTitle("Tic Tac Toe").
                setMessage(msg).
                setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        newGame();
                    }
                }).show();
    }

    private void newGame() {
        gameEngine.newGame();
        boardView.invalidate();
    }

}
