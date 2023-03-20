package com.drawin.panchylime.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


import com.drawin.panchylime.R;
import com.drawin.panchylime.games.tictactoe.TicTacToeActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * @author Draco0503
 */
public class GameDialog extends Dialog implements View.OnClickListener {

    // ATTRIBUTES
    private Activity _parent;
    private Activity _grandParent;

    // LAYOUT VIEWS
    private TextView _tvInfo;
    private TextView _tvCoins;
    private ImageView _OK;
    private ImageView _RESTART;

    private int _msg;
    private int _color;
    private String _coins;
    private String _username;
    private String _pet;

    /**
     * Constructor to redirect to the "parent" activity when "exit"
     * @param parent the caller and where is going to be sent if exit option
     * @param msg display info message
     * @param color color of the message
     * @param coins the coins won
     * @param username players username
     */
    public GameDialog(Activity parent, int msg, int color, int coins, String username) {
        super(parent);
        this._parent = parent;
        this._grandParent = null;
        _msg = msg;
        _coins = "+ " + coins;
        _color = color;
        _username = username;
        _pet = null;
    }

    /**
     * Constructor to redirect to the "grandParent" activity when "exit"
     * @param parent the caller
     * @param grandParent where is going to be sent if exit option
     * @param msg display info message
     * @param color color of the message
     * @param coins the coins won
     * @param username players username
     * @param pet for restart purpose
     */
    public GameDialog(Activity parent, Activity grandParent, int msg, int color, int coins, String username, String pet) {
        super(parent);
        this._parent = parent;
        this._grandParent = grandParent;
        _msg = msg;
        _coins = "+ " + coins;
        _color = color;
        _username = username;
        _pet = pet;
    }

    /**
     * Is called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_dialog);

        _tvInfo = findViewById(R.id.gameInfo);
        _tvInfo.setTextColor(_color);
        _tvInfo.setText(_msg);

        _tvCoins = findViewById(R.id.coinsInfo);
        _tvCoins.setText(_coins);

        _RESTART = findViewById(R.id.ivRestart);
        _RESTART.setOnClickListener(this);

        _OK = findViewById(R.id.ivContinue);
        _OK.setOnClickListener(this);

    }

    /**
     * What the program is going to do whatever button is pressed, RESTART or OK
     * @param view the caller
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivRestart:
                _parent.finish();
                Intent reset_game;
                if (_grandParent == null) {
                    reset_game = new Intent(_parent, TicTacToeActivity.class);
                } else {
                    reset_game = new Intent(_parent, _grandParent.getClass());
                    reset_game.putExtra("pet", _pet);
                }
                reset_game.putExtra("username", _username);
                _parent.startActivity(reset_game);
                dismiss();
                break;
            case R.id.ivContinue:
                _parent.finish();
                break;
        }
        dismiss();
    }
}
