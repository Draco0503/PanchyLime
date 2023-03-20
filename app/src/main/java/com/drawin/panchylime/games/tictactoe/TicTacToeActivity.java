package com.drawin.panchylime.games.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.drawin.panchylime.R;
import com.drawin.panchylime.dialogs.GameDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Jaekwin, Draco0503
 */
public class TicTacToeActivity extends AppCompatActivity implements Serializable {
    // IMPORTANT!! THE OPPONENT DOES NOT HAVE INTELLIGENCE ITS A SIMPLE RANDOM
    // ATTRIBUTES
    // This array contains the ids of all the buttons
    private final Integer[] BUTTONS = new Integer[] {
            R.id.ttt0, R.id.ttt1, R.id.ttt2,
            R.id.ttt3, R.id.ttt4, R.id.ttt5,
            R.id.ttt6, R.id.ttt7, R.id.ttt8
    };
    private int[] _board;
    private int[] _winnerPosition;

    // STATUS = { 1 WIN, -1 LOSE, 2 DRAW }
    private int _status;
    private int _itemCount;
    private int _turn;

    private String _username;

    /**
     * Is called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);
        init();
    }

    /**
     * Initialize all the variables needed
     */
    private void init() {
        if (getIntent().getExtras() != null) {
            _username = getIntent().getExtras().getString("username");
        }
        _board = new int[]{
                0, 0, 0,
                0, 0, 0,
                0, 0, 0
        };
        _status = 0;
        _itemCount = 0;
        _turn = 1;
        _winnerPosition = new int[]{
                -1, -1, -1
        };
        resetButtons();
    }

    /**
     * Resets all the images from the buttons
     */
    private void resetButtons() {
        for (Integer button : BUTTONS) {
            ImageView b = findViewById(button);
            b.setBackgroundResource(0);
        }
    }

    /**
     * The action when the player clicks into the button v
     * @param v the caller
     */
    public void placeItemOnBoard(View v){
        if(_status == 0) {
            _turn = 1;
            int numBoton = Arrays.asList(BUTTONS).indexOf(v.getId());
            if(_board[numBoton] == 0) {
                v.setBackgroundResource(R.drawable.ttt_o);
                _board[numBoton] = 1;
                _itemCount += 1;
                _status = checkStatus();
                if (_status == 0) {
                    _turn = -1;
                    opponentsMove();
                    _itemCount += 1;
                    _status = checkStatus();
                }
                endGame();
            }
        }
    }

    /**
     * The opponents move, this has no "brain" its just a random number
     */
    private void opponentsMove(){
        Random ran = new Random();
        int pos = ran.nextInt(_board.length);

        while (_board[pos] != 0){
            pos = ran.nextInt(_board.length);
        }
        ImageView iv = (ImageView) findViewById(BUTTONS[pos]);
        iv.setBackgroundResource(R.drawable.ttt_x);
        _board[pos] = -1;
    }

    /**
     * Checks every move to see if someone has win or if its a draw
     * @return the status of the game; 1 WIN, -1 LOSE, 2 DRAW
     */
    private int checkStatus(){
        int newStatus = 0;
        if(Math.abs(_board[0]+ _board[1]+ _board[2]) == 3){
           /*
            x, x, x,
            0, 0, 0,
            0, 0, 0
            */
            _winnerPosition = new int[]{0,1,2};
            newStatus = _turn;
        }else if(Math.abs(_board[3]+ _board[4]+ _board[5]) == 3){
           /*
            0, 0, 0,
            x, x, x,
            0, 0, 0
            */
            _winnerPosition = new int[]{3,4,5};
            newStatus = _turn;
        }else if(Math.abs(_board[6]+ _board[7]+ _board[8]) == 3){
           /*
            0, 0, 0,
            0, 0, 0,
            x, x, x
            */
            _winnerPosition = new int[]{6,7,8};
            newStatus = _turn;
        }else if(Math.abs(_board[0]+ _board[3]+ _board[6]) == 3){
           /*
            x, 0, 0,
            x, 0, 0,
            x, 0, 0
            */
            _winnerPosition = new int[]{0,3,6};
            newStatus = _turn;
        }else if(Math.abs(_board[1]+ _board[4]+ _board[7]) == 3){
           /*
            0, x, 0,
            0, x, 0,
            0, x, 0
            */
            _winnerPosition = new int[]{1,4,7};
            newStatus = _turn;
        }else if(Math.abs(_board[2]+ _board[5]+ _board[8]) == 3){
           /*
            0, 0, x,
            0, 0, x,
            0, 0, x
            */
            _winnerPosition = new int[]{2,5,8};
            newStatus = _turn;
        }else if(Math.abs(_board[0]+ _board[4]+ _board[8]) == 3){
           /*
            x, 0, 0,
            0, x, 0,
            0, 0, x
            */
            _winnerPosition = new int[]{0,4,8};
            newStatus = _turn;
        }else if(Math.abs(_board[2]+ _board[4]+ _board[6]) == 3){
           /*
            0, 0, x,
            0, x, 0,
            x, 0, 0
            */
            _winnerPosition = new int[]{2,4,6};
            newStatus = _turn;
        }else if(_itemCount == 9){
            newStatus = 2;
        }
        return newStatus;
    }

    /**
     * Actions when the game ends, if win 1 coin, 0 if not
     */
    private void endGame(){
        GameDialog dialog;
        if(_status == 1 || _status == -1){
            int winner;
            if(_status == 1){
                winner = R.drawable.ttt_win;
                dialog = new GameDialog(this, R.string.game_2_win, Color.GREEN, 1, _username);
                addCoins();
            }else{
                dialog = new GameDialog(this, R.string.game_2_lose, Color.RED,  0, _username);
                winner = R.drawable.ttt_lose;
            }
            dialog.show();
            for (int j : _winnerPosition) {
                ImageView b = findViewById(BUTTONS[j]);
                b.setBackgroundResource(winner);
            }
        }else if(_status == 2){
            dialog = new GameDialog(this, R.string.game_2_draw, Color.YELLOW, 0, _username);
            dialog.show();
        }
    }

    /**
     * Add 1 coin to the user
     */
    private void addCoins() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(_username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Object> postValues = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    postValues.put(snapshot.getKey(),snapshot.getValue());
                }
                assert postValues.get("_coins") != null;
                long lastCoins = (long) postValues.get("_coins");
                postValues.put("_coins", lastCoins + 1);
                ref.child("users").child(_username).updateChildren(postValues);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("addCoins", error.getMessage());
            }
        });
    }

}